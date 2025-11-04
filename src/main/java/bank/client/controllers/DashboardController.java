package bank.client.controllers;

import bank.client.ServerConfig;
import bank.interfaces.BankInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.rmi.Naming;

public class DashboardController {

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblBalance;

    @FXML
    private TableView<?> tblTransactions;

    private String currentUser;
    private BankInterface bankService;

    public void setUser(String username) {
        this.currentUser = username;
        lblUsername.setText("Tài khoản: " + username);

        // Thử kết nối với server 1 trước, nếu lỗi thì dùng server 2 (failover)
        connectToServer(username);
        
        // Thêm listener để tự động refresh khi window được focus lại
        // Dùng Platform.runLater để đợi Scene được attach vào Stage
        Platform.runLater(() -> setupAutoRefresh());
    }
    
    /**
     * Thiết lập tự động refresh khi window được focus
     */
    private void setupAutoRefresh() {
        try {
            // Kiểm tra Scene đã được attach chưa
            if (lblUsername.getScene() == null) {
                // Chưa attach, thử lại sau
                Platform.runLater(() -> setupAutoRefresh());
                return;
            }
            
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            if (stage != null) {
                // Refresh khi window được focus lại
                stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (isNowFocused && !wasFocused && currentUser != null) {
                        // Window vừa được focus lại, refresh số dư
                        System.out.println("🔄 Window được focus lại, tự động làm mới số dư...");
                        refreshBalance();
                    }
                });
                System.out.println("✅ Đã setup auto refresh thành công!");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Không thể setup auto refresh: " + e.getMessage());
            // Không throw exception, chỉ log warning
        }
    }
    
    /**
     * Kết nối đến server với failover support
     * Thử server 1 trước, nếu lỗi thì dùng server 2
     */
    private void connectToServer(String username) {
        try {
            // Thử kết nối Server 1 trước
            String url1 = ServerConfig.getInstance().getServer1URL();
            System.out.println("🔗 Đang kết nối Server 1: " + url1);
            
            bankService = (BankInterface) Naming.lookup(url1);
            double balance = bankService.getBalance(username);
            lblBalance.setText(String.format("%.0f VND", balance));
            
            System.out.println("✅ Kết nối Server 1 thành công! Số dư: " + balance);
            
        } catch (Exception e1) {
            System.err.println("⚠️ Server 1 không khả dụng: " + e1.getMessage());
            
            // Failover: Thử kết nối Server 2
            try {
                String url2 = ServerConfig.getInstance().getServer2URL();
                System.out.println("🔗 Đang kết nối Server 2 (failover): " + url2);
                
                bankService = (BankInterface) Naming.lookup(url2);
                double balance = bankService.getBalance(username);
                lblBalance.setText(String.format("%.0f VND", balance));
                
                System.out.println("✅ Kết nối Server 2 thành công! Số dư: " + balance);
                
            } catch (Exception e2) {
                System.err.println("❌ Server 2 cũng không khả dụng: " + e2.getMessage());
                lblBalance.setText("❌ Không thể kết nối server");
            }
        }
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transfer.fxml"));
            Parent root = loader.load();

            TransferController controller = loader.getController();
            controller.initData(currentUser, bankService);

            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Chuyển tiền");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Làm mới số dư từ server
     */
    public void refreshBalance() {
        if (currentUser == null || bankService == null) {
            return;
        }
        
        try {
            // Lấy số dư mới từ server
            double newBalance = bankService.getBalance(currentUser);
            lblBalance.setText(String.format("%.0f VND", newBalance));
            System.out.println("🔄 Đã làm mới số dư: " + newBalance + " VND");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi làm mới số dư: " + e.getMessage());
            // Nếu lỗi, thử kết nối lại
            connectToServer(currentUser);
        }
    }
    
    /**
     * Handler cho nút "Làm mới"
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshBalance();
    }
    
    /**
     * Được gọi khi Dashboard được hiển thị
     * Tự động refresh số dư khi quay lại từ màn hình khác
     */
    @FXML
    public void initialize() {
        // initialize() được gọi khi FXML được load
        // Nhưng lúc này currentUser chưa được set, nên không làm gì ở đây
    }
}

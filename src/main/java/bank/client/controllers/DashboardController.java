package bank.client.controllers;

import bank.client.ServerConfig;
import bank.interfaces.BankInterface;
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

    public void refreshBalance() {
        try {
            double newBalance = bankService.getBalance(currentUser);
            lblBalance.setText(newBalance + " VND");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

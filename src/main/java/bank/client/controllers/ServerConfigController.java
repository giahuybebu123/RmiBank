package bank.client.controllers;

import bank.client.ServerConfig;
import bank.interfaces.BankInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.rmi.Naming;

public class ServerConfigController {

    @FXML
    private TextField txtServer1IP;

    @FXML
    private TextField txtServer1Port;

    @FXML
    private TextField txtServer2IP;

    @FXML
    private TextField txtServer2Port;

    @FXML
    public void initialize() {
        // Giá trị mặc định
        txtServer1IP.setText("localhost");
        txtServer1Port.setText("1099");
        txtServer2IP.setText("localhost");
        txtServer2Port.setText("1100");
    }

    @FXML
    private void handleConnect(ActionEvent event) {
        String ip1 = txtServer1IP.getText().trim();
        String port1Str = txtServer1Port.getText().trim();
        String ip2 = txtServer2IP.getText().trim();
        String port2Str = txtServer2Port.getText().trim();

        if (ip1.isEmpty() || port1Str.isEmpty() || ip2.isEmpty() || port2Str.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin 2 server!", AlertType.WARNING);
            return;
        }

        try {
            int port1 = Integer.parseInt(port1Str);
            int port2 = Integer.parseInt(port2Str);
            
            // Lưu vào ServerConfig
            ServerConfig.getInstance().setServerInfo(ip1, port1, ip2, port2);
            
            // Thử kết nối và link 2 server
            showAlert("Đang kết nối...", "Đang kết nối và đồng bộ 2 server...", AlertType.INFORMATION);
            linkServers(ip1, port1, ip2, port2);
            
            // Chuyển sang màn hình login
            loadLoginScreen();
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi định dạng", "Port phải là số nguyên (ví dụ: 1099)", AlertType.ERROR);
        }
    }
    
    /**
     * Tự động kết nối và link 2 server với nhau
     */
    private void linkServers(String ip1, int port1, String ip2, int port2) {
        try {
            String url1 = "rmi://" + ip1 + ":" + port1 + "/BankService";
            String url2 = "rmi://" + ip2 + ":" + port2 + "/BankService";
            
            System.out.println("🔗 Đang kết nối Server 1: " + url1);
            System.out.println("🔗 Đang kết nối Server 2: " + url2);
            
            BankInterface server1 = (BankInterface) Naming.lookup(url1);
            BankInterface server2 = (BankInterface) Naming.lookup(url2);
            
            // Link 2 server với nhau
            server1.setOtherServer(server2);
            server2.setOtherServer(server1);
            
            System.out.println("✅ Đã link 2 server thành công!");
            showAlert("Thành công", "Đã kết nối và đồng bộ 2 server thành công!", AlertType.INFORMATION);
            
        } catch (Exception e) {
            System.err.println("⚠️ Không thể link 2 server: " + e.getMessage());
            System.err.println("   Server vẫn có thể hoạt động độc lập.");
            // Không throw exception - cho phép tiếp tục sử dụng
        }
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtServer1IP.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập - Bank RMI");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải màn hình đăng nhập!", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


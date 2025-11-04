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
        //  THAY ĐỔI IP THỰC TẾ CỦA 2 MÁY Ở ĐÂY
        txtServer1IP.setText("192.168.1.83");     // ← IP máy chính
        txtServer1Port.setText("1099");
        txtServer2IP.setText("192.168.1.183");    // ← IP máy ảo (mới)
        txtServer2Port.setText("1100");
    }

    @FXML
    private void handleTestConnection(ActionEvent event) {
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
            
            StringBuilder result = new StringBuilder();
            result.append("🔍 KẾT QUẢ KIỂM TRA:\n\n");
            
            // Test Server 1
            String url1 = "rmi://" + ip1 + ":" + port1 + "/BankService";
            result.append("📡 Server 1: ").append(url1).append("\n");
            try {
                BankInterface server1 = (BankInterface) Naming.lookup(url1);
                double testBalance = server1.getBalance("A001");
                result.append("   ✅ KẾT NỐI THÀNH CÔNG!\n");
                result.append("   📊 Test: Số dư A001 = ").append(testBalance).append(" VND\n\n");
            } catch (Exception e) {
                result.append("   ❌ KẾT NỐI THẤT BẠI!\n");
                result.append("   📛 Lỗi: ").append(e.getMessage()).append("\n\n");
            }
            
            // Test Server 2
            String url2 = "rmi://" + ip2 + ":" + port2 + "/BankService";
            result.append("📡 Server 2: ").append(url2).append("\n");
            try {
                BankInterface server2 = (BankInterface) Naming.lookup(url2);
                double testBalance = server2.getBalance("A002");
                result.append("   ✅ KẾT NỐI THÀNH CÔNG!\n");
                result.append("   📊 Test: Số dư A002 = ").append(testBalance).append(" VND\n\n");
            } catch (Exception e) {
                result.append("   ❌ KẾT NỐI THẤT BẠI!\n");
                result.append("   📛 Lỗi: ").append(e.getMessage()).append("\n\n");
            }
            
            result.append("💡 Nếu cả 2 đều ✅, click 'Kết nối và Đồng bộ'!");
            
            showAlert("Test Connection", result.toString(), AlertType.INFORMATION);
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi định dạng", "Port phải là số nguyên (ví dụ: 1099)", AlertType.ERROR);
        }
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
            
            // Chạy kết nối trong background thread (không block UI)
            System.out.println("🔗 Đang thử kết nối 2 server trong background...");
            linkServersInBackground(ip1, port1, ip2, port2);
            
            // Chuyển sang màn hình login ngay (không đợi)
            loadLoginScreen();
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi định dạng", "Port phải là số nguyên (ví dụ: 1099)", AlertType.ERROR);
        }
    }
    
    /**
     * Chạy linkServers trong background thread với timeout
     */
    private void linkServersInBackground(String ip1, int port1, String ip2, int port2) {
        new Thread(() -> {
            try {
                linkServers(ip1, port1, ip2, port2);
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi khi link servers: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Tự động kết nối và link 2 server với nhau
     * Có timeout để tránh đợi quá lâu
     */
    private void linkServers(String ip1, int port1, String ip2, int port2) {
        String url1 = "rmi://" + ip1 + ":" + port1 + "/BankService";
        String url2 = "rmi://" + ip2 + ":" + port2 + "/BankService";
        
        System.out.println("🔗 Đang kết nối Server 1: " + url1);
        
        // Thử kết nối Server 1
        BankInterface server1 = null;
        try {
            server1 = (BankInterface) Naming.lookup(url1);
            System.out.println("✅ Server 1 kết nối thành công!");
        } catch (Exception e) {
            System.err.println("❌ Không thể kết nối Server 1: " + e.getMessage());
            System.err.println("   💡 Kiểm tra: Server 1 đã chạy chưa? IP và port đúng chưa?");
            return; // Không tiếp tục nếu Server 1 không kết nối được
        }
        
        System.out.println("🔗 Đang kết nối Server 2: " + url2);
        
        // Thử kết nối Server 2
        BankInterface server2 = null;
        try {
            server2 = (BankInterface) Naming.lookup(url2);
            System.out.println("✅ Server 2 kết nối thành công!");
        } catch (Exception e) {
            System.err.println("❌ Không thể kết nối Server 2: " + e.getMessage());
            System.err.println("   💡 Kiểm tra: Server 2 đã chạy chưa? IP và port đúng chưa?");
            System.err.println("   ⚠️ Hệ thống sẽ chỉ dùng Server 1");
            return; // Không tiếp tục nếu Server 2 không kết nối được
        }
        
        // Link 2 server với nhau
        try {
            System.out.println("🔗 Đang link 2 server...");
            server1.setOtherServer(server2);
            server2.setOtherServer(server1);
            System.out.println("✅✅✅ ĐÃ LINK 2 SERVER THÀNH CÔNG! ✅✅✅");
            System.out.println("📊 Dữ liệu giữa 2 server sẽ được đồng bộ tự động!");
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi link 2 server: " + e.getMessage());
            System.err.println("   Cả 2 server vẫn hoạt động nhưng KHÔNG đồng bộ với nhau.");
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


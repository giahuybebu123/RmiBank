package bank.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) {
        String accountId = txtUsername.getText().trim().toUpperCase();
        String password = txtPassword.getText();

        if (accountId.isEmpty() || password.isEmpty()) {
            showAlert("Lỗi đăng nhập", "Vui lòng nhập đầy đủ mã tài khoản và mật khẩu.");
            return;
        }

        // Cho phép đăng nhập với 3 tài khoản: A001, A002, A003
        // Password đơn giản: "123" cho tất cả
        if ((accountId.equals("A001") || accountId.equals("A002") || accountId.equals("A003")) 
            && password.equals("123")) {
            loadDashboard(accountId);
        } else {
            showAlert("Đăng nhập thất bại", "Sai mã tài khoản hoặc mật khẩu!\n\nGợi ý: Mã tài khoản: A001, A002, A003 | Password: 123");
        }
    }

    private void loadDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUser(username); // truyền dữ liệu sang dashboard

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bảng điều khiển");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải giao diện Dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

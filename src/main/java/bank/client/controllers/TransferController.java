package bank.client.controllers;

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

public class TransferController {

    @FXML
    private TextField txtReceiver;

    @FXML
    private TextField txtAmount;

    private String sender;
    private BankInterface bankService;

    public void initData(String sender, BankInterface bankService) {
        this.sender = sender;
        this.bankService = bankService;
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        String receiver = txtReceiver.getText().trim();
        String amountStr = txtAmount.getText().trim();

        if (receiver.isEmpty() || amountStr.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ tài khoản và số tiền.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showAlert("Sai định dạng", "Số tiền phải là số hợp lệ.");
            return;
        }

        try {
            boolean success = bankService.transfer(sender, receiver, amount);
            if (success) {
                showAlert("Thành công", "Đã chuyển " + amount + " VND đến tài khoản " + receiver);

                // Quay lại Dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();
                controller.setUser(sender);

                Stage stage = (Stage) txtReceiver.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Bảng điều khiển");

                controller.refreshBalance();
            } else {
                showAlert("Thất bại", "Không thể thực hiện giao dịch. Kiểm tra số dư hoặc tài khoản nhận.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể kết nối tới server RMI.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

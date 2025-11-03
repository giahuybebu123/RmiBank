package bank.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load màn hình cấu hình server trước
        Parent root = FXMLLoader.load(getClass().getResource("/serverconfig.fxml"));
        stage.setScene(new Scene(root, 500, 600));
        stage.setTitle("Bank RMI - Cấu hình 2 Server");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

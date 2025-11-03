module RmiBank {
    requires java.rmi;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports bank.interfaces;
    exports bank.model;
    exports bank.server;
    exports bank.client;

    opens bank.client to javafx.fxml, javafx.graphics;
    opens bank.client.controllers to javafx.fxml;
}

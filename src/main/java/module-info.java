module com.example.auditapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;


    opens com.example.auditapp to javafx.fxml;
    exports com.example.auditapp;
}
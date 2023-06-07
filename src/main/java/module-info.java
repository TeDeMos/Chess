module com.example.chesss {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    opens com.example.chesss to javafx.fxml;
    exports com.example.chesss;
}
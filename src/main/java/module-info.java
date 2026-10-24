module dio.fintrack {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens dio.fintrack to javafx.fxml;
    exports dio.fintrack;
    exports dio.fintrack.model;
    exports dio.fintrack.exception;
    exports dio.fintrack.repository;
    exports dio.fintrack.db;

}
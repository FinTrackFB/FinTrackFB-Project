module dio.fintrack {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.junit.jupiter.api;

    opens dio.fintrack to javafx.fxml;
    opens dio.fintrack.view to javafx.fxml;

    exports dio.fintrack;
    exports dio.fintrack.model;
    exports dio.fintrack.exception;
    exports dio.fintrack.repository;
    exports dio.fintrack.service;
    exports dio.fintrack.view;
    exports dio.fintrack.db;
}
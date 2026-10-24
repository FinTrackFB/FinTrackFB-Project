package dio.fintrack;

import dio.fintrack.model.Tipo;
import dio.fintrack.model.Transacao;
import javafx.application.Application;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;



import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(FinApp.class, args);
    }
}
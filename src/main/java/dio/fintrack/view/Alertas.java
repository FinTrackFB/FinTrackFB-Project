package dio.fintrack.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Alertas {

    private Alertas() { }

    public static void erro(String mensagem) {
        mostrar(Alert.AlertType.ERROR, "Erro", mensagem);
    }

    public static void aviso(String mensagem) {
        mostrar(Alert.AlertType.WARNING, "Atenção", mensagem);
    }

    public static void info(String mensagem) {
        mostrar(Alert.AlertType.INFORMATION, "FinTrack", mensagem);
    }

    public static boolean confirmar(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        return alerta.showAndWait().filter(botao -> botao == ButtonType.YES).isPresent();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
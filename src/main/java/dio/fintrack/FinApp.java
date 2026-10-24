package dio.fintrack;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.repository.ExportadorCSV;
import dio.fintrack.repository.RepositorioSQLite;
import dio.fintrack.service.ContaService;
import dio.fintrack.view.Alertas;
import dio.fintrack.view.TelaPrincipalController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FinApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ContaService service;
        try {
            service = new ContaService(new RepositorioSQLite(), new ExportadorCSV());
        } catch (FinTrackException e) {
            Alertas.erro("Não foi possível iniciar o sistema: " + e.getMessage());
            return;
        }

        FXMLLoader loader = new FXMLLoader(FinApp.class.getResource("/dio/fintrack/tela-principal.fxml"));
        loader.setControllerFactory(classe -> new TelaPrincipalController(service));
        Parent raiz = loader.load();

        Scene scene = new Scene(raiz, 900, 560);
        scene.getStylesheets().add(FinApp.class.getResource("/dio/fintrack/style.css").toExternalForm());
        stage.setTitle("FinTrack - Controle de Finanças Pessoais");
        stage.setScene(scene);
        stage.show();
    }
}
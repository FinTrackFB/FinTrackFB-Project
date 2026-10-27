
package dio.fintrack.view;

import dio.fintrack.service.ContaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RelatorioController {

    private final ContaService service;

    @FXML
    private Label labelReceitas;

    @FXML
    private Label labelDespesas;

    @FXML
    private Label labelSaldoFinal;

    public RelatorioController(ContaService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        carregarRelatorio();
    }

    private void carregarRelatorio() {
        labelReceitas.setText(
                "Total de Receitas: " +
                        Formatador.dinheiro(service.totalReceitas())
        );

        labelDespesas.setText(
                "Total de Despesas: " +
                        Formatador.dinheiro(service.totalDespesas())
        );

        labelSaldoFinal.setText(
                "Saldo Final: " +
                        Formatador.dinheiro(service.saldo())
        );
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) labelReceitas.getScene().getWindow();
        stage.close();
    }
}


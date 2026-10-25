package dio.fintrack.view;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.exception.ValorInvalidoException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Tipo;
import dio.fintrack.service.ContaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NovaTransacaoController {

    private final ContaService service;
    private final Tipo tipoInicial;

    @FXML private ComboBox<Tipo> campoTipo;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoValor;
    @FXML private DatePicker campoData;
    @FXML private ComboBox<String> campoCategoria;
    @FXML private Label labelDetalhe;
    @FXML private TextField campoDetalhe;

    public NovaTransacaoController(ContaService service, Tipo tipoInicial) {
        this.service = service;
        this.tipoInicial = tipoInicial;
    }

    @FXML
    private void initialize() {
        campoTipo.setItems(FXCollections.observableArrayList(Tipo.values()));
        campoTipo.setValue(tipoInicial);
        campoTipo.valueProperty().addListener((obs, antigo, novo) -> atualizarRotuloDetalhe());
        atualizarRotuloDetalhe();

        campoData.setValue(LocalDate.now());

        for (Categoria categoria : service.categoriasEmUso()) {
            campoCategoria.getItems().add(categoria.getNome());
        }
    }

    private void atualizarRotuloDetalhe() {
        labelDetalhe.setText(campoTipo.getValue() == Tipo.RECEITA ? "Fonte:" : "Forma de pagamento:");
    }

    @FXML
    private void salvar() {
        try {
            BigDecimal valor = lerValor();
            String categoria = campoCategoria.getEditor().getText();

            if (campoTipo.getValue() == Tipo.RECEITA) {
                service.registrarReceita(campoDescricao.getText(), valor, campoData.getValue(),
                        categoria, campoDetalhe.getText());
            } else {
                service.registrarDespesa(campoDescricao.getText(), valor, campoData.getValue(),
                        categoria, campoDetalhe.getText());
            }
            fechar();
        } catch (FinTrackException e) {
            Alertas.aviso(e.getMessage());
        }
    }

    private BigDecimal lerValor() throws ValorInvalidoException {
        return Formatador.lerValor(campoValor.getText());
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        ((Stage) campoDescricao.getScene().getWindow()).close();
    }
}
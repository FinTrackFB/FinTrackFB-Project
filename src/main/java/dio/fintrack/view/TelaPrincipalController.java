package dio.fintrack.view;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.model.Tipo;
import dio.fintrack.model.Transacao;
import dio.fintrack.service.ContaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.nio.file.Path;

public class TelaPrincipalController {

    private final ContaService service;

    @FXML private TableView<Transacao> tabela;
    @FXML private TableColumn<Transacao, String> colunaId;
    @FXML private TableColumn<Transacao, String> colunaTipo;
    @FXML private TableColumn<Transacao, String> colunaData;
    @FXML private TableColumn<Transacao, String> colunaDescricao;
    @FXML private TableColumn<Transacao, String> colunaCategoria;
    @FXML private TableColumn<Transacao, String> colunaValor;
    @FXML private Label labelReceitas;
    @FXML private Label labelDespesas;
    @FXML private Label labelSaldo;
    @FXML private TextField campoBusca;


    public TelaPrincipalController(ContaService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colunaTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        colunaData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getData().toString()));
        colunaDescricao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescricao()));
        colunaCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getNome()));

        colunaValor.setCellValueFactory(c ->
                new SimpleStringProperty(Formatador.dinheiroComSinal(c.getValue().getValorComSinal())));
        colunaValor.setCellFactory(coluna -> new TableCell<Transacao, String>() {
            @Override
            protected void updateItem(String texto, boolean vazio) {
                super.updateItem(texto, vazio);
                getStyleClass().removeAll("valor-receita", "valor-despesa");
                if (vazio || texto == null) {
                    setText(null);
                    return;
                }
                setText(texto);
                getStyleClass().add(texto.startsWith("-") ? "valor-despesa" : "valor-receita");
            }
        });

        tabela.setPlaceholder(new Label("Nenhuma transação cadastrada"));
        atualizar();
    }

    private void atualizar() {
        tabela.setItems(FXCollections.observableArrayList(service.listar()));
        labelReceitas.setText("Receitas: " + Formatador.dinheiro(service.totalReceitas()));
        labelDespesas.setText("Despesas: " + Formatador.dinheiro(service.totalDespesas()));
        labelSaldo.setText("Saldo: " + Formatador.dinheiro(service.saldo()));
    }

    @FXML
    private void novaReceita() {
        abrirNovaTransacao(Tipo.RECEITA);
    }

    @FXML
    private void novaDespesa() {
        abrirNovaTransacao(Tipo.DESPESA);
    }

    private void abrirNovaTransacao(Tipo tipoInicial) {
        abrirJanela("/dio/fintrack/nova-transacao.fxml",
                tipoInicial == Tipo.RECEITA ? "Nova receita" : "Nova despesa",
                classe -> new NovaTransacaoController(service, tipoInicial));
        atualizar();
    }

    @FXML
    private void graficos() {
        abrirJanela("/dio/fintrack/graficos.fxml", "Gráficos",
                classe -> new GraficosController(service));
    }
    @FXML
    private void relatorio() {
        abrirJanela(
                "/dio/fintrack/relatorio.fxml",
                "Relatório Financeiro",
                classe -> new RelatorioController(service)
        );
    }

    @FXML
    private void buscar() {
        try {
            int id = Integer.parseInt(campoBusca.getText().trim());
            Transacao encontrada = service.buscar(id);
            tabela.getSelectionModel().select(encontrada);
            tabela.scrollTo(encontrada);
        } catch (NumberFormatException e) {
            Alertas.aviso("O id deve ser um número inteiro.");
        } catch (FinTrackException e) {
            Alertas.aviso(e.getMessage());
        }
    }


    @FXML
    private void remover() {
        Transacao selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            Alertas.aviso("Selecione uma transação na tabela para remover.");
            return;
        }
        if (!Alertas.confirmar("Remover \"" + selecionada.getDescricao() + "\"?")) {
            return;
        }
        try {
            service.remover(selecionada.getId());
            atualizar();
        } catch (FinTrackException e) {
            Alertas.erro(e.getMessage());
        }
    }


    @FXML
    private void exportarCsv() {
        TextInputDialog dialogo = new TextInputDialog("transacoes");
        dialogo.setTitle("Exportar para CSV");
        dialogo.setHeaderText(null);
        dialogo.setContentText("Nome do arquivo:");
        dialogo.showAndWait().ifPresent(nome -> {
            try {
                Path caminho = service.exportarCsv(nome);
                Alertas.info("Arquivo gerado em:\n" + caminho);
            } catch (FinTrackException e) {
                Alertas.erro(e.getMessage());
            }
        });
    }

    private void abrirJanela(String fxml, String titulo, Callback<Class<?>, Object> fabrica) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setControllerFactory(fabrica);
            Parent raiz = loader.load();

            Scene scene = new Scene(raiz);
            scene.getStylesheets().add(getClass().getResource("/dio/fintrack/style.css").toExternalForm());

            Stage janela = new Stage();
            janela.setTitle(titulo);
            janela.setScene(scene);
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.showAndWait();
        } catch (IOException e) {
            Alertas.erro("Não foi possível abrir a tela: " + e.getMessage());
        }
    }
}
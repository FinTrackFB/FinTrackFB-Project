package dio.fintrack.view;

import dio.fintrack.model.Categoria;
import dio.fintrack.service.ContaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Map;

public class GraficosController {

    private final ContaService service;

    @FXML private PieChart graficoCategorias;
    @FXML private BarChart<String, Number> graficoReceitaDespesa;
    @FXML private Label labelSemDespesas;

    public GraficosController(ContaService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        montarGraficoDeCategorias();
        montarGraficoReceitaDespesa();
    }


    private void montarGraficoDeCategorias() {
        Map<Categoria, BigDecimal> gastos = service.gastosPorCategoria();

        boolean semDespesas = gastos.isEmpty();
        labelSemDespesas.setVisible(semDespesas);
        labelSemDespesas.setManaged(semDespesas);

        ObservableList<PieChart.Data> fatias = FXCollections.observableArrayList();
        gastos.forEach((categoria, total) -> fatias.add(
                new PieChart.Data(categoria + " (" + Formatador.dinheiro(total) + ")", total.doubleValue())));
        graficoCategorias.setData(fatias);
    }

    private void montarGraficoReceitaDespesa() {
        XYChart.Series<String, Number> receitas = new XYChart.Series<>();
        receitas.setName("Receitas");
        receitas.getData().add(new XYChart.Data<>("Total", service.totalReceitas()));

        XYChart.Series<String, Number> despesas = new XYChart.Series<>();
        despesas.setName("Despesas");
        despesas.getData().add(new XYChart.Data<>("Total", service.totalDespesas()));

        graficoReceitaDespesa.getData().add(receitas);
        graficoReceitaDespesa.getData().add(despesas);
    }

    @FXML
    private void fechar() {
        ((Stage) graficoCategorias.getScene().getWindow()).close();
    }
}
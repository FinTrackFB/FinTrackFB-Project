package dio.fintrack.repository;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Conta;
import dio.fintrack.model.Despesa;
import dio.fintrack.model.Receita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorCSVTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);


    @TempDir
    Path pasta;

    private Conta contaComDuasTransacoes() throws Exception {
        Conta conta = new Conta();

        Receita receita = new Receita("Salário", new BigDecimal("3000"), DATA,
                new Categoria("Trabalho"), "Empresa X");
        receita.setId(1);

        Despesa despesa = new Despesa("Mercado; semana", new BigDecimal("450.5"), DATA,
                new Categoria("Alimentação"), "Cartão");
        despesa.setId(2);

        conta.adicionar(receita);
        conta.adicionar(despesa);
        return conta;
    }

    @Test
    void deveGravarCabecalhoEUmaLinhaPorTransacao() throws Exception {
        Path destino = pasta.resolve("saida.csv");

        Path gerado = new ExportadorCSV().exportar(contaComDuasTransacoes(), destino.toString());

        List<String> linhas = Files.readAllLines(gerado, StandardCharsets.UTF_8);
        assertEquals(3, linhas.size());
        assertTrue(linhas.get(0).contains("id;tipo;data;descricao;categoria;valor"));
        assertEquals("1;RECEITA;2026-10-24;Salário;Trabalho;3000,00", linhas.get(1));
        assertTrue(gerado.isAbsolute());
    }

    @Test
    void deveColocarEntreAspasTextoComPontoEVirgula() throws Exception {
        Path destino = pasta.resolve("saida.csv");

        Path gerado = new ExportadorCSV().exportar(contaComDuasTransacoes(), destino.toString());

        List<String> linhas = Files.readAllLines(gerado, StandardCharsets.UTF_8);
        assertEquals("2;DESPESA;2026-10-24;\"Mercado; semana\";Alimentação;450,50", linhas.get(2));
    }

    @Test
    void deveAcrescentarExtensaoCsvQuandoFaltar() throws Exception {
        Path destino = pasta.resolve("relatorio");

        Path gerado = new ExportadorCSV().exportar(contaComDuasTransacoes(), destino.toString());

        assertTrue(gerado.getFileName().toString().endsWith(".csv"));
        assertTrue(Files.exists(gerado));
    }

    @Test
    void contaVaziaDeveGerarSoOCabecalho() throws Exception {
        Path destino = pasta.resolve("vazio.csv");

        Path gerado = new ExportadorCSV().exportar(new Conta(), destino.toString());

        assertEquals(1, Files.readAllLines(gerado, StandardCharsets.UTF_8).size());
    }

    @Test
    void deveRejeitarNomeDeArquivoVazio() {
        assertThrows(FinTrackException.class,
                () -> new ExportadorCSV().exportar(new Conta(), "   "));
    }

    @Test
    void deveLancarFinTrackExceptionQuandoNaoConseguirGravar() {

        String caminhoInvalido = pasta.resolve("nao-existe").resolve("saida.csv").toString();

        assertThrows(FinTrackException.class,
                () -> new ExportadorCSV().exportar(new Conta(), caminhoInvalido));
    }
}
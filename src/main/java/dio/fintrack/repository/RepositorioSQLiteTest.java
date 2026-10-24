package dio.fintrack.repository;

import dio.fintrack.exception.TransacaoNaoEncontradaException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Despesa;
import dio.fintrack.model.Receita;
import dio.fintrack.model.Tipo;
import dio.fintrack.model.Transacao;
import dio.fintrack.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioSQLiteTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);

    @TempDir
    Path pasta;

    private String url;
    private RepositorioSQLite repositorio;

    @BeforeEach
    void setUp() {
        url = "jdbc:sqlite:" + pasta.resolve("teste.db");
        repositorio = new RepositorioSQLite(url);
    }

    private static void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " mas foi " + real);
    }

    private Receita receita() throws Exception {
        return new Receita("Salário", new BigDecimal("3000.00"), DATA,
                new Categoria("Trabalho"), "Empresa X");
    }

    private Despesa despesa() throws Exception {
        return new Despesa("Mercado", new BigDecimal("450.50"), DATA,
                new Categoria("Alimentação"), "Cartão");
    }

    @Test
    void bancoNovoDeveComecarVazio() throws Exception {
        assertTrue(repositorio.listarTodas().isEmpty());
    }

    @Test
    void salvarDeveGerarIdsSequenciais() throws Exception {
        Receita r = receita();
        Despesa d = despesa();

        repositorio.salvar(r);
        repositorio.salvar(d);

        assertEquals(Integer.valueOf(1), r.getId());
        assertEquals(Integer.valueOf(2), d.getId());
    }

    @Test
    void deveRecuperarReceitaComTodosOsCampos() throws Exception {
        repositorio.salvar(receita());

        List<Transacao> lista = repositorio.listarTodas();

        assertEquals(1, lista.size());
        Receita lida = assertInstanceOf(Receita.class, lista.get(0));
        assertEquals(Tipo.RECEITA, lida.getTipo());
        assertEquals("Salário", lida.getDescricao());
        assertValor("3000", lida.getValor());
        assertEquals(DATA, lida.getData());
        assertEquals("Trabalho", lida.getCategoria().getNome());
        assertEquals("Empresa X", lida.getFonte());
    }

    @Test
    void deveRecuperarDespesaComTodosOsCampos() throws Exception {
        repositorio.salvar(despesa());

        Despesa lida = assertInstanceOf(Despesa.class, repositorio.listarTodas().get(0));

        assertEquals(Tipo.DESPESA, lida.getTipo());
        assertEquals("Mercado", lida.getDescricao());
        assertValor("450.5", lida.getValor());
        assertValor("-450.5", lida.getValorComSinal());
        assertEquals("Alimentação", lida.getCategoria().getNome());
        assertEquals("Cartão", lida.getFormaPagamento());
    }

    @Test
    void listaDeveVirNaOrdemDeCadastro() throws Exception {
        repositorio.salvar(despesa());
        repositorio.salvar(receita());

        List<Transacao> lista = repositorio.listarTodas();

        assertInstanceOf(Despesa.class, lista.get(0));
        assertInstanceOf(Receita.class, lista.get(1));
    }

    @Test
    void deveRemoverApenasATransacaoIndicada() throws Exception {
        repositorio.salvar(receita());
        repositorio.salvar(despesa());

        repositorio.remover(1);

        List<Transacao> lista = repositorio.listarTodas();
        assertEquals(1, lista.size());
        assertEquals(Integer.valueOf(2), lista.get(0).getId());
    }

    @Test
    void removerIdInexistenteDeveLancarExcecao() {
        assertThrows(TransacaoNaoEncontradaException.class, () -> repositorio.remover(99));
    }

    @Test
    void dadosDevemPersistirEntreInstancias() throws Exception {
        repositorio.salvar(receita());

        List<Transacao> lista = new RepositorioSQLite(url).listarTodas();

        assertEquals(1, lista.size());
    }

    @Test
    void contaServiceDeveRecarregarOSaldoDoBanco() throws Exception {
        ContaService primeiro = new ContaService(repositorio, new ExportadorCSV());
        primeiro.registrarReceita("Salário", new BigDecimal("3000"), DATA, "Trabalho", "Empresa X");
        primeiro.registrarDespesa("Mercado", new BigDecimal("450"), DATA, "Alimentação", "Cartão");

        ContaService segundo = new ContaService(new RepositorioSQLite(url), new ExportadorCSV());

        assertEquals(2, segundo.listar().size());
        assertValor("2550", segundo.saldo());
    }

    @Test
    void deveGuardarTextoComAcentosEAspas() throws Exception {
        Receita r = new Receita("Pão de queijo; \"quente\"", new BigDecimal("10"), DATA,
                new Categoria("Alimentação"), "Padaria São João");
        repositorio.salvar(r);

        Receita lida = assertInstanceOf(Receita.class, repositorio.listarTodas().get(0));

        assertEquals("Pão de queijo; \"quente\"", lida.getDescricao());
        assertEquals("Padaria São João", lida.getFonte());
    }

    @Test
    void entradaMaliciosaNaoDeveApagarATabela() throws Exception {
        String ataque = "x'); DROP TABLE transacoes; --";
        repositorio.salvar(new Receita(ataque, new BigDecimal("10"), DATA,
                new Categoria("Teste"), "Fonte"));

        repositorio.salvar(despesa());

        List<Transacao> lista = repositorio.listarTodas();
        assertEquals(2, lista.size());
        assertEquals(ataque, lista.get(0).getDescricao());
    }
}
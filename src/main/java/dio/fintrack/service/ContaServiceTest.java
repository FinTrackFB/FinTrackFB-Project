package dio.fintrack.service;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.exception.TransacaoNaoEncontradaException;
import dio.fintrack.exception.ValorInvalidoException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Despesa;
import dio.fintrack.model.Receita;
import dio.fintrack.model.Transacao;
import dio.fintrack.repository.ExportadorCSV;
import dio.fintrack.repository.RepositorioEmMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContaServiceTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);

    private ContaService service;

    @BeforeEach
    void setUp() throws FinTrackException {
        service = new ContaService(new RepositorioEmMemoria(), new ExportadorCSV());
    }

    private static void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " mas foi " + real);
    }

    @Test
    void registrarReceitaDeveGerarIdEAtualizarSaldo() throws Exception {
        Receita r = service.registrarReceita("Salário", new BigDecimal("3000"), DATA,
                "Trabalho", "Empresa X");

        assertEquals(Integer.valueOf(1), r.getId());
        assertValor("3000", service.saldo());
    }

    @Test
    void registrarDespesaDeveDiminuirOSaldo() throws Exception {
        service.registrarReceita("Salário", new BigDecimal("3000"), DATA, "Trabalho", "Empresa X");
        service.registrarDespesa("Mercado", new BigDecimal("450"), DATA, "Alimentação", "Cartão");

        assertValor("2550", service.saldo());
        assertValor("3000", service.totalReceitas());
        assertValor("450", service.totalDespesas());
    }

    @Test
    void idsDevemSerUnicosESequenciais() throws Exception {
        Receita r = service.registrarReceita("Salário", new BigDecimal("100"), DATA, "Trabalho", "Empresa X");
        Despesa d = service.registrarDespesa("Mercado", new BigDecimal("10"), DATA, "Alimentação", "Cartão");

        assertEquals(Integer.valueOf(1), r.getId());
        assertEquals(Integer.valueOf(2), d.getId());
    }

    @Test
    void valorInvalidoNaoDeveAlterarOEstadoDoSistema() {
        assertThrows(ValorInvalidoException.class,
                () -> service.registrarDespesa("Mercado", new BigDecimal("-5"), DATA, "Alimentação", "Cartão"));

        assertTrue(service.listar().isEmpty());
        assertValor("0", service.saldo());
    }

    @Test
    void deveRejeitarCategoriaVazia() {
        assertThrows(ValorInvalidoException.class,
                () -> service.registrarReceita("Salário", new BigDecimal("100"), DATA, "  ", "Empresa X"));
    }

    @Test
    void removerDeveAtualizarSaldoELista() throws Exception {
        service.registrarReceita("Salário", new BigDecimal("1000"), DATA, "Trabalho", "Empresa X");
        service.registrarDespesa("Mercado", new BigDecimal("300"), DATA, "Alimentação", "Cartão");

        service.remover(2);

        assertEquals(1, service.listar().size());
        assertValor("1000", service.saldo());
    }

    @Test
    void removerIdInexistenteDeveLancarExcecao() {
        assertThrows(TransacaoNaoEncontradaException.class, () -> service.remover(99));
    }

    @Test
    void deveBuscarTransacaoPorId() throws Exception {
        service.registrarReceita("Salário", new BigDecimal("100"), DATA, "Trabalho", "Empresa X");

        Transacao encontrada = service.buscar(1);

        assertEquals("Salário", encontrada.getDescricao());
    }

    @Test
    void buscarIdInexistenteDeveLancarExcecao() {
        assertThrows(TransacaoNaoEncontradaException.class, () -> service.buscar(42));
    }

    @Test
    void relatorioPorCategoriaDeveSomarDespesasIgnorandoMaiusculas() throws Exception {
        service.registrarReceita("Salário", new BigDecimal("3000"), DATA, "Trabalho", "Empresa X");
        service.registrarDespesa("Almoço", new BigDecimal("40"), DATA, "Alimentação", "Cartão");
        service.registrarDespesa("Jantar", new BigDecimal("60"), DATA, "alimentação", "Pix");

        Map<Categoria, BigDecimal> gastos = service.gastosPorCategoria();

        assertEquals(1, gastos.size());
        assertValor("100", gastos.get(new Categoria("Alimentação")));
    }

    @Test
    void deveCarregarTransacoesJaGuardadasNoRepositorio() throws Exception {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.salvar(new Receita("Salário", new BigDecimal("500"), DATA,
                new Categoria("Trabalho"), "Empresa X"));

        ContaService novoService = new ContaService(repositorio, new ExportadorCSV());

        assertEquals(1, novoService.listar().size());
        assertValor("500", novoService.saldo());
    }
}

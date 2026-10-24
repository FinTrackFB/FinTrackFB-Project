package dio.fintrack.model;

import dio.fintrack.exception.TransacaoNaoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta();
    }



    private static void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " mas foi " + real);
    }

    private Receita receita(int id, String valor) throws Exception {
        Receita r = new Receita("Salário", new BigDecimal(valor), DATA,
                new Categoria("Trabalho"), "Empresa X");
        r.setId(id);
        return r;
    }

    private Despesa despesa(int id, String valor, String categoria) throws Exception {
        Despesa d = new Despesa("Compra", new BigDecimal(valor), DATA,
                new Categoria(categoria), "Cartão");
        d.setId(id);
        return d;
    }



    @Test
    void contaNovaDeveComecarVazia() {
        assertTrue(conta.listar().isEmpty());
        assertValor("0", conta.calcularSaldo());
    }

    @Test
    void deveCalcularSaldoSomandoValorComSinal() throws Exception {
        conta.adicionar(receita(1, "3000"));
        conta.adicionar(despesa(2, "450", "Alimentação"));

        assertValor("2550", conta.calcularSaldo());
    }

    @Test
    void deveCalcularTotaisPorTipo() throws Exception {
        conta.adicionar(receita(1, "3000"));
        conta.adicionar(receita(2, "500"));
        conta.adicionar(despesa(3, "450", "Alimentação"));

        assertValor("3500", conta.totalPorTipo(Tipo.RECEITA));
        assertValor("450", conta.totalPorTipo(Tipo.DESPESA));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Receita r = receita(1, "100");
        conta.adicionar(r);

        assertSame(r, conta.buscarPorId(1));
    }

    @Test
    void buscarIdInexistenteDeveLancarExcecao() {
        TransacaoNaoEncontradaException e = assertThrows(TransacaoNaoEncontradaException.class,
                () -> conta.buscarPorId(99));

        assertEquals("Transação não encontrada: id 99", e.getMessage());
    }

    @Test
    void deveRemoverTransacaoEAtualizarSaldo() throws Exception {
        conta.adicionar(receita(1, "1000"));
        conta.adicionar(despesa(2, "300", "Alimentação"));

        Transacao removida = conta.remover(2);

        assertEquals(Integer.valueOf(2), removida.getId());
        assertEquals(1, conta.listar().size());
        assertValor("1000", conta.calcularSaldo());
        assertThrows(TransacaoNaoEncontradaException.class, () -> conta.buscarPorId(2));
    }

    @Test
    void removerIdInexistenteDeveLancarExcecao() {
        assertThrows(TransacaoNaoEncontradaException.class, () -> conta.remover(99));
    }

    @Test
    void gastosPorCategoriaDeveConsiderarApenasDespesas() throws Exception {
        conta.adicionar(receita(1, "3000"));
        conta.adicionar(despesa(2, "100", "Alimentação"));
        conta.adicionar(despesa(3, "50", "Alimentação"));
        conta.adicionar(despesa(4, "200", "Transporte"));

        Map<Categoria, BigDecimal> gastos = conta.gastosPorCategoria();

        assertEquals(2, gastos.size());
        assertValor("150", gastos.get(new Categoria("Alimentação")));
        assertValor("200", gastos.get(new Categoria("Transporte")));
        assertNull(gastos.get(new Categoria("Trabalho")));
    }

    @Test
    void categoriasEmUsoNaoDeveRepetir() throws Exception {
        conta.adicionar(receita(1, "3000"));
        conta.adicionar(despesa(2, "100", "Alimentação"));
        conta.adicionar(despesa(3, "50", "alimentação"));

        Set<Categoria> categorias = conta.categoriasEmUso();

        assertEquals(2, categorias.size());
    }

    @Test
    void listaRetornadaNaoDevePermitirAlteracao() throws Exception {
        conta.adicionar(receita(1, "100"));
        List<Transacao> lista = conta.listar();
        Transacao outra = receita(2, "50");

        assertThrows(UnsupportedOperationException.class, () -> lista.add(outra));
        assertEquals(1, conta.listar().size());
    }

    @Test
    void deveRecusarTransacaoSemId() throws Exception {
        Receita semId = new Receita("Salário", new BigDecimal("100"), DATA,
                new Categoria("Trabalho"), "Empresa X");

        assertThrows(IllegalArgumentException.class, () -> conta.adicionar(semId));
    }
}
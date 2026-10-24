package dio.fintrack.model;

import dio.fintrack.exception.ValorInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DespesaTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);

    private static void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " mas foi " + real);
    }

    @Test
    void deveCriarDespesaComDadosValidos() throws Exception {
        Despesa d = new Despesa("Mercado", new BigDecimal("450.00"), DATA,
                new Categoria("Alimentação"), "Cartão");

        assertEquals("Mercado", d.getDescricao());
        assertValor("450", d.getValor());
        assertEquals("Alimentação", d.getCategoria().getNome());
        assertEquals("Cartão", d.getFormaPagamento());
        assertEquals(Tipo.DESPESA, d.getTipo());
    }

    @Test
    void despesaDevolveValorNegativoComSinal() throws Exception {
        Despesa d = new Despesa("Mercado", new BigDecimal("450"), DATA,
                new Categoria("Alimentação"), "Cartão");

        assertValor("-450", d.getValorComSinal());
    }

    @Test
    void deveRejeitarValorZeroOuNegativo() throws Exception {
        Categoria categoria = new Categoria("Alimentação");

        assertThrows(ValorInvalidoException.class,
                () -> new Despesa("Mercado", BigDecimal.ZERO, DATA, categoria, "Cartão"));
        assertThrows(ValorInvalidoException.class,
                () -> new Despesa("Mercado", new BigDecimal("-1"), DATA, categoria, "Cartão"));
    }

    @Test
    void deveRejeitarFormaDePagamentoVazia() throws Exception {
        Categoria categoria = new Categoria("Alimentação");

        assertThrows(ValorInvalidoException.class,
                () -> new Despesa("Mercado", new BigDecimal("10"), DATA, categoria, ""));
    }

    @Test
    void polimorfismoSomaReceitaEDespesaSemTestarOTipo() throws Exception {
        Categoria categoria = new Categoria("Geral");
        List<Transacao> transacoes = List.of(
                new Receita("Salário", new BigDecimal("1000"), DATA, categoria, "Empresa X"),
                new Despesa("Mercado", new BigDecimal("300"), DATA, categoria, "Cartão"));

        BigDecimal soma = BigDecimal.ZERO;
        for (Transacao t : transacoes) {
            soma = soma.add(t.getValorComSinal());
        }

        assertValor("700", soma);
    }
}

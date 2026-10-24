package dio.fintrack.model;

import dio.fintrack.exception.ValorInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReceitaTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);

    private static void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " mas foi " + real);
    }

    @Test
    void deveCriarReceitaComDadosValidos() throws Exception {
        Receita r = new Receita("Salário", new BigDecimal("3000.00"), DATA,new Categoria("Trabalho"), "Empresa X");

        assertEquals("Salário", r.getDescricao());
        assertValor("3000", r.getValor());
        assertEquals(DATA, r.getData());
        assertEquals("Trabalho", r.getCategoria().getNome());
        assertEquals("Empresa X", r.getFonte());
        assertEquals(Tipo.RECEITA, r.getTipo());
    }

    @Test
    void receitaDevolveValorPositivoComSinal() throws Exception {
        Receita r = new Receita("Salário", new BigDecimal("100"), DATA,
                new Categoria("Trabalho"), "Empresa X");

        assertValor("100", r.getValorComSinal());
    }

    @Test
    void deveRejeitarValorZeroOuNegativo() throws Exception {
        Categoria categoria = new Categoria("Trabalho");

        assertThrows(ValorInvalidoException.class,
                () -> new Receita("Salário", BigDecimal.ZERO, DATA, categoria, "Empresa X"));
        assertThrows(ValorInvalidoException.class,
                () -> new Receita("Salário", new BigDecimal("-10"), DATA, categoria, "Empresa X"));
    }

    @Test
    void deveRejeitarDescricaoVazia() throws Exception {
        Categoria categoria = new Categoria("Trabalho");

        assertThrows(ValorInvalidoException.class,
                () -> new Receita("", new BigDecimal("10"), DATA, categoria, "Empresa X"));
        assertThrows(ValorInvalidoException.class,
                () -> new Receita("   ", new BigDecimal("10"), DATA, categoria, "Empresa X"));
        assertThrows(ValorInvalidoException.class,
                () -> new Receita(null, new BigDecimal("10"), DATA, categoria, "Empresa X"));
    }

    @Test
    void deveRejeitarFonteVazia() throws Exception {
        Categoria categoria = new Categoria("Trabalho");

        assertThrows(ValorInvalidoException.class,
                () -> new Receita("Salário", new BigDecimal("10"), DATA, categoria, " "));
    }

    @Test
    void deveRejeitarCategoriaNula() {
        assertThrows(ValorInvalidoException.class,
                () -> new Receita("Salário", new BigDecimal("10"), DATA, null, "Empresa X"));
    }

    @Test
    void deveRejeitarDataNula() throws Exception {
        Categoria categoria = new Categoria("Trabalho");

        assertThrows(ValorInvalidoException.class,
                () -> new Receita("Salário", new BigDecimal("10"), null, categoria, "Empresa X"));
    }

    @Test
    void mensagemDaExcecaoDeveSerClara() throws Exception {
        Categoria categoria = new Categoria("Trabalho");

        ValorInvalidoException e = assertThrows(ValorInvalidoException.class,
                () -> new Receita("Salário", BigDecimal.ZERO, DATA, categoria, "Empresa X"));

        assertEquals("O valor deve ser maior que zero", e.getMessage());
    }
}
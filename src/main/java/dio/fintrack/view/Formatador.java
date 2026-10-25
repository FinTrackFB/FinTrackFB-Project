package dio.fintrack.view;

import dio.fintrack.exception.ValorInvalidoException;

import java.math.BigDecimal;
import java.util.Locale;

public final class Formatador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Formatador() { }

    public static String dinheiro(BigDecimal valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }

    public static String dinheiroComSinal(BigDecimal valorComSinal) {
        String sinal = valorComSinal.signum() < 0 ? "- " : "+ ";
        return sinal + dinheiro(valorComSinal.abs());
    }


    public static BigDecimal lerValor(String texto) throws ValorInvalidoException {
        String limpo = texto == null ? "" : texto.replace("R$", "").replace(" ", "").trim();
        boolean negativo = limpo.startsWith("-");
        if (negativo) {
            limpo = limpo.substring(1);
        }
        if (!limpo.matches("[0-9.,]+")) {
            throw new ValorInvalidoException("Valor inválido: digite apenas números, por exemplo 12.000,50");
        }

        int posicao = Math.max(limpo.lastIndexOf('.'), limpo.lastIndexOf(','));
        int digitosDepois = limpo.length() - posicao - 1;
        String inteiro = limpo;
        String decimal = "";
        if (posicao >= 0 && digitosDepois >= 1 && digitosDepois <= 2) {
            inteiro = limpo.substring(0, posicao);
            decimal = limpo.substring(posicao + 1);
        }
        inteiro = inteiro.replace(".", "").replace(",", "");

        try {
            BigDecimal valor = new BigDecimal(decimal.isEmpty() ? inteiro : inteiro + "." + decimal);
            return negativo ? valor.negate() : valor;
        } catch (NumberFormatException e) {
            throw new ValorInvalidoException("Valor inválido: digite apenas números, por exemplo 12.000,50");
        }
    }
}
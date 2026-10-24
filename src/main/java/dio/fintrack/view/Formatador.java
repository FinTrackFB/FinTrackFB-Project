package dio.fintrack.view;

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
}

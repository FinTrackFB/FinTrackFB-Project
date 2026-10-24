package dio.fintrack.model;

import dio.fintrack.exception.ValorInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Despesa extends Transacao {

    private final String formaPagamento;

    public Despesa(String descricao, BigDecimal valor, LocalDate data,Categoria categoria, String formaPagamento) throws ValorInvalidoException {
        super(descricao, valor, data, categoria);
        if (formaPagamento == null || formaPagamento.isBlank()) {
            throw new ValorInvalidoException("Forma de pagamento obrigatória");
        }
        this.formaPagamento = formaPagamento.trim();
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    @Override
    public BigDecimal getValorComSinal() {
        return getValor().negate();
    }

    @Override
    public Tipo getTipo() {
        return Tipo.DESPESA;
    }
}

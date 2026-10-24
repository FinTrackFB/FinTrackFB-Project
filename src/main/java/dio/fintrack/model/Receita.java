package dio.fintrack.model;



import dio.fintrack.exception.ValorInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Receita extends Transacao {

    private final String fonte;

    public Receita(String descricao, BigDecimal valor, LocalDate data,Categoria categoria, String fonte) throws ValorInvalidoException {
        super(descricao, valor, data, categoria);
        if (fonte == null || fonte.isBlank()) {
            throw new ValorInvalidoException("Fonte obrigatória");
        }
        this.fonte = fonte.trim();
    }

    public String getFonte() { return fonte; }

    @Override
    public BigDecimal getValorComSinal() {
        return getValor();
    }

    @Override
    public Tipo getTipo() {
        return Tipo.RECEITA;
    }
}

package dio.fintrack.model;

import dio.fintrack.exception.ValorInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Transacao {

    private Integer id;
    private final String descricao;
    private final BigDecimal valor;
    private final LocalDate data;
    private final Categoria categoria;

    protected Transacao(String descricao, BigDecimal valor, LocalDate data, Categoria categoria)throws ValorInvalidoException {
        if (descricao == null || descricao.isBlank()) {
            throw new ValorInvalidoException("Descrição obrigatória");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("O valor deve ser maior que zero");
        }
        if (data == null) {
            throw new ValorInvalidoException("Data inválida");
        }
        if (categoria == null) {
            throw new ValorInvalidoException("Categoria obrigatória");
        }
        this.descricao = descricao.trim();
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
    }

    public abstract BigDecimal getValorComSinal();

    public abstract Tipo getTipo();

    public Integer getId() { return id; }


    public void setId(Integer id) { this.id = id; }

    public String getDescricao() { return descricao; }

    public BigDecimal getValor() { return valor; }

    public LocalDate getData() { return data; }

    public Categoria getCategoria() { return categoria; }

    @Override
    public String toString() {
        return getTipo() + " #" + id + " | " + data + " | " + descricao
                + " | " + categoria + " | R$ " + valor;
    }
}
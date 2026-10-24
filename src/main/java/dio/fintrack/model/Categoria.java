package dio.fintrack.model;

import dio.fintrack.exception.ValorInvalidoException;

public class Categoria {

    private final String nome;

    public Categoria(String nome) throws ValorInvalidoException {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalidoException("Categoria obrigatória");
        }
        this.nome = nome.trim();
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria outra)) return false;
        return nome.equalsIgnoreCase(outra.nome);
    }

    @Override
    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return nome;
    }
}
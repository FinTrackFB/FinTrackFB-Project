package dio.fintrack.model;

import dio.fintrack.exception.TransacaoNaoEncontradaException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Conta {


    private final List<Transacao> transacoes = new ArrayList<>();

    private final Map<Integer, Transacao> indicePorId = new HashMap<>();

    public void adicionar(Transacao transacao) {
        if (transacao.getId() == null) {
            throw new IllegalArgumentException("A transação precisa ter id antes de entrar na conta");
        }
        transacoes.add(transacao);
        indicePorId.put(transacao.getId(), transacao);
    }

    public Transacao buscarPorId(int id) throws TransacaoNaoEncontradaException {
        Transacao transacao = indicePorId.get(id);
        if (transacao == null) {
            throw new TransacaoNaoEncontradaException(id);
        }
        return transacao;
    }

    public Transacao remover(int id) throws TransacaoNaoEncontradaException {
        Transacao removida = indicePorId.remove(id);
        if (removida == null) {
            throw new TransacaoNaoEncontradaException(id);
        }
        transacoes.remove(removida);
        return removida;
    }


    public List<Transacao> listar() {
        return List.copyOf(transacoes);
    }


    public BigDecimal calcularSaldo() {
        BigDecimal saldo = BigDecimal.ZERO;
        for (Transacao t : transacoes) {
            saldo = saldo.add(t.getValorComSinal());
        }
        return saldo;
    }

    public BigDecimal totalPorTipo(Tipo tipo) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transacao t : transacoes) {
            if (t.getTipo() == tipo) {
                total = total.add(t.getValor());
            }
        }
        return total;
    }

    public Map<Categoria, BigDecimal> gastosPorCategoria() {
        Map<Categoria, BigDecimal> gastos = new LinkedHashMap<>();
        for (Transacao t : transacoes) {
            if (t.getTipo() == Tipo.DESPESA) {
                gastos.merge(t.getCategoria(), t.getValor(), BigDecimal::add);
            }
        }
        return gastos;
    }


    public Set<Categoria> categoriasEmUso() {
        Set<Categoria> categorias = new LinkedHashSet<>();
        for (Transacao t : transacoes) {
            categorias.add(t.getCategoria());
        }
        return categorias;
    }
}

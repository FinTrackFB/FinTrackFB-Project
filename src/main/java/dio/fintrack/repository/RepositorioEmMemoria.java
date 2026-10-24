package dio.fintrack.repository;

import dio.fintrack.exception.TransacaoNaoEncontradaException;
import dio.fintrack.model.Transacao;

import java.util.ArrayList;
import java.util.List;

public class RepositorioEmMemoria implements RepositorioTransacoes {

    private final List<Transacao> transacoes = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public void salvar(Transacao transacao) {
        transacao.setId(proximoId++);
        transacoes.add(transacao);
    }

    @Override
    public List<Transacao> listarTodas() {
        return List.copyOf(transacoes);
    }

    @Override
    public void remover(int id) throws TransacaoNaoEncontradaException {
        boolean removida = transacoes.removeIf(t -> Integer.valueOf(id).equals(t.getId()));
        if (!removida) {
            throw new TransacaoNaoEncontradaException(id);
        }
    }
}
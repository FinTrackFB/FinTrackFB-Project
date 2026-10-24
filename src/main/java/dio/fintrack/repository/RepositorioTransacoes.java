package dio.fintrack.repository;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.model.Transacao;

import java.util.List;

public interface RepositorioTransacoes {


    void salvar(Transacao transacao) throws FinTrackException;

    List<Transacao> listarTodas() throws FinTrackException;

    void remover(int id) throws FinTrackException;
}

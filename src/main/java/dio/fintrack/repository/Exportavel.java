package dio.fintrack.repository;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.model.Conta;

import java.nio.file.Path;

public interface Exportavel {


    Path exportar(Conta conta, String nomeArquivo) throws FinTrackException;
}

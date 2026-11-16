package dio.fintrack.exception;

public class TransacaoNaoEncontradaException extends RuntimeException {

    public TransacaoNaoEncontradaException(int id) {
        super("Transação não encontrada: id " + id);
    }

}

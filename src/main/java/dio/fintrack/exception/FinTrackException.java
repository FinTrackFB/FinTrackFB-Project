package dio.fintrack.exception;

public class FinTrackException extends Exception {

    public FinTrackException(String mensagem) {
        super(mensagem);
    }

    public FinTrackException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
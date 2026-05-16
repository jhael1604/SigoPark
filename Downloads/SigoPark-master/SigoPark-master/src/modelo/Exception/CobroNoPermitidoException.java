package modelo.Exception;

public class CobroNoPermitidoException extends Exception {
    public CobroNoPermitidoException(String mensaje) {
        super(mensaje);
    }
    public CobroNoPermitidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

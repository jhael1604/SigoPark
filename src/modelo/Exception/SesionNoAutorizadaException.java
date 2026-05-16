package modelo.Exception;

public class SesionNoAutorizadaException extends Exception {
    public SesionNoAutorizadaException(String mensaje) { super(mensaje); }
    public SesionNoAutorizadaException(String mensaje, Throwable causa) { super(mensaje, causa); }
}

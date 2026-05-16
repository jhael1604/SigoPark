package modelo.Exception;

public class EspacioNoDisponibleException extends Exception {
    public EspacioNoDisponibleException(String mensaje) { super(mensaje); }
    public EspacioNoDisponibleException(String mensaje, Throwable causa) { super(mensaje, causa); }
}

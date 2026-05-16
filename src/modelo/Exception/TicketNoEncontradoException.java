package modelo.Exception;

public class TicketNoEncontradoException extends Exception {
    public TicketNoEncontradoException(String mensaje) { super(mensaje); }
    public TicketNoEncontradoException(String mensaje, Throwable causa) { super(mensaje, causa); }
}

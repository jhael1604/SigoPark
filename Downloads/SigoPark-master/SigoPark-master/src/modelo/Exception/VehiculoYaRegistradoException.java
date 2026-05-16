package modelo.Exception;

public class VehiculoYaRegistradoException extends Exception {
    public VehiculoYaRegistradoException(String mensaje) { super(mensaje); }
    public VehiculoYaRegistradoException(String mensaje, Throwable causa) { super(mensaje, causa); }
}

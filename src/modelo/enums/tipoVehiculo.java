package modelo.enums;

public enum tipoVehiculo {
    AUTO("Automóvil"),
    MOTO("Motocicleta");
    

    private final String descripcion;

    tipoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

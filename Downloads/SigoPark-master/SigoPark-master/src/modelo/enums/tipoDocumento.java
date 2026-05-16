package modelo.enums;

public enum tipoDocumento {
    CC("Cédula de Ciudadanía");

    private final String descripcion;

    tipoDocumento(String descripcion) {
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

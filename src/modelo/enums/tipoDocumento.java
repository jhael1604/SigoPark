package modelo.enums;

public enum tipoDocumento {
    CC("Cédula de Ciudadanía"),
    CE("Cédula de Extranjería"),
    PASAPORTE("Pasaporte"),
    NIT("NIT"),
    TI("Tarjeta de Identidad");

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

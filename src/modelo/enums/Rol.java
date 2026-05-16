package modelo.enums;

public enum Rol {
    ADMIN("Administrador"),
    CAJERO("Cajero"),
    OPERARIO("Operario");

    private final String descripcion;

    Rol(String descripcion) {
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

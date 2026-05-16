package modelo.parqueo;

public class EspacioParqueo {

    public enum EstadoEspacio {
        LIBRE, OCUPADO, DISCAPACIDAD
    }

    private final String id;       // e.g. "A1", "B3"
    private final char fila;       // A, B, C
    private final int columna;     // 1-6
    private EstadoEspacio estado;
    private Vehiculo vehiculoActual;

    public EspacioParqueo(char fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.id = String.valueOf(fila) + columna;
        this.estado = EstadoEspacio.LIBRE;
    }

    // Getters
    public String getId() { return id; }
    public char getFila() { return fila; }
    public int getColumna() { return columna; }
    public EstadoEspacio getEstado() { return estado; }
    public Vehiculo getVehiculoActual() { return vehiculoActual; }

    // Setters
    public void setEstado(EstadoEspacio estado) { this.estado = estado; }
    public void setVehiculoActual(Vehiculo vehiculo) { this.vehiculoActual = vehiculo; }

    public boolean isDisponible() {
        return estado == EstadoEspacio.LIBRE || estado == EstadoEspacio.DISCAPACIDAD;
    }

    @Override
    public String toString() {
        return id + " [" + estado + "]";
    }
}

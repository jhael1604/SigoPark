package modelo.parqueo;

import modelo.enums.tipoVehiculo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vehiculo {

    public enum EstadoVehiculo {
        ESTACIONADO, RETIRADO, PENDIENTE
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String placa;
    private tipoVehiculo tipo;
    private String propietario;
    private EspacioParqueo espacio;
    private EstadoVehiculo estado;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;

    public Vehiculo(String placa, tipoVehiculo tipo, String propietario) {
        this.placa = placa.toUpperCase().trim();
        this.tipo = tipo;
        this.propietario = propietario;
        this.estado = EstadoVehiculo.PENDIENTE;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getPlaca() { return placa; }
    public tipoVehiculo getTipo() { return tipo; }
    public String getPropietario() { return propietario; }
    public EspacioParqueo getEspacio() { return espacio; }
    public EstadoVehiculo getEstado() { return estado; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public LocalDateTime getHoraSalida() { return horaSalida; }

    public String getHoraEntradaFormateada() {
        return horaEntrada != null ? horaEntrada.format(FORMATTER) : "-";
    }

    public String getHoraSalidaFormateada() {
        return horaSalida != null ? horaSalida.format(FORMATTER) : "-";
    }

    public String getEspacioId() {
        return espacio != null ? espacio.getId() : "-";
    }

    // ── Setters ──────────────────────────────────────────────
    public void setPlaca(String placa) { this.placa = placa.toUpperCase().trim(); }
    public void setTipo(tipoVehiculo tipo) { this.tipo = tipo; }
    public void setPropietario(String propietario) { this.propietario = propietario; }
    public void setEspacio(EspacioParqueo espacio) { this.espacio = espacio; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    @Override
    public String toString() {
        return placa + " | " + tipo.getDescripcion() + " | " + propietario;
    }
}

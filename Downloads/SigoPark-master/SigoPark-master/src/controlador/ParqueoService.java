package controlador;

import modelo.Exception.*;
import modelo.enums.tipoVehiculo;
import modelo.parqueo.EspacioParqueo;
import modelo.parqueo.EspacioParqueo.EstadoEspacio;
import modelo.parqueo.Vehiculo;
import modelo.parqueo.Vehiculo.EstadoVehiculo;
import modelo.usuarios.Administrador;

import modelo.usuarios.Usuario;
import modelo.enums.tipoDocumento;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ParqueoService {

    private static final int FILAS = 3;
    private static final int COLUMNAS = 6;
    private static final char[] LETRAS_FILA = {'A', 'B', 'C'};

    private final EspacioParqueo[][] mapa;
    private final List<Vehiculo> vehiculos;
    private final Map<String, Usuario> usuarios;

    private Usuario sesionActual;

    // ── Constructor ───────────────────────────────────────────
    public ParqueoService() {
        mapa = new EspacioParqueo[FILAS][COLUMNAS];
        vehiculos = new ArrayList<>();
        usuarios = new HashMap<>();

        inicializarMapa();
        cargarUsuariosDemo();
    }

    // ── Inicialización ────────────────────────────────────────
    private void inicializarMapa() {
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                mapa[f][c] = new EspacioParqueo(LETRAS_FILA[f], c + 1);
            }
        }
        // Marcar espacios de discapacidad (primera columna de cada fila)
        mapa[0][0].setEstado(EstadoEspacio.DISCAPACIDAD);
        mapa[0][0].setEsDiscapacidad(true);   
        mapa[1][0].setEstado(EstadoEspacio.DISCAPACIDAD);
        mapa[1][0].setEsDiscapacidad(true);   
        mapa[2][0].setEstado(EstadoEspacio.DISCAPACIDAD);
        mapa[2][0].setEsDiscapacidad(true);   
    }

    private void cargarUsuariosDemo() {
        Administrador admin = new Administrador(
            "U001", "Jhael", "Marin",
            tipoDocumento.CC, "1234567890",
            "admin", "admin123"
        );


        usuarios.put(admin.getUsername(), admin);
        
        
    }

    // ── Sesión ────────────────────────────────────────────────
    public Usuario iniciarSesion(String username, String password)
            throws CredencialesInvalidasException, UsuarioNoEncontradoException {
        Usuario u = usuarios.get(username);
        if (u == null) {
            throw new UsuarioNoEncontradoException("Usuario y/o contraseña incorrecta");
        }
        if (!u.verificarCredenciales(username, password)) {
            throw new CredencialesInvalidasException("Usuario y/o contraseña incorrecta.");
        }
        sesionActual = u;
        return u;
    }

    public void cerrarSesion() {
        sesionActual = null;
    }

    public Usuario getSesionActual() { return sesionActual; }

    private void verificarSesion() throws SesionNoAutorizadaException {
        if (sesionActual == null) {
            throw new SesionNoAutorizadaException("No hay sesión activa.");
        }
    }

    private void verificarPermiso(String accion) throws SesionNoAutorizadaException {
        verificarSesion();
        if (!sesionActual.tienePermiso(accion)) {
            throw new SesionNoAutorizadaException(
                "El usuario '" + sesionActual.getUsername() +
                "' no tiene permiso para: " + accion
            );
        }
    }

    // ── Mapa ──────────────────────────────────────────────────
    public EspacioParqueo[][] getMapa() { return mapa; }

    public EspacioParqueo getEspacio(String id) {
        for (EspacioParqueo[] fila : mapa) {
            for (EspacioParqueo e : fila) {
                if (e.getId().equalsIgnoreCase(id)) return e;
            }
        }
        return null;
    }

    public List<EspacioParqueo> getEspaciosLibres() {
        List<EspacioParqueo> libres = new ArrayList<>();
        for (EspacioParqueo[] fila : mapa) {
            for (EspacioParqueo e : fila) {
                if (e.isDisponible()) libres.add(e);
            }
        }
        return libres;
    }

    // ── Operaciones de vehículos ──────────────────────────────
    public Vehiculo registrarEntrada(String placa, tipoVehiculo tipo,
                                     String propietario, String espacioId)
            throws SesionNoAutorizadaException, EspacioNoDisponibleException,
                   VehiculoYaRegistradoException {

        verificarPermiso("REGISTRAR_ENTRADA");

        // Verificar duplicado
        boolean yaEstacionado = vehiculos.stream()
            .anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa)
                       && v.getEstado() == EstadoVehiculo.ESTACIONADO);
        if (yaEstacionado) {
            throw new VehiculoYaRegistradoException(
                "El vehículo con placa " + placa + " ya está estacionado.");
        }

        // Resolver espacio
        EspacioParqueo espacio;
        if (espacioId != null && !espacioId.isBlank()) {
            espacio = getEspacio(espacioId);
            if (espacio == null || !espacio.isDisponible()) {
                throw new EspacioNoDisponibleException(
                    "El espacio " + espacioId + " no está disponible.");
            }
        } else {
            espacio = getEspaciosLibres().stream()
                .filter(e -> e.getEstado() == EstadoEspacio.LIBRE ||
                        e.getEstado() == EstadoEspacio.DISCAPACIDAD)
                .findFirst()
                .orElseThrow(() -> new EspacioNoDisponibleException(
                    "No hay espacios libres disponibles."));
        }

        Vehiculo v = new Vehiculo(placa, tipo, propietario);
        v.setEspacio(espacio);
        v.setEstado(EstadoVehiculo.ESTACIONADO);
        v.setHoraEntrada(LocalDateTime.now());

        espacio.setEstado(EstadoEspacio.OCUPADO);
        espacio.setVehiculoActual(v);
        vehiculos.add(v);

        return v;
    }

    public Vehiculo registrarSalida(String placa)
            throws SesionNoAutorizadaException, TicketNoEncontradoException,
                   CobroNoPermitidoException {

        verificarPermiso("REGISTRAR_SALIDA");

        Vehiculo v = vehiculos.stream()
            .filter(ve -> ve.getPlaca().equalsIgnoreCase(placa)
                       && ve.getEstado() == EstadoVehiculo.ESTACIONADO)
            .findFirst()
            .orElseThrow(() -> new TicketNoEncontradoException(
                "No se encontró vehículo estacionado con placa: " + placa));

        v.setHoraSalida(LocalDateTime.now());
        v.setEstado(EstadoVehiculo.RETIRADO);

        EspacioParqueo espacio = v.getEspacio();
        if (espacio != null) {
            if(espacio.getColumna() == 1){
                espacio.setEstado(EstadoEspacio.DISCAPACIDAD);
            }else {
                espacio.setEstado(espacio.isEsDiscapacidad()
                        ? EstadoEspacio.DISCAPACIDAD
                        : EstadoEspacio.LIBRE);
            }
            espacio.setVehiculoActual(null);
        }

        return v;
    }

    // ── Consultas ─────────────────────────────────────────────
    public List<Vehiculo> getVehiculos() {
        return Collections.unmodifiableList(vehiculos);
    }

    public List<Vehiculo> getVehiculosEstacionados() {
        return vehiculos.stream()
            .filter(v -> v.getEstado() == EstadoVehiculo.ESTACIONADO)
            .collect(Collectors.toList());
    }

    public List<Vehiculo> buscar(String termino) {
        if (termino == null || termino.isBlank()) return getVehiculosEstacionados();
        String t = termino.toLowerCase();
        return vehiculos.stream()
            .filter(v -> v.getPlaca().toLowerCase().contains(t)
                      || v.getPropietario().toLowerCase().contains(t))
            .collect(Collectors.toList());
    }

    // ── Estadísticas ──────────────────────────────────────────
    public int totalEspacios() { return FILAS * COLUMNAS; }

    public long espaciosOcupados() {
        return Arrays.stream(mapa)
            .flatMap(Arrays::stream)
            .filter(e -> e.getEstado() == EstadoEspacio.OCUPADO).count();
    }

    public long espaciosLibres() {
        return Arrays.stream(mapa)
            .flatMap(Arrays::stream)
            .filter(EspacioParqueo::isDisponible).count();
    }

    public long espaciosDiscapacidad() {
        return Arrays.stream(mapa)
            .flatMap(Arrays::stream)
            .filter(e -> e.getEstado() == EstadoEspacio.DISCAPACIDAD).count();
    }   
}

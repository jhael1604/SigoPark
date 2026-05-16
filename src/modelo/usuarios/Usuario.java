package modelo.usuarios;

import modelo.enums.Rol;
import modelo.enums.tipoDocumento;

public abstract class Usuario {

    private String id;
    private String nombre;
    private String apellido;
    private tipoDocumento tipoDoc;
    private String numeroDocumento;
    private String username;
    private String password;
    private Rol rol;
    private boolean activo;

    public Usuario(String id, String nombre, String apellido,
                   tipoDocumento tipoDoc, String numeroDocumento,
                   String username, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDoc = tipoDoc;
        this.numeroDocumento = numeroDocumento;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreCompleto() { return nombre + " " + apellido; }
    public tipoDocumento getTipoDoc() { return tipoDoc; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }

    // ── Setters ──────────────────────────────────────────────
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setPassword(String password) { this.password = password; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean verificarCredenciales(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /** Cada subclase declara sus permisos específicos */
    public abstract boolean tienePermiso(String accion);

    @Override
    public String toString() {
        return getNombreCompleto() + " [" + rol.getDescripcion() + "]";
    }
}

package modelo.usuarios;

import modelo.enums.Rol;
import modelo.enums.tipoDocumento;

public class Administrador extends Usuario {

    private static final String[] PERMISOS = {
        "GESTIONAR_USUARIOS", "VER_REPORTES", "CONFIGURAR_SISTEMA",
        "REGISTRAR_ENTRADA", "REGISTRAR_SALIDA", "COBRAR", "VER_MAPA"
    };

    public Administrador(String id, String nombre, String apellido,
                         tipoDocumento tipoDoc, String numeroDocumento,
                         String username, String password) {
        super(id, nombre, apellido, tipoDoc, numeroDocumento, username, password, Rol.ADMIN);
    }

    @Override
    public boolean tienePermiso(String accion) {
        for (String p : PERMISOS) {
            if (p.equalsIgnoreCase(accion)) return true;
        }
        return false;
    }
}

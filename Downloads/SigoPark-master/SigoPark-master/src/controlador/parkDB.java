package controlador;

import java.sql.*;

public class parkDB {
    public static void main(String[] args) {
        // Datos de conexión
        String url = "Server=localhost\\SQLEXPRESS;Database=master;Trusted_Connection=True;";
        String user = "sa";
        String pass = "TuContraseña";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement st = con.createStatement()) {

            // 1. CREAR BASE DE DATOS
            st.executeUpdate("CREATE DATABASE BD_Prueba");
            System.out.println("Base de datos creada");

            // Conectarnos a la nueva base
            try (Connection con2 = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=BD_Prueba;encrypt=true;trustServerCertificate=true", user, pass);
                 Statement st2 = con2.createStatement()) {

                // 2. CREAR TABLA
                st2.executeUpdate("CREATE TABLE Personas (id INT, nombre VARCHAR(30))");
                System.out.println(" Tabla creada");

                // 3. INSERTAR
                st2.executeUpdate("INSERT INTO Personas VALUES (1, 'Luis'), (2, 'Marta')");
                System.out.println(" Datos guardados");

                // 4. CONSULTAR
                ResultSet rs = st2.executeQuery("SELECT * FROM Personas");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt(1) + " | Nombre: " + rs.getString(2));
                }

                // 5. MODIFICAR
                st2.executeUpdate("UPDATE Personas SET nombre = 'Luis Alberto' WHERE id = 1");
                System.out.println(" Dato modificado");

                // 6. ELIMINAR
                st2.executeUpdate("DELETE FROM Personas WHERE id = 2");
                System.out.println(" Dato eliminado");

            }
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }
}


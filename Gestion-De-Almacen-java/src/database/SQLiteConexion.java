package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQLiteConexion {

    public Connection conectar() {
        Connection conexion = null;
        try {
            // Registrar el driver JDBC manualmente
            Class.forName("org.sqlite.JDBC");
    
            // Obtener la ruta del directorio del proyecto de forma dinámica
            String projectPath = System.getProperty("user.dir");
    
            // Especificar la ubicación de la base de datos dentro de la carpeta "database"
            String dbPath = projectPath + "/databaseFile/baseDeDatos.db";
    
            // Crear la URL de conexión a SQLite
            String url = "jdbc:sqlite:" + dbPath;
    
            // Establecer la conexión
            conexion = DriverManager.getConnection(url);
            System.out.println("Conexión exitosa a la base de datos SQLite");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: El controlador JDBC no se pudo cargar.");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }

        return conexion;
    }

    public static void main(String[] args) {
        // Probar la conexión
        SQLiteConexion conexion = new SQLiteConexion();
        conexion.conectar();
    }
}

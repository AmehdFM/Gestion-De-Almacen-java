package database;

import java.sql.Connection;
import java.sql.PreparedStatement;  
import java.sql.ResultSet;          
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final SQLiteConexion conexion;

    public DatabaseManager() {
        this.conexion = new SQLiteConexion();
    }

    // Método para crear todas las tablas necesarias
    public void crearTablas() {
        crearTablaUsuarios();
        crearTablaAlmacenes();
        crearTablaArticulos();
        crearTablaClientes();
    }

    // Método para crear la tabla 'users'
    public void crearTablaUsuarios() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " username TEXT NOT NULL UNIQUE,"
                + " password TEXT NOT NULL,"
                + " role TEXT NOT NULL"
                + ");";
        ejecutarSQL(sql, "Tabla 'users' creada correctamente.");
    }

    // Método para crear la tabla 'almacenes'
    public void crearTablaAlmacenes() {
        String sql = "CREATE TABLE IF NOT EXISTS almacenes ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " nombre TEXT NOT NULL,"
            + " ubicacion TEXT NOT NULL,"
            + " area_disponible REAL NOT NULL"
            + ");";
        ejecutarSQL(sql, "Tabla 'almacenes' creada correctamente.");
    }

    // Método para crear la tabla 'articulos'
    public void crearTablaArticulos() {
        String sql = "CREATE TABLE IF NOT EXISTS articulos ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " descripcion TEXT,"
            + " cantidad INTEGER NOT NULL,"
            + " precio REAL NOT NULL,"
            + " almacen_id INTEGER NOT NULL,"
            + " FOREIGN KEY (almacen_id) REFERENCES almacenes(id) ON DELETE CASCADE"
            + ");";
        ejecutarSQL(sql, "Tabla 'articulos' creada correctamente.");
    }

    // Método para crear la tabla 'clientes'
    public void crearTablaClientes() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombre TEXT NOT NULL,"
                + " direccion TEXT NOT NULL,"
                + " telefono TEXT NOT NULL,"
                + " email TEXT"
                + ");";
        ejecutarSQL(sql, "Tabla 'clientes' creada correctamente.");
    }

    // Método genérico para ejecutar cualquier consulta SQL
    private void ejecutarSQL(String sql, String mensajeExito) {
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println(mensajeExito);
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta: " + e.getMessage());
        }
    }

    // Métodos CRUD para 'users'
    public void agregarUsuario(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // Idealmente, se debe hash la contraseña aquí
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            System.out.println("Usuario agregado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
        }
    }

    public void obtenerUsuarios() {
        String sql = "SELECT id, username, role FROM users";
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" + 
                    rs.getString("username") + "\t" +
                    rs.getString("role"));
            }
        } catch (SQLException e) {
            System.out.println("Error al recuperar usuarios: " + e.getMessage());
        }
    }

    public void actualizarUsuario(int id, String username, String password, String role) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // También se debe hash la nueva contraseña aquí
            pstmt.setString(3, role);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("Usuario actualizado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public void eliminarUsuario(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // Método para agregar usuario 'admin' por defecto si no existen usuarios
    public void crearUsuarioAdmin() {
        String sql = "SELECT COUNT(*) AS total FROM users";
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next() && rs.getInt("total") == 0) {
                // No hay usuarios en la base de datos, agregar el usuario 'admin'
                agregarUsuario("admin", "admin123", "admin");  // Recuerda, en producción debes encriptar la contraseña
                System.out.println("Usuario 'admin' creado con contraseña por defecto.");
            } else {
                System.out.println("Ya existen usuarios en el sistema.");
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar o agregar el usuario admin: " + e.getMessage());
        }
    }

    // Método main para pruebas (crear tablas y usuario admin por defecto)
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.crearTablas();  // Crear todas las tablas
        dbManager.crearUsuarioAdmin();  // Crear usuario admin si no existen usuarios
    }
}

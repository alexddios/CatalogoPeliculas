import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PeliculasDAO {

    // FIX 1 — barra normal en vez de \\\\ para que funcione en Windows, Mac y Linux
    private final String url = "jdbc:sqlite:data/peliculas.db";

    // FIX 5 — crear la tabla automáticamente si no existe al arrancar
    public PeliculasDAO() {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = """
                CREATE TABLE IF NOT EXISTS peliculas (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo   TEXT    NOT NULL,
                    director TEXT,
                    anio     INTEGER,
                    genero   TEXT,
                    vista    BOOLEAN DEFAULT FALSE
                )""";
            conn.createStatement().execute(sql);
        } catch (Exception e) {
            System.out.println("Error al crear la tabla: " + e);
        }
    }

    public void insertarPeliculas(List<Pelicula> peliculas) {
        String sql = """
            INSERT INTO peliculas(titulo, director, anio, genero, vista)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // Desactivamos el autocommit para agrupar todas las inserciones en una transacción
            conn.setAutoCommit(false);

            for (Pelicula p : peliculas) {
                ps.setString(1, p.getTitulo());
                ps.setString(2, p.getDirector());
                ps.setInt(3, p.getAnio());
                ps.setString(4, p.getGenero());
                // FIX 4 — isVista() en vez de getVista()
                ps.setBoolean(5, p.isVista());
                ps.addBatch();
            }

            ps.executeBatch(); // Ejecutamos todas las inserciones de golpe
            conn.commit();     // Confirmamos los cambios en la base de datos

        } catch (Exception e) {
            System.out.println("Error al insertar lote de películas: " + e);
        }
    }

    public List<Pelicula> seleccionarTodas() {
        List<Pelicula> res = new ArrayList<>();

        String sql = "SELECT * FROM peliculas";

        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                res.add(mapearPelicula(rs));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return res;
    }

    public List<Pelicula> buscarPeliculasTitulo(String titulo) {
        // FIX 2 — LIKE en vez de = para encontrar coincidencias parciales
        String sql = "SELECT * FROM peliculas WHERE titulo LIKE ?";
        return getPeliculas("%" + titulo + "%", sql);
    }

    public List<Pelicula> buscarPeliculasGenero(String genero) {
        // FIX 2 — LIKE en vez de = para encontrar coincidencias parciales
        String sql = "SELECT * FROM peliculas WHERE genero LIKE ?";
        return getPeliculas("%" + genero + "%", sql);
    }

    // Método privado reutilizable para búsquedas con un solo parámetro
    private List<Pelicula> getPeliculas(String parametro, String sql) {
        List<Pelicula> res = new ArrayList<>();
        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(mapearPelicula(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return res;
    }

    public int actualizarVista(int id, boolean vista) {
        String sql = """
            UPDATE peliculas
            SET vista = ?
            WHERE id = ?
            """;
        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setBoolean(1, vista);
            ps.setInt(2, id);
            return ps.executeUpdate(); // 1 si se modificó, 0 si el ID no existía
        } catch (Exception e) {
            System.out.println("Error al actualizar el estado de la película: " + e);
            return 0;
        }
    }

    public int eliminarPorTitulo(String titulo) {
        String sql = "DELETE FROM peliculas WHERE titulo = ?";
        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, titulo);
            return ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return 0;
        }
    }

    public void mostrarEstadisticas() {
        String sql = """
            SELECT genero, COUNT(*) AS total_genero, SUM(vista) AS vistas_genero
            FROM peliculas
            GROUP BY genero
            """;

        int totalGlobal = 0;
        int vistasGlobales = 0;

        System.out.println("\n====================== ESTADÍSTICAS DEL CATÁLOGO ======================");
        System.out.printf("%-15s | %-14s | %-16s | %-14s%n",
                "Género", "Total Pelis", "Películas Vistas", "Pendientes");
        System.out.println("-----------------------------------------------------------------------");

        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            boolean hayDatos = false;

            while (rs.next()) {
                hayDatos = true;
                String genero       = rs.getString("genero");
                int totalGenero     = rs.getInt("total_genero");
                int vistasGenero    = rs.getInt("vistas_genero");
                int pendientesGenero = totalGenero - vistasGenero;

                totalGlobal    += totalGenero;
                vistasGlobales += vistasGenero;

                System.out.printf("%-15s | %-14d | %-16d | %-14d%n",
                        genero, totalGenero, vistasGenero, pendientesGenero);
            }

            if (!hayDatos) {
                System.out.println("   No hay datos suficientes en el catálogo para calcular estadísticas.");
            } else {
                int pendientesGlobales = totalGlobal - vistasGlobales;
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("RESUMEN GLOBAL:");
                System.out.println("  Total de películas en el catálogo: " + totalGlobal);
                System.out.println("  ✅ Total películas ya vistas:      " + vistasGlobales);
                System.out.println("  ❌ Total películas pendientes:     " + pendientesGlobales);
            }

        } catch (Exception e) {
            System.out.println("Error al calcular las estadísticas: " + e);
        }
        System.out.println("=======================================================================");
    }

    // Método privado auxiliar para construir una Pelicula desde un ResultSet
    // Evita repetir las 6 líneas de rs.getXxx() en cada método
    private Pelicula mapearPelicula(ResultSet rs) throws Exception {
        return new Pelicula(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("director"),
                rs.getInt("anio"),
                rs.getString("genero"),
                rs.getBoolean("vista")
        );
    }
}
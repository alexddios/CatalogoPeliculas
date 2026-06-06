import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PeliculasDAO {
    private final String url = "jdbc:sqlite:data\\\\peliculas.db";

    public void insertarPeliculas(List<Pelicula> peliculas) {
        String sql = """
            INSERT INTO peliculas(titulo,director,anio,genero,vista)
            VALUES (?,?,?,?,?)
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
                ps.setBoolean(5, p.getVista());

                ps.addBatch(); // En lugar de ejecutar, lo añadimos al "lote"
            }

            ps.executeBatch(); // Ejecutamos todas las inserciones de golpe
            conn.commit();     // Confirmamos los cambios en la base de datos

        } catch (Exception e) {
            System.out.println("Error al insertar lote de películas: " + e);
        }
    }
    public List<Pelicula> seleccionarTodas(){
        List<Pelicula> res = new ArrayList<>();

        String sql = """
                SELECT *
                FROM peliculas
                """;
        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ){
            while (rs.next()){
                Pelicula p = new Pelicula(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("director"),
                        rs.getInt("anio"),
                        rs.getString("genero"),
                        rs.getBoolean("vista")
                );
                res.add(p);
            }

        }catch (Exception e){
            System.out.println("Error: " + e);
        }
        return res;
    }
    public List<Pelicula> buscarPeliculasTitulo(String titulo){
        List<Pelicula> res = new ArrayList<>();

        String sql = """
                SELECT * FROM peliculas
                WHERE titulo = ?
                """;
        return getPeliculas(titulo, res, sql);
    }
    public List<Pelicula> buscarPeliculasGenero(String genero){
        List<Pelicula> res = new ArrayList<>();

        String sql = """
                SELECT * FROM peliculas
                WHERE genero = ?
                """;
        return getPeliculas(genero, res, sql);
    }

    private List<Pelicula> getPeliculas(String genero, List<Pelicula> res, String sql) {
        try (
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,genero);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Pelicula p = new Pelicula(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("director"),
                            rs.getInt("anio"),
                            rs.getString("genero"),
                            rs.getBoolean("vista")
                    );
                    res.add(p);
                }
            }
        }catch (Exception e){
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

            // Devuelve 1 si se modificó correctamente, o 0 si el ID no existía
            return ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error al actualizar el estado de la película: " + e);
            return 0;
        }
    }

}

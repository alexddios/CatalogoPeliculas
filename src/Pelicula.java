public class Pelicula {
    private Integer id;
    private String titulo;
    private String director;
    private int anio;
    private String genero;
    private boolean vista;

    public Pelicula(int id,String titulo,String director, int anio,String genero,boolean vista){
        this.id=id;
        this.titulo=titulo;
        this.director=director;
        this.anio=anio;
        this.genero=genero;
        this.vista = vista;
    }

    public Pelicula(String titulo, String director, int anio, String genero, boolean vista) {
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.genero = genero;
        this.vista = vista;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDirector() {
        return director;
    }

    public int getAnio() {
        return anio;
    }

    public String getGenero() {
        return genero;
    }

    public boolean isVista() {
        return vista;
    }


    @Override
    public String toString() {
        String estadoVista = this.vista ? "✅ Vista" : "❌ Pendiente";
        return String.format("ID: %d | Título: %s | Director: %s | Año: %d | Género: %s | %s",
                id, titulo, director, anio, genero, estadoVista);
    }
}

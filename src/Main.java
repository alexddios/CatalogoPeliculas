Scanner scanner = new Scanner(System.in);
PeliculasDAO dao = new PeliculasDAO();

void main() {
    System.out.println("""
            Elige un número dependendo de lo que quieras hacer:
            1. Añadir película
            2. Listar todas las películas
            3. Buscar por título
            4. Buscar por género
            5. Marcar película como vista / no vista
            6. Eliminar película por título
            7. Estadísticas: cuántas vistas, cuántas pendientes, por género
            """);
    System.out.print("Introduce el número: ");
    int ejercicio = comprobarEjercicio();
    switch (ejercicio){
        case 1-> aniadirPeliculas();
        case 2 -> listarPeliculas();
        case 3 -> buscarTitulo();
        case 4 -> buscarGenero();
        case 5 -> marcarVista();
        case 6 -> eliminarTitulo();
        case 7 -> estadisticas();
        default -> System.out.println("Opción incorrecta");
    }

}
private int comprobarEjercicio() {
    int res = 0;
    boolean valido = false;

    while (!valido) {
        try {
            // Intentamos convertir lo que escriba el usuario a número
            res = Integer.parseInt(scanner.nextLine());

            // Si funciona, comprobamos el rango
            if (res >= 1 && res <= 7) {
                valido = true; // Es correcto, salimos del bucle
            } else {
                System.out.print("Número fuera de rango. Introduce un número del 1 al 7: ");
            }

        } catch (NumberFormatException e) {
            // Si falla la conversión (no era un número), lo capturamos aquí
            System.out.print("Entrada no válida. Por favor, introduce un número: ");
        }
    }

    return res;
}
public void aniadirPeliculas() {
    List<Pelicula> listaNuevas = new ArrayList<>();
    boolean seguir = true;

    System.out.println("\n--- Añadir Múltiples Películas ---");

    while (seguir) {
        String titulo = pedirTexto("Introduce el título: ");
        String director = pedirTexto("Introduce el director: ");
        String genero = pedirTexto("Introduce el género: ");
        int anio = pedirNumero("Introduce el año de lanzamiento: ");
        boolean vista = pedirConfirmacion("¿Ya la has visto? (s/n): ");

        // Añadimos a la lista temporal
        listaNuevas.add(new Pelicula(titulo, director, anio, genero, vista));

        System.out.println("-----------------------------------");
        // Preguntamos si quiere repetir el bucle
        seguir = pedirConfirmacion("¿Quieres añadir otra película? (s/n): ");
    }

    // Cuando el usuario dice que no, le pasamos la lista completa al DAO
    if (!listaNuevas.isEmpty()) {
        dao.insertarPeliculas(listaNuevas);
        System.out.println("✅ ¡Se han guardado " + listaNuevas.size() + " películas con éxito en la base de datos!");
    }
}
// Método auxiliar para textos simples
private String pedirTexto(String mensaje) {
    System.out.print(mensaje);
    return scanner.nextLine();
}

// Método auxiliar para números (reutilizando la lógica segura que ya aprendiste)
private int pedirNumero(String mensaje) {
    int numero = 0;
    boolean valido = false;
    while (!valido) {
        System.out.print(mensaje);
        try {
            numero = Integer.parseInt(scanner.nextLine());
            valido = true;
        } catch (NumberFormatException e) {
            System.out.println("Error: Por favor, introduce un número válido.");
        }
    }
    return numero;
}

// Método auxiliar para booleanos (sí o no)
private boolean pedirConfirmacion(String mensaje) {
    while (true) {
        System.out.print(mensaje);
        String respuesta = scanner.nextLine().trim().toLowerCase();
        if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
            return true;
        } else if (respuesta.equals("n") || respuesta.equals("no")) {
            return false;
        } else {
            System.out.println("Error: Responde con 's' para sí o 'n' para no.");
        }
    }
}

public void listarPeliculas() {
    System.out.println("\n=================================== CATÁLOGO DE PELÍCULAS ===================================");

    // Recuperamos la lista desde el DAO
    List<Pelicula> todas = dao.seleccionarTodas();

    // Validamos si la base de datos está vacía
    if (todas.isEmpty()) {
        System.out.println("   No hay películas registradas en el catálogo actual.");
    } else {
        // Recorremos la lista. Al pasar el objeto 'p' a System.out.println, Java llama automáticamente a su toString()
        for (Pelicula p : todas) {
            System.out.println(p);
        }
    }

    System.out.println("=============================================================================================");
}
public void buscarTitulo() {
    System.out.println("\n--- Buscar Película por Título ---");
    String titulo = pedirTexto("Introduce el título a buscar: ");

    System.out.println("\n=================================== RESULTADOS DE BÚSQUEDA ==================================");

    // Recogemos la lista que devuelve el DAO
    java.util.List<Pelicula> encontradas = dao.buscarPeliculasTitulo(titulo);

    if (encontradas.isEmpty()) {
        System.out.println("   No se encontró ninguna película con el título: \"" + titulo + "\"");
    } else {
        // Al imprimir 'p', Java llamará automáticamente a tu nuevo toString() alineado
        for (Pelicula p : encontradas) {
            System.out.println(p);
        }
    }

    System.out.println("=============================================================================================");
}

public void buscarGenero() {
    System.out.println("\n--- Buscar Película por Género ---");
    String genero = pedirTexto("Introduce el género a buscar: ");

    System.out.println("\n=================================== PELÍCULAS POR GÉNERO ===================================");

    // Recogemos la lista que devuelve el DAO
    java.util.List<Pelicula> encontradas = dao.buscarPeliculasGenero(genero);

    if (encontradas.isEmpty()) {
        System.out.println("   No se encontraron películas registradas bajo el género: \"" + genero + "\"");
    } else {
        for (Pelicula p : encontradas) {
            System.out.println(p);
        }
    }

    System.out.println("=============================================================================================");
}
public void marcarVista() {
    System.out.println("\n--- Marcar Película como Vista / No Vista ---");

    // 1. El consejo: Mostramos la lista primero para ver los IDs disponibles
    listarPeliculas();

    boolean seguir = true;
    int contadorModificadas = 0;

    // 2. Bucle para permitir modificar más de una película seguida
    while (seguir) {
        System.out.println("\n-------------------------------------------");
        int id = pedirNumero("Introduce el ID de la película: ");
        boolean vista = pedirConfirmacion("¿La has visto? (s/n): ");

        // Llamamos al DAO para actualizar esta película individualmente
        int filasModificadas = dao.actualizarVista(id, vista);

        if (filasModificadas > 0) {
            System.out.println("✅ ¡Película con ID " + id + " actualizada!");
            contadorModificadas++;
        } else {
            System.out.println("❌ No se encontró ninguna película con el ID " + id + ".");
        }

        // Preguntamos si quiere repetir el bucle
        seguir = pedirConfirmacion("\n¿Quieres modificar otra película? (s/n): ");
    }

    // Al salir del bucle, damos un resumen de los cambios
    System.out.println("\n-------------------------------------------");
    if (contadorModificadas > 0) {
        System.out.println("✅ Proceso terminado. Se han actualizado " + contadorModificadas + " películas.");
    } else {
        System.out.println("Proceso terminado. No se realizó ninguna modificación.");
    }
}
public void eliminarTitulo(){
    System.out.println("\n--- Eliminar Película por Título ---");
    listarPeliculas();

    boolean seguir = true;
    int contadorEliminadas = 0;

    while (seguir){
        System.out.println("\n-------------------------------------------");
        String titulo = pedirTexto("Introduce el título: ");

        int filasModificadas = dao.eliminarPorTitulo(titulo);

        if(filasModificadas>0){
            System.out.println("✅ ¡Película con título " + titulo + " eliminada!");
            contadorEliminadas++;
        }else{
            System.out.println("❌ No se encontró ninguna película con el título " + titulo + ".");
        }
        seguir = pedirConfirmacion("\n¿Quieres eliminar otra película? (s/n): ");
    }
    System.out.println("\n-------------------------------------------");
    if (contadorEliminadas > 0) {
        System.out.println("✅ Proceso terminado. Se han eliminado " + contadorEliminadas + " películas.");
    } else {
        System.out.println("Proceso terminado. No se realizó ninguna modificación.");
    }
}
public void estadisticas() {
    dao.mostrarEstadisticas();
}

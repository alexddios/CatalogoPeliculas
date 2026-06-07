# 🎬 Catálogo de Películas

Proyecto académico de **1º DAM** (Desarrollo de Aplicaciones Multiplataforma).  
Aplicación de consola en Java para gestionar un catálogo personal de películas, con persistencia en una base de datos **SQLite** mediante **JDBC**.

---

## 🖥️ Captura de pantalla

```
Elige un número dependiendo de lo que quieras hacer:
1. Añadir película
2. Listar todas las películas
3. Buscar por título
4. Buscar por género
5. Marcar película como vista / no vista
6. Eliminar película por título
7. Estadísticas: cuántas vistas, cuántas pendientes, por género

Introduce el número: 2

=================================== CATÁLOGO DE PELÍCULAS ===================================
ID: 1 | Título: Inception | Director: Christopher Nolan | Año: 2010 | Género: Ciencia ficción | ✅ Vista
ID: 2 | Título: Interstellar | Director: Christopher Nolan | Año: 2014 | Género: Ciencia ficción | ❌ Pendiente
ID: 3 | Título: El Padrino | Director: Francis Ford Coppola | Año: 1972 | Género: Drama | ✅ Vista
=============================================================================================
```

---

## ✨ Funcionalidades

| Opción | Función |
|--------|---------|
| 1 | Añadir una o varias películas seguidas (título, director, género, año, vista) |
| 2 | Listar todo el catálogo |
| 3 | Buscar películas por título (búsqueda parcial) |
| 4 | Buscar películas por género (búsqueda parcial) |
| 5 | Marcar una película como vista o pendiente por ID |
| 6 | Eliminar una película por título |
| 7 | Ver estadísticas por género: total, vistas y pendientes |

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|-----------|-----|
| Java SE 21 | Lenguaje principal |
| SQLite | Base de datos local embebida |
| JDBC | Conexión y operaciones con la base de datos |
| `PreparedStatement` | Queries parametrizadas (sin SQL injection) |
| `try-with-resources` | Cierre automático de conexiones |
| Transacciones JDBC | Inserciones en lote con `executeBatch()` |

---

## 🏗️ Estructura del proyecto

```
catalogo-peliculas/
├── src/
│   ├── Main.java           ← Menú principal y lógica de interacción con el usuario
│   ├── Pelicula.java       ← Clase modelo con los datos de cada película
│   └── PeliculasDAO.java   ← Acceso a la base de datos (todas las queries SQL)
└── data/
    └── peliculas.db        ← Se crea automáticamente al arrancar la app
```

### Responsabilidad de cada clase

- **`Pelicula.java`** — Almacena los datos: `id`, `titulo`, `director`, `anio`, `genero`, `vista`. Dos constructores: uno con ID (para cargar desde BD) y otro sin ID (para insertar nuevas).
- **`PeliculasDAO.java`** — Toda la comunicación con SQLite. Crea la tabla si no existe, inserta, consulta, actualiza y elimina.
- **`Main.java`** — Muestra el menú, recoge la entrada del usuario y llama a los métodos del DAO. Incluye métodos auxiliares para validar entradas (`pedirTexto`, `pedirNumero`, `pedirConfirmacion`).

---

## 🚀 Cómo ejecutar

### Requisitos
- Java 21 o superior
- IntelliJ IDEA (recomendado)
- Driver JDBC de SQLite: [sqlite-jdbc en GitHub](https://github.com/xerial/sqlite-jdbc/releases)

### Desde IntelliJ IDEA
1. Abre el proyecto con `File → Open`
2. Añade el driver SQLite: `File → Project Structure → Libraries → + → Java` y selecciona el `.jar` descargado
3. Ejecuta `Main.java`
4. La base de datos `peliculas.db` se creará automáticamente en la carpeta `data/`

### Desde terminal
```bash
# Compilar (con el .jar del driver en la misma carpeta)
javac -cp sqlite-jdbc.jar src/*.java -d out

# Ejecutar
java -cp "out:sqlite-jdbc.jar" Main
# En Windows: java -cp "out;sqlite-jdbc.jar" Main
```

---

## 🗄️ Esquema de la base de datos

```sql
CREATE TABLE IF NOT EXISTS peliculas (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo   TEXT    NOT NULL,
    director TEXT,
    anio     INTEGER,
    genero   TEXT,
    vista    BOOLEAN DEFAULT FALSE  
);
```

---

## 💡 Qué aprendí con este proyecto

- Cómo conectar Java con una base de datos real usando **JDBC** y el driver de SQLite
- La diferencia entre `Statement` y `PreparedStatement`, y por qué el segundo es siempre mejor
- Cómo usar **`try-with-resources`** para garantizar que las conexiones se cierran aunque haya un error
- Qué son las **transacciones** y cómo `setAutoCommit(false)` + `executeBatch()` hace las inserciones múltiples mucho más eficientes
- Separar la lógica de la interfaz de usuario (Main) de la lógica de acceso a datos (DAO)

---

## 🔮 Posibles mejoras futuras

- [ ] Añadir búsqueda por director
- [ ] Añadir fecha en la que se vio la película
- [ ] Exportar el catálogo a un fichero CSV o XML
- [ ] Interfaz gráfica con JavaFX (2º DAM)
- [ ] Añadir puntuación personal (1-10) a cada película

---

## 👤 Autor

**Álex De Dios** — Estudiante de 1º DAM  
[GitHub](https://github.com/alexddios) · [LinkedIn](https://www.linkedin.com/in/alex-de-dios-pallicer-ab44a5409/)
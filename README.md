# Comedor Universitario UV

Aplicación móvil desarrollada en **Kotlin** para la gestión, consulta de menús y optimización del servicio del Comedor Universitario de la Universidad Veracruzana (UV).

---

## Requisitos del Entorno

* **Android Studio** (versión estable recomendada).
* **JDK 17** o superior.
* **Kotlin** + Gradle configurado en el proyecto.
* **Git** configurado con tu cuenta de GitHub.

### Configuración inicial
1. Clona el repositorio en tu equipo:
   ```bash
   git clone https://github.com/USUARIO/NOMBRE_DEL_REPOSITORIO.git
   cd NOMBRE_DEL_REPOSITORIO
   ```
2. Abre el proyecto en Android Studio y espera a que **Gradle** sincronice todas las dependencias antes de compilar.

---

## Flujo de Trabajo en Git (Ramas y Pull Requests)

Para mantener estable el proyecto y evitar conflictos o pérdida de código, **está estrictamente prohibido hacer commits o `push` directamente a la rama `main`**. Todo desarrollo o prueba debe realizarse en ramas independientes.

### 1. Crea tu propia rama para trabajar y hacer pruebas
Antes de empezar una tarea, asegúrate de tener la última versión de `main` y crea una rama nueva usando los siguientes prefijos:

* `feature/nombre-tarea` → Para nuevas pantallas, funciones o lógica.
* `fix/nombre-error` → Para corrección de bugs.
* `test/nombre-prueba` → Para experimentar o realizar pruebas aisladas sin afectar el código funcional.

```bash
# 1. Cambia a la rama principal y descárgate los últimos cambios
git checkout main
git pull origin main

# 2. Crea y muévete a tu rama de trabajo
git checkout -b feature/consulta-menu-diario
```

### 2. Desarrolla y prueba localmente
* Realiza tus cambios y compila la aplicación en el emulador o en un dispositivo físico para verificar que no existan cierres inesperados (*crashes*) ni errores de compilación.
* Asegúrate de no dejar archivos temporales, rutas locales absolutas ni claves privadas sin ignorar en el `.gitignore`.

### 3. Sube tu rama y abre un Pull Request (PR)
Una vez que tu código funcione y esté probado:

```bash
git push -u origin feature/consulta-menu-diario
```

1. Ve al repositorio en GitHub y haz clic en **Compare & pull request**.
2. Selecciona como rama base `main` y como rama de comparación tu rama (`feature/...`).
3. En la descripción del PR detalla:
   * **Qué se hizo:** Resumen breve de los cambios implementados.
   * **Cómo probarlo:** Pasos para verificar la funcionalidad en la app.
4. Espera a que al menos un integrante del equipo revise el código antes de hacer el **Merge**. Una vez integrado a `main`, puedes eliminar tu rama de trabajo.

---

## Estándares para Commits

Evita mensajes genéricos como `"cambios"`, `"fix"`, `"ya quedó"` o `"final"`. Cada commit debe representar un cambio lógico específico y describir claramente qué hace.

Se recomienda usar el formato **`tipo(módulo): descripción breve en imperativo`**:

* `feat`: Nueva funcionalidad.
* `fix`: Corrección de un error.
* `refactor`: Mejora de código sin cambiar su comportamiento.
* `docs`: Cambios en documentación o comentarios.
* `style`: Formato, indentación o ajustes visuales de UI.

### Ejemplos
* **Correcto:** `feat(menu): agregar filtrado de platillos por horario de comida`
* **Correcto:** `fix(login): validar formato de matrícula UV antes de enviar petición`
* **Incorrecto:** `subiendo avances del martes`

---

## Calidad y Comentarios en el Código (Kotlin)

El código debe ser legible para cualquier integrante del equipo sin necesidad de adivinar qué hace cada bloque:

1. **Documenta clases y funciones públicas con KDoc (`/** ... */`):** Explica el propósito de la función, sus parámetros (`@param`) y lo que devuelve (`@return`).
2. **Usa comentarios en línea (`//`) con criterio:** No comentes lo obvio (ej. `// suma 1 al contador`), comenta el **porqué** de una decisión lógica, validaciones especiales o reglas de negocio del comedor.
3. **Convenciones de nombres:**
   * Clases e interfaces en `PascalCase` (ej. `MenuRepository`, `PlatilloAdapter`).
   * Funciones y variables en `camelCase` (ej. `obtenerMenuDelDia()`, `precioSubsidiado`).
   * Constantes en `UPPER_SNAKE_CASE` (ej. `MAX_RESERVAS_POR_ESTUDIANTE`).

### Ejemplo de referencia
```kotlin
/**
 * Calcula el monto total de la orden aplicando el subsidio estudiantil vigente.
 *
 * @param platillos Lista de platillos seleccionados en el carrito.
 * @param tieneSubsidio Indica si la matrícula cuenta con beca alimenticia activa.
 * @return Total a pagar en pesos mexicanos (MXN).
 */
fun calcularTotalOrden(platillos: List<Platillo>, tieneSubsidio: Boolean): Double {
    val subtotal = platillos.sumOf { it.precio }

    // El subsidio cubre el 50% únicamente en el menú básico del día
    return if (tieneSubsidio) subtotal * 0.5 else subtotal
}
```

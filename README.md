# Sistema de Gestión de Tutorías Académicas (UEES)

## 1. Propósito del Proyecto

El propósito del sistema es automatizar y gestionar eficientemente el agendamiento, seguimiento y cancelación de tutorías académicas, integrando a estudiantes, docentes y distintos canales de comunicación de forma desacoplada y mantenible.

---

## 2. Problema y Alcance del Incremento

- **Problema:** Los sistemas tradicionales de tutorías presentan alto acoplamiento al gestionar múltiples canales de notificación, construcciones complejas de reservas con parámetros opcionales y reglas de cancelación rígidas.
- **Alcance:** Implementar un incremento modular que consolida el modelo de dominio base (Ae1), la creación dinámica de canales de notificación y construcción fluida de reservas (Ae2), e incorpora el manejo de eventos por cambio de estado y políticas dinámicas de cancelación (Ae3).

---

## 3. Clases / Componentes Principales

- **`Usuario` (Clase Base):** Encapsula atributos comunes (`id`, `nombre`, `email`, `cedula`).
- **`Estudiante` / `Docente`:** Subclases que representan a los actores del sistema.
- **`Reserva`:** Objeto central de dominio que coordina la cita y su estado (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`).
- **`HorarioTutoria` & `Materia`:** Representan la disponibilidad del docente y la asignatura.
- **`ComprobanteAsistencia`:** Genera el registro físico/digital post-tutoría.

---

## 4. Patrones Utilizados y Justificación

- **Abstract Factory (`patrones.factory`):** Permite instanciar notificaciones por Email, SMS, Teams y WhatsApp sin acoplar el código a clases concretas ni usar estructuras `if/else`.
- **Builder (`patrones.builder`):** Facilita la creación paso a paso de objetos `Reserva` complejos mediante Fluent API, gestionando campos obligatorios y opcionales sin sobrecargar constructores.
- **Observer (`patrones.observer`):** Desacopla la lógica de eventos; al confirmar o cancelar una reserva, se notifica automáticamente a observadores como `EmailReservaObserver` y `LogReservaObserver`.
- **Strategy (`patrones.strategy`):** Encapsula la lógica de validación para cancelaciones (`Estandar` vs `Prioritaria`), permitiendo cambiar las reglas de negocio dinámicamente sin modificar la entidad `Reserva`.

---

## 5. Principios SOLID Relevantes

- **Single Responsibility Principle (SRP):** Cada clase tiene una única responsabilidad (ej. `ServicioReservas` gestiona la lógica, `ReservaBuilder` solo construye el objeto).
- **Open/Closed Principle (OCP):** Se pueden agregar nuevos canales de notificación o nuevas estrategias de cancelación sin modificar las clases existentes.
- **Dependency Inversion Principle (DIP):** Las clases de alto nivel dependen de abstracciones (interfaces como `Notificador`, `ReservaObserver`, `EstrategiaCancelacion`) y no de implementaciones concretas.

---

## 5.1. Refactorización Aplicada (Ae4)

En este incremento se aplicó una reestructuración interna sobre el componente `ServicioReservas.java` para resolver problemas de mantenibilidad, acoplamiento y legibilidad (_code smells_), garantizando la paridad funcional mediante la ejecución de pruebas con Maven.

### Técnicas Aplicadas

- **Guard Clauses (Cláusulas de Guarda):** Se eliminó el anidamiento profundo (_Deep Nesting_) reemplazando bloques condicionales complejos por retornos tempranos ante valores nulos o estados no válidos.
- **Extract Constant (Extracción de Constantes):** Se sustituyeron las cadenas de texto literales (_Magic Strings_) como `"RES-"` y `"Tu reserva de tutoría ha sido registrada."` por constantes estáticas privadas (`PREFIJO_RESERVA_ID` y `MENSAJE_CONFIRMACION`).
- **Extract Method (Extracción de Métodos):** Se descompuso el método orquestador extrayendo responsabilidades específicas a métodos privados auxiliares:
    - `esHorarioValido()`: Centraliza la regla de validación de disponibilidad del horario.
    - `procesarYGuardarReserva()`: Gestiona la reserva de cupo y la persistencia en el repositorio.
    - `notificarEstudiante()`: Encapsula el envío del mensaje de confirmación.

### Impacto en el Diseño

- **Legibilidad y Flujo Lineal:** El método principal pasó de un flujo condicional anidado a una secuencia plana y limpia.
- **Principio de Responsabilidad Única (SRP):** Cada método privado asume una tarea puntual dentro del ciclo de vida de la reserva.
- **Seguridad Defensiva:** Se incorporaron validaciones oportunas para prevenir excepciones inesperadas del tipo `NullPointerException`.

---

## 6. Tecnologías Utilizadas

- **Lenguaje:** Java 17
- **Gestor de Dependencias:** Apache Maven 3.9+
- **Modelado UML:** PlantUML

---

## 7. Cómo Compilar y Ejecutar

1. **Compilar el proyecto:**
    ```bash
    mvn clean compile
    mvn exec:java -Dexec.mainClass="edu.uees.tutorias.Main"
    ```

## Estructura del Proyecto

```text
sistema-tutorias/
├── docs/
│   ├── builder.puml
│   ├── factory-method.png
│   ├── factory-method.puml
│   ├── modelo-clases.png
│   └── modelo-clases.puml
├── src/
│   └── main/
│       └── java/
│           └── edu/uees/tutorias/
│               ├── Main.java
│               ├── domain/
│               │   ├── ComprobanteAsistencia.java
│               │   ├── Docente.java
│               │   ├── Estudiante.java
│               │   ├── HorarioTutoria.java
│               │   ├── Materia.java
│               │   ├── Reserva.java
│               │   └── Usuario.java
│               ├── notification/
│               │   ├── Notificacion.java
│               │   └── Notificador.java
│               ├── patrones/
│               │   ├── builder/
│               │   │   └── ReservaBuilder.java
│               │   ├── factory/
│               │   │   ├── EmailNotificadorFactory.java
│               │   │   ├── Notificacion.java
│               │   │   ├── NotificacionEmail.java
│               │   │   ├── NotificacionSMS.java
│               │   │   ├── NotificacionTeams.java
│               │   │   ├── NotificacionWhatsApp.java
│               │   │   ├── NotificadorFactory.java
│               │   │   ├── SMSNotificadorFactory.java
│               │   │   ├── TeamsNotificadorFactory.java
│               │   │   └── WhatsAppNotificadorFactory.java
│               │   ├── observer/
│               │   │   ├── EmailReservaObserver.java
│               │   │   ├── LogReservaObserver.java
│               │   │   └── ReservaObserver.java
│               │   └── strategy/
│               │       ├── CancelacionEstandarStrategy.java
│               │       ├── CancelacionPrioritariaStrategy.java
│               │       └── EstrategiaCancelacion.java
│               └── service/
│                   ├── RepositorioReservas.java
│                   └── ServicioReservas.java
└── pom.xml
```

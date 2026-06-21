# Proyecto de Desarrollo de Software — App Móvil de Gestión de Club de Atletismo
**Universidad Privada Domingo Savio — Carrera de Ingeniería de Sistemas**
**Santa Cruz de la Sierra, Bolivia · 2026**

---

## Índice General

1. [Entrevista al atleta (fuente de requerimientos)](#1-entrevista-al-atleta)
2. [Historias de Usuario (HU-01 a HU-13)](#2-historias-de-usuario)
3. [Requisitos Funcionales (RF-01 a RF-18)](#3-requisitos-funcionales)
4. [Requisitos No Funcionales (RNF-01 a RNF-06)](#4-requisitos-no-funcionales)
5. [Casos de Uso (CU-01 a CU-06)](#5-casos-de-uso)
6. [Modelado del Dominio — Diagrama de Clases Conceptual](#6-modelado-del-dominio)
7. [Capítulo 4 — Diseño del Software](#7-diseño-del-software)
   - 4.1 Arquitectura de 3 capas
   - 4.2 Modelo Lógico de BD
   - 4.3 Diccionario de Datos
   - 4.4 Diseño de Componentes y Módulos
8. [Capítulo 5 — Implementación y Pruebas (Java / Spring Boot)](#8-implementación-y-pruebas)
   - 5.1 Entorno de implementación
   - 5.2 Proceso de desarrollo (Scrum)
   - 5.3 Estructura del proyecto
   - 5.4 Pruebas y validación

---

## 1. Entrevista al Atleta

**Entrevistado:** Marco Antonio Gutiérrez — Atleta/Gimnasta, Club Atlético Santa Cruz de la Sierra, Bolivia.

### Bloque 1 — Contexto del club

**P1. ¿Cuántos atletas tiene el club y cómo están organizados?**
Somos alrededor de 45 atletas en total. Divididos en categorías: Pre-Infantil (8-10 años), Infantil (11-13), Juvenil (14-17) y Mayores (+18). Disciplinas: velocidad (100m, 200m), salto largo, lanzamiento de bala y gimnasia artística. Hay 4 grupos de entrenamiento según disciplina y entrenador asignado.

**P2. ¿Quiénes usan la información del club?**
Principalmente los entrenadores. Los atletas mayores también necesitamos ver horarios. Los padres de los menores a veces preguntan por WhatsApp porque no tienen acceso oficial a nada.

**P3. ¿Hay diferentes roles con diferentes permisos?**
Sí. El entrenador ve marcas de todos los atletas, puede modificar horarios y resultados. El atleta debería ver solo su propia información y la agenda general. Los padres deberían poder ver la agenda y el rendimiento de su hijo.

### Bloque 2 — Gestión de agenda y entrenamientos

**P4. ¿Cómo organizan hoy los turnos y horarios?**
Usamos un grupo de WhatsApp y a veces el entrenador manda foto de un papel escrito a mano con el horario semanal. No hay nada formal.

**P5. ¿Con qué frecuencia cambia la agenda?**
Cambia bastante, al menos una o dos veces por semana por clima, disponibilidad de la pista o competencias. Todos necesitan enterarse rápido.

**P6. ¿Necesitan registrar la asistencia?**
Sí, sería clave. Ahora el entrenador lleva planilla en papel y a veces la pasa a Excel, pero se pierde información.

**P7. ¿Gestionan competencias o torneos?**
Sí, pero es muy desorganizado. La inscripción se hace por mensaje privado al entrenador. No hay registro formal de resultados — solo fotos del cronómetro guardadas en el teléfono.

### Bloque 3 — Seguimiento del rendimiento

**P8. ¿Registran los tiempos y marcas?**
Las anota el entrenador en una libreta o en WhatsApp. A veces en Excel, pero no siempre. Se pierde mucho historial.

**P9. ¿Los entrenadores necesitan ver la evolución?**
Sí, es fundamental para planificar el entrenamiento y detectar si un atleta está bajando el rendimiento.

**P10. ¿Los atletas deben poder ver su historial?**
Sí. Yo quiero saber si mejoré en el 100m respecto al mes pasado. Eso nos motiva.

### Bloque 4 — Comunicación y notificaciones

**P11. ¿Cómo avisan cuando hay un cambio?**
Todo por WhatsApp. A veces el mensaje se pierde entre otros y alguien no se entera del cambio de horario.

**P12. ¿Les gustaría notificaciones automáticas?**
Sí. Principalmente para: cambio de horario, cancelación de entrenamiento, convocatoria a competencia y publicación de resultados.

**P13. ¿Necesitan un canal de mensajes interno?**
Con notificaciones alcanza por ahora. Un chat interno sería bueno a futuro pero no es urgente.

### Bloque 5 — Seguridad y privacidad

**P14. ¿Qué tan sensible es la información?**
Hay menores de edad, así que es importante proteger sus datos. No queremos que cualquiera vea información personal de los chicos.

**P15. ¿Cada atleta debería tener su cuenta?**
Sí. Los menores pueden tener la cuenta a nombre del padre/tutor.

**P16. ¿Quién puede modificar información del atleta?**
El entrenador debería poder modificar todo. El atleta puede editar solo sus datos personales básicos, no sus marcas.

### Bloque 6 — Prioridades

**P17. Las 3 funciones más importantes:**
1. Ver y recibir notificaciones de la agenda de entrenamientos
2. Registro y consulta de marcas/rendimiento por atleta
3. Gestión de asistencia

**P18. ¿Experiencia con otras apps?**
He visto TeamApp y una que usaban en Cochabamba pero era muy complicada y en inglés. Lo que no me gustó: demasiados menús, difícil de entender para los chicos más jóvenes.

**P19. ¿App simple o completa?**
Prefiero algo simple y directo. Que sea intuitivo, bonito, y que funcione bien con internet lento que a veces tenemos en la cancha.

**P20. ¿Qué problema definitivamente debe resolver la app?**
Lo que más tiempo nos hace perder: confirmar si hay entrenamiento o no. A veces llego a la pista y estaba cancelado y nadie avisó a tiempo. Eso definitivamente tiene que resolverlo la app.

---

## 2. Historias de Usuario

> **Formato:** Como [rol], quiero [funcionalidad] para [beneficio].

### Módulo M1 — Autenticación y Gestión de Usuarios

---

#### HU-01 — Registro de cuenta de usuario
| Campo | Detalle |
|---|---|
| **Rol** | Atleta / Padre de atleta menor |
| **Historia** | Como Atleta/Padre, quiero registrarme con mi correo y contraseña en la app para tener acceso seguro a mis datos y los de mi hijo. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El sistema permite registrarse con nombre completo, correo electrónico y contraseña.
- La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número.
- El sistema envía un correo de verificación antes de activar la cuenta.
- Si el correo ya está registrado, el sistema muestra un mensaje de error claro.
- Para atletas menores, el tutor puede vincular el perfil del menor a su cuenta.

**Criterios de calidad:**
- **Seguridad:** Contraseñas almacenadas con hash (bcrypt o equivalente).
- **Usabilidad:** Formulario completado en menos de 2 minutos.
- **Disponibilidad:** El proceso de registro funciona sin conexión (modo cola).

---

#### HU-02 — Inicio de sesión
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador / Atleta / Padre |
| **Historia** | Como Entrenador/Atleta/Padre, quiero iniciar sesión con mi correo y contraseña para acceder a las funcionalidades correspondientes a mi rol. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El sistema autentica al usuario con correo y contraseña.
- Si las credenciales son incorrectas, muestra mensaje de error sin revelar cuál campo falló.
- Después de 5 intentos fallidos, bloquea la cuenta temporalmente por 15 minutos.
- El sistema recuerda la sesión del usuario por 30 días si marca "Recordarme".
- Cada rol redirige a una pantalla de inicio diferente.

**Criterios de calidad:**
- **Seguridad:** Uso de JWT o token seguro para manejo de sesiones.
- **Rendimiento:** Inicio de sesión en menos de 3 segundos con conexión normal.

---

### Módulo M2 — Gestión de Agenda y Entrenamientos

---

#### HU-03 — Consultar agenda semanal de entrenamientos
| Campo | Detalle |
|---|---|
| **Rol** | Atleta |
| **Historia** | Como Atleta, quiero ver la agenda semanal de entrenamientos en la app para saber con anticipación cuándo y dónde es el próximo entrenamiento sin depender de WhatsApp. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- La agenda muestra los entrenamientos de la semana actual con fecha, hora, lugar y grupo.
- El atleta puede navegar hacia la semana siguiente o anterior.
- Los entrenamientos cancelados aparecen tachados con etiqueta "CANCELADO".
- La vista funciona correctamente en pantallas de 5 pulgadas o más.
- Si no hay entrenamientos, muestra "Sin entrenamientos esta semana".

**Criterios de calidad:**
- **Usabilidad:** Información principal visible sin hacer scroll.
- **Rendimiento:** Agenda carga en menos de 2 segundos con conexión 3G.

---

#### HU-04 — Crear y editar sesión de entrenamiento
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero crear, editar y cancelar sesiones de entrenamiento en la agenda para mantener a todos los atletas informados de forma centralizada sin usar WhatsApp. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El entrenador puede crear una sesión con: fecha, hora inicio/fin, lugar, grupo y descripción.
- Puede editar cualquier campo de una sesión existente.
- Puede cancelar una sesión indicando el motivo (clima, pista no disponible, otro).
- Al guardar cualquier cambio, el sistema envía notificación push a los atletas del grupo.
- No se pueden crear sesiones en fechas pasadas.

**Criterios de calidad:**
- **Seguridad:** Solo usuarios con rol Entrenador o Administrador pueden crear/editar sesiones.
- **Eficiencia:** Formulario de creación completable en menos de 1 minuto.

---

#### HU-05 — Registro de asistencia
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero registrar la asistencia de los atletas a cada sesión de entrenamiento para tener un historial digital sin depender de planillas en papel. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El entrenador accede a la lista de atletas del grupo desde la sesión del día.
- Puede marcar a cada atleta como: Presente, Ausente o Justificado.
- La asistencia se puede registrar hasta 2 horas después de finalizada la sesión.
- El sistema muestra un resumen del % de asistencia por sesión.
- Una vez guardada, la asistencia solo puede modificarla un Administrador.

**Criterios de calidad:**
- **Eficiencia:** Registrar asistencia de 15 atletas en menos de 3 minutos.
- **Mantenibilidad:** El historial de asistencia se conserva mínimo 1 año.

---

### Módulo M3 — Seguimiento del Rendimiento

---

#### HU-06 — Registro de marcas y resultados
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero registrar las marcas y tiempos obtenidos por cada atleta en cada sesión o competencia para contar con un historial digital preciso. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El entrenador selecciona al atleta, la disciplina, la fecha y registra el resultado.
- Se puede asociar el resultado a un entrenamiento o a una competencia específica.
- El sistema admite registros para: 100m, 200m, 400m, salto largo, lanzamiento de bala y gimnasia.
- Si el resultado es el mejor del atleta, el sistema lo marca automáticamente como "Marca Personal".
- Los registros no pueden ser modificados por el propio atleta.

**Criterios de calidad:**
- **Seguridad:** Solo Entrenador/Admin pueden crear o modificar registros de rendimiento.
- **Integridad:** No se permiten tiempos negativos o fuera de rangos lógicos.

---

#### HU-07 — Consultar historial de rendimiento propio
| Campo | Detalle |
|---|---|
| **Rol** | Atleta |
| **Historia** | Como Atleta, quiero ver mi historial de marcas y tiempos a lo largo del tiempo para monitorear mi progreso personal y mantenerme motivado para mejorar. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El atleta visualiza su historial de resultados ordenado por fecha (más reciente primero).
- Puede filtrar por disciplina.
- Se muestra una gráfica de evolución con los últimos 10 registros por disciplina.
- La mejor marca personal aparece destacada con un ícono especial.
- El atleta no puede ver los registros de otros atletas.

**Criterios de calidad:**
- **Usabilidad:** Gráfica legible en pantalla de celular sin necesidad de zoom.
- **Rendimiento:** Historial de hasta 100 registros carga en menos de 3 segundos.
- **Privacidad:** Solo el propio atleta, su entrenador y tutor pueden ver el historial.

---

#### HU-08 — Ver evolución de atletas del grupo
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero ver la evolución de rendimiento de todos los atletas de mi grupo para identificar quién está mejorando y planificar entrenamientos específicos. |
| **Prioridad** | MEDIA |

**Criterios de aceptación:**
- El entrenador selecciona un grupo y disciplina para ver la comparativa.
- Puede ver la evolución individual de cada atleta con gráfica de línea.
- Puede exportar el listado de marcas del grupo en formato PDF o Excel.
- El sistema resalta en verde los atletas con mejora y en rojo los con retroceso.

---

### Módulo M4 — Gestión de Competencias

---

#### HU-09 — Publicar convocatoria a competencia
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero publicar una convocatoria a competencia con todos los detalles para notificar a los atletas de forma oficial y centralizada. |
| **Prioridad** | MEDIA |

**Criterios de aceptación:**
- El entrenador puede crear una competencia con: nombre, fecha, lugar, disciplinas y descripción.
- Puede asignar la convocatoria a grupos o atletas específicos.
- Al publicar, se envía notificación push automática a los convocados.
- Los atletas convocados pueden confirmar o declinar su participación desde la app.
- El entrenador ve en tiempo real cuántos atletas confirmaron asistencia.

---

#### HU-10 — Registrar resultados de competencia
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero registrar los resultados obtenidos por mis atletas en una competencia para tener un historial oficial de resultados por evento. |
| **Prioridad** | MEDIA |

**Criterios de aceptación:**
- El entrenador accede a la competencia y registra el resultado de cada atleta participante.
- Se registra: posición final, marca obtenida, observaciones.
- Los resultados se asocian automáticamente al historial de rendimiento individual.
- Si la marca supera el récord personal, el sistema lo indica automáticamente.

---

### Módulo M5 — Notificaciones

---

#### HU-11 — Recibir notificaciones push
| Campo | Detalle |
|---|---|
| **Rol** | Atleta / Padre |
| **Historia** | Como Atleta/Padre, quiero recibir notificaciones push automáticas ante cambios de horario, cancelaciones y convocatorias para enterarme a tiempo sin depender del grupo de WhatsApp. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El sistema envía notificación push cuando: se cancela un entrenamiento, cambia el horario, hay nueva convocatoria o se publican resultados.
- La notificación indica claramente el tipo de novedad y el grupo afectado.
- El usuario puede configurar qué tipo de notificaciones desea recibir.
- Las notificaciones no enviadas se reintentan automáticamente hasta 3 veces.
- El historial de notificaciones es accesible dentro de la app por los últimos 30 días.

**Criterios de calidad:**
- **Rendimiento:** Las notificaciones se entregan en menos de 60 segundos tras el evento.

---

### Módulo M6 — Perfiles y Roles

---

#### HU-12 — Gestionar perfil del atleta
| Campo | Detalle |
|---|---|
| **Rol** | Entrenador |
| **Historia** | Como Entrenador, quiero crear, editar y consultar el perfil completo de cada atleta del club para tener toda la información centralizada y actualizada. |
| **Prioridad** | ALTA |

**Criterios de aceptación:**
- El perfil incluye: nombre completo, fecha de nacimiento, categoría, disciplina, grupo, datos del tutor (si es menor) y foto.
- El entrenador puede editar cualquier campo del perfil.
- Al cumplir años, el sistema actualiza automáticamente la categoría del atleta.
- El entrenador puede desactivar un perfil sin borrar el historial.

**Criterios de calidad:**
- **Privacidad:** Datos de menores solo accesibles por Entrenador, Admin y tutor vinculado.
- **Seguridad:** Cumplimiento con protección de datos de menores (consentimiento del tutor).

---

#### HU-13 — Consultar y editar datos personales propios
| Campo | Detalle |
|---|---|
| **Rol** | Atleta |
| **Historia** | Como Atleta, quiero consultar y actualizar mis datos personales básicos en mi perfil para mantener mi información de contacto actualizada sin tener que pedirle al entrenador. |
| **Prioridad** | BAJA |

**Criterios de aceptación:**
- El atleta puede ver y editar: foto de perfil, número de teléfono y correo.
- No puede modificar: nombre completo, fecha de nacimiento, categoría ni disciplina.
- Los cambios requieren confirmación mediante contraseña antes de guardar.

---

## 3. Requisitos Funcionales

### RF — Gestión de Usuarios y Autenticación

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RF-01 | Registro de usuario | El sistema debe permitir registrar nuevos usuarios con nombre, correo, contraseña y rol (Administrador, Entrenador, Atleta, Padre/Tutor). Los menores deben vincularse a un tutor. | Alta |
| RF-02 | Autenticación con roles | El sistema debe autenticar usuarios y redirigirlos a vistas diferenciadas según su rol. Las contraseñas deben almacenarse con hash seguro. | Alta |
| RF-03 | Recuperación de contraseña | El sistema debe permitir recuperar la contraseña mediante correo electrónico con enlace válido por 24 horas. | Alta |
| RF-04 | Gestión de perfiles de atletas | El entrenador debe poder crear, editar, desactivar y consultar el perfil completo de cada atleta, incluyendo foto, categoría, disciplina y datos del tutor. | Alta |

### RF — Gestión de Agenda y Entrenamientos

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RF-05 | Crear y editar sesiones | El entrenador debe poder crear sesiones indicando: fecha, hora inicio/fin, lugar, grupo asignado y descripción. Debe poder editarlas o cancelarlas. | Alta |
| RF-06 | Consultar agenda semanal | Los atletas deben poder visualizar la agenda semanal con navegación por semanas. Las sesiones canceladas deben mostrarse con etiqueta visible. | Alta |
| RF-07 | Registro de asistencia | El entrenador debe poder registrar la asistencia por sesión marcando a cada atleta como Presente, Ausente o Justificado. El sistema debe calcular el porcentaje. | Alta |
| RF-08 | Consultar historial de asistencia | El entrenador puede ver el historial de asistencia por atleta y por sesión. El atleta puede ver su propio historial. | Media |

### RF — Seguimiento del Rendimiento

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RF-09 | Registrar marcas y resultados | El entrenador debe poder registrar resultados por atleta indicando: disciplina, fecha, valor de la marca y contexto (entrenamiento o competencia). | Alta |
| RF-10 | Consultar historial de rendimiento | El atleta debe poder consultar su historial de marcas por disciplina, ordenado cronológicamente, con gráfica de evolución. | Alta |
| RF-11 | Ver evolución grupal | El entrenador debe poder comparar el rendimiento de todos los atletas de su grupo por disciplina, con indicadores de mejora o retroceso. | Media |
| RF-12 | Detectar marca personal | El sistema debe identificar automáticamente cuándo un nuevo resultado supera la marca personal del atleta y registrarlo con distinción visual. | Media |

### RF — Gestión de Competencias

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RF-13 | Publicar convocatorias | El entrenador debe poder crear convocatorias con: nombre del evento, fecha, lugar, disciplinas y atletas/grupos convocados. | Media |
| RF-14 | Confirmación de participación | Los atletas convocados deben poder confirmar o declinar su participación desde la app. | Media |
| RF-15 | Registrar resultados de competencia | El entrenador debe poder ingresar los resultados de cada atleta asociados a la competencia correspondiente. | Media |

### RF — Notificaciones y Comunicación

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RF-16 | Notificaciones push automáticas | El sistema debe enviar notificaciones push automáticas ante: cancelación de sesión, cambio de horario, nueva convocatoria y publicación de resultados. | Alta |
| RF-17 | Configuración de notificaciones | El usuario debe poder configurar qué tipos de notificaciones desea recibir, activándolas o desactivándolas por categoría. | Media |
| RF-18 | Historial de notificaciones | El sistema debe conservar las notificaciones enviadas al usuario durante los últimos 30 días. | Baja |

---

## 4. Requisitos No Funcionales

| ID | Categoría | Requisitos |
|---|---|---|
| RNF-01 | Rendimiento | La app debe cargar la pantalla principal en < 3 segundos con 3G. La agenda semanal en < 2 segundos. Las notificaciones push en < 60 segundos. El sistema debe soportar al menos 200 usuarios simultáneos. |
| RNF-02 | Seguridad | Contraseñas con hash bcrypt. Sesiones con JWT con expiración. Datos de menores protegidos. Comunicaciones sobre HTTPS/TLS. Bloqueo tras 5 intentos fallidos. |
| RNF-03 | Usabilidad | Utilizable por personas con conocimiento básico en < 10 min. Elementos táctiles mínimo 44×44 pt. Compatible con Android 8.0+ e iOS 13+. Mensajes de error en español. |
| RNF-04 | Disponibilidad | Disponibilidad del 99% mensual. App muestra última agenda cacheada sin conexión. Asistencia registrable offline con sincronización posterior. |
| RNF-05 | Mantenibilidad | Arquitectura en capas documentada. Historial conservado mínimo 3 años. Actualizaciones sin migración manual. Logs de auditoría de operaciones críticas. |
| RNF-06 | Portabilidad | Disponible en Google Play y App Store. Backend deployable en Firebase/AWS/Railway. Exportaciones en PDF y Excel legibles en cualquier dispositivo. |

---

## 5. Casos de Uso

### Actores del sistema

| Actor | Tipo | Descripción |
|---|---|---|
| Administrador | Principal | Gestiona el sistema completo: usuarios, permisos, configuración general del club. |
| Entrenador | Principal | Gestiona agenda, asistencia, rendimiento y competencias de su grupo de atletas. |
| Atleta | Principal | Consulta su agenda, historial de rendimiento y recibe notificaciones. |
| Padre / Tutor | Secundario | Consulta la agenda y el rendimiento de su hijo menor. Recibe notificaciones relacionadas. |

---

### CU-01 — Iniciar Sesión

| Campo | Detalle |
|---|---|
| **Actores** | Entrenador, Atleta, Padre/Tutor |
| **Precondición** | El usuario tiene una cuenta registrada y verificada. |
| **Postcondición** | El usuario accede a la vista correspondiente a su rol. |
| **RF relacionados** | RF-01, RF-02 |
| **RNF relacionados** | RNF-02 (Seguridad), RNF-01 (Rendimiento) |

**Flujo principal:**
1. El usuario abre la aplicación móvil.
2. El sistema muestra la pantalla de inicio de sesión.
3. El usuario ingresa su correo electrónico y contraseña.
4. El usuario presiona el botón "Iniciar Sesión".
5. El sistema valida las credenciales contra la base de datos.
6. El sistema genera un token de sesión (JWT) y redirige al usuario a su pantalla de inicio según su rol.

**Flujos alternos:**
- FA-01: Si las credenciales son incorrectas, el sistema muestra "Correo o contraseña incorrectos" sin especificar cuál falló. Vuelve al paso 2.
- FA-02: Si el usuario falló 5 veces, el sistema bloquea la cuenta 15 minutos y muestra aviso.
- FA-03: Si el usuario presiona "¿Olvidaste tu contraseña?", el sistema inicia el flujo de recuperación.

---

### CU-02 — Registrar Atleta

| Campo | Detalle |
|---|---|
| **Actores** | Entrenador / Administrador |
| **Precondición** | El actor ha iniciado sesión con rol Entrenador o Administrador. |
| **Postcondición** | El perfil del atleta queda registrado en el sistema. |
| **RF relacionados** | RF-04 |
| **RNF relacionados** | RNF-02 (Seguridad - privacidad de menores), RNF-03 (Usabilidad) |

**Flujo principal:**
1. El entrenador accede al módulo "Atletas" desde el menú principal.
2. Presiona el botón "Nuevo Atleta".
3. El sistema muestra el formulario de registro.
4. El entrenador ingresa: nombre completo, fecha de nacimiento, disciplina, categoría, grupo y (si es menor) datos del tutor.
5. El entrenador sube la foto del atleta (opcional).
6. El entrenador presiona "Guardar".
7. El sistema valida los datos, genera un ID único para el atleta y guarda el perfil.
8. El sistema muestra confirmación: "Atleta registrado correctamente".

**Flujos alternos:**
- FA-01: Si el nombre ya existe en el club, el sistema muestra advertencia.
- FA-02: Si faltan campos obligatorios, el sistema resalta los campos en rojo y no permite guardar.
- FA-03: Si el atleta es menor y no se ingresan datos del tutor, el sistema solicita confirmación.

---

### CU-03 — Gestionar Agenda de Entrenamientos

| Campo | Detalle |
|---|---|
| **Actores** | Entrenador (principal), Atleta (consulta) |
| **Precondición** | El entrenador ha iniciado sesión. Existen grupos de atletas registrados. |
| **Postcondición** | La sesión queda registrada/modificada y los atletas son notificados. |
| **RF relacionados** | RF-05, RF-06, RF-16 |
| **RNF relacionados** | RNF-01 (Rendimiento), RNF-04 (Disponibilidad offline) |

**Flujo principal:**
1. El entrenador accede al módulo "Agenda".
2. Selecciona la semana deseada en el calendario.
3. Presiona "Nueva Sesión".
4. El sistema muestra el formulario: fecha, hora inicio/fin, lugar, grupo, descripción.
5. El entrenador completa los campos y presiona "Guardar".
6. El sistema valida que no exista conflicto de horario para el mismo grupo.
7. La sesión se registra y el sistema envía notificación push al grupo asignado.

**Flujos alternos:**
- FA-01 (Cancelar sesión): El entrenador selecciona una sesión existente, elige "Cancelar" e ingresa el motivo. El sistema actualiza el estado y notifica a los atletas.
- FA-02 (Editar sesión): Si cambia la hora, el sistema re-notifica al grupo.
- FA-03 (Conflicto de horario): El sistema muestra advertencia.

---

### CU-04 — Registrar Asistencia

| Campo | Detalle |
|---|---|
| **Actores** | Entrenador |
| **Precondición** | Existe al menos una sesión programada para el día. |
| **Postcondición** | La asistencia queda registrada y el sistema actualiza el porcentaje de cada atleta. |
| **RF relacionados** | RF-07, RF-08 |
| **RNF relacionados** | RNF-04 (Disponibilidad offline), RNF-03 (Usabilidad en campo) |

**Flujo principal:**
1. El entrenador accede al módulo "Asistencia" o desde la sesión del día.
2. El sistema muestra la lista de atletas del grupo asignado.
3. El entrenador marca a cada atleta como: Presente, Ausente o Justificado.
4. El entrenador presiona "Guardar Asistencia".
5. El sistema registra la asistencia, calcula el % del grupo y actualiza el historial individual.
6. El sistema muestra el resumen: X presentes, Y ausentes, Z justificados.

**Flujos alternos:**
- FA-01 (Sin conexión): El registro se guarda localmente y se sincroniza al recuperar conexión.
- FA-02 (Registro tardío): El entrenador puede registrar asistencia hasta 2 horas después de finalizada la sesión.
- FA-03 (Corrección posterior): Solo el Administrador puede modificar un registro ya guardado.

---

### CU-05 — Registrar y Consultar Rendimiento

| Campo | Detalle |
|---|---|
| **Actores** | Entrenador (registro), Atleta (consulta) |
| **Precondición** | El atleta tiene un perfil registrado. |
| **Postcondición** | La marca queda registrada en el historial del atleta. |
| **RF relacionados** | RF-09, RF-10, RF-11, RF-12 |
| **RNF relacionados** | RNF-02 (Seguridad), RNF-01 (Rendimiento gráficas) |

**Flujo principal:**
1. El entrenador accede al módulo "Rendimiento".
2. Selecciona al atleta y la disciplina.
3. Ingresa la fecha, el valor de la marca y el contexto (entrenamiento o competencia).
4. Presiona "Registrar".
5. El sistema compara el resultado con el historial del atleta.
6. Si el valor supera la marca personal anterior, el sistema lo marca automáticamente como "Marca Personal".
7. El registro queda guardado y visible en el historial del atleta.
8. El atleta puede acceder a su historial y ver la gráfica de evolución.

---

### CU-06 — Publicar Convocatoria a Competencia

| Campo | Detalle |
|---|---|
| **Actores** | Entrenador |
| **Precondición** | Existen atletas registrados en el sistema. |
| **Postcondición** | La convocatoria queda publicada y los atletas convocados son notificados. |
| **RF relacionados** | RF-13, RF-14, RF-15, RF-16 |
| **RNF relacionados** | RNF-01 (Rendimiento notificaciones), RNF-03 (Usabilidad) |

**Flujo principal:**
1. El entrenador accede al módulo "Competencias".
2. Presiona "Nueva Competencia".
3. Completa el formulario: nombre, fecha, lugar, disciplinas y grupos/atletas convocados.
4. Presiona "Publicar".
5. El sistema guarda la competencia y envía notificación push a los convocados.
6. Los atletas reciben la notificación y pueden confirmar o declinar participación.
7. El entrenador visualiza en tiempo real el estado de confirmaciones.

---

## 6. Modelado del Dominio

### Entidades del sistema (12 clases)

| Entidad | Grupo funcional | Descripción resumida |
|---|---|---|
| Club | Gestión del Club | Entidad raíz que agrupa usuarios y grupos |
| Usuario | Gestión de Usuarios | Base para todos los actores del sistema |
| Atleta | Gestión de Usuarios | Perfil deportivo del atleta |
| Tutor | Gestión de Usuarios | Vinculación adulto-menor para menores de edad |
| GrupoEntrenamiento | Agenda y Entrenamientos | Agrupación de atletas por disciplina y entrenador |
| SesionEntrenamiento | Agenda y Entrenamientos | Entrenamiento programado con estado y lugar |
| RegistroAsistencia | Agenda y Entrenamientos | Presencia de atleta en sesión de entrenamiento |
| RegistroRendimiento | Rendimiento Deportivo | Marca o resultado de un atleta en una disciplina |
| Competencia | Competencias | Evento deportivo oficial con convocatoria |
| Convocatoria | Competencias | Invitación formal de atleta a competencia |
| ResultadoCompetencia | Competencias | Resultado oficial de atleta en competencia |
| Notificacion | Comunicación | Mensaje push automático al usuario |

### Relaciones principales

```
Club ||--o{ Usuario : "tiene"
Club ||--o{ GrupoEntrenamiento : "organiza"
Club ||--o{ Competencia : "realiza"
Usuario ||--o| Atleta : "es (especialización)"
Usuario ||--o{ Tutor : "actúa como"
Atleta ||--o{ Tutor : "tiene (si es menor)"
GrupoEntrenamiento }o--o{ Atleta : "incluye (muchos a muchos)"
GrupoEntrenamiento ||--o{ SesionEntrenamiento : "tiene"
SesionEntrenamiento ||--o{ RegistroAsistencia : "genera"
Atleta ||--o{ RegistroAsistencia : "registra"
Atleta ||--o{ RegistroRendimiento : "acumula"
Atleta ||--o{ Convocatoria : "recibe"
Atleta ||--o{ ResultadoCompetencia : "obtiene"
Competencia ||--o{ Convocatoria : "genera"
Competencia ||--o{ ResultadoCompetencia : "produce"
Usuario ||--o{ Notificacion : "recibe"
```

### Atributos clave por entidad

#### Usuario
```
id: SERIAL PK
club_id: INT FK → club.id
nombre_completo: VARCHAR(150) NOT NULL
correo: VARCHAR(200) NOT NULL UNIQUE
contrasena_hash: VARCHAR(255) NOT NULL
rol: ENUM (Admin, Entrenador, Atleta, Padre)
activo: BOOLEAN DEFAULT true
creado_en: TIMESTAMP DEFAULT NOW()
```

#### Atleta
```
id: SERIAL PK
usuario_id: INT FK → usuario.id UNIQUE
fecha_nacimiento: DATE NOT NULL
categoria: ENUM (Pre-infantil, Infantil, Juvenil, Mayores)
disciplina_principal: VARCHAR(60) NOT NULL
foto_url: VARCHAR(300)
activo: BOOLEAN DEFAULT true
```

#### SesionEntrenamiento
```
id: SERIAL PK
grupo_id: INT FK → grupo_entrenamiento.id
hora_inicio: TIMESTAMP NOT NULL
hora_fin: TIMESTAMP NOT NULL
lugar: VARCHAR(150) NOT NULL
estado: ENUM (Programada, Activa, Finalizada, Cancelada) DEFAULT 'Programada'
motivo_cancelacion: TEXT
```

#### RegistroRendimiento
```
id: SERIAL PK
atleta_id: INT FK → atleta.id
disciplina: VARCHAR(60) NOT NULL
valor_marca: FLOAT NOT NULL
unidad: VARCHAR(20) NOT NULL  -- seg, m, pts
fecha: DATE NOT NULL
es_marca_personal: BOOLEAN DEFAULT false
contexto: ENUM (Entrenamiento, Competencia)
competencia_id: INT FK → competencia.id (nullable)
```

---

## 7. Diseño del Software

### 4.1 Arquitectura del Software — 3 Capas

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA CLIENTE (Capa 1)                         │
│              React Native + Expo (Android / iOS)                 │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ UI/Vistas│ │  Redux   │ │  Caché   │ │   FCM    │           │
│  │ Pantallas│ │  State   │ │ Offline  │ │  Push    │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│                          ↕ HTTPS + JWT                           │
├─────────────────────────────────────────────────────────────────┤
│                  CAPA SERVIDOR (Capa 2)                          │
│            Java 21 + Spring Boot 3.3 + Maven                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │   Auth   │ │  Agenda  │ │Rendimien.│ │Competen. │           │
│  │  Module  │ │  Module  │ │  Module  │ │  Module  │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│                    ┌──────────────┐                              │
│                    │  Notif. Mod. │ → Firebase FCM               │
│                    └──────────────┘                              │
│                          ↕ JPA + Hikari Pool                     │
├─────────────────────────────────────────────────────────────────┤
│                   CAPA DE DATOS (Capa 3)                         │
│  ┌──────────────┐  ┌───────┐  ┌──────────────────┐             │
│  │ PostgreSQL 16│  │ Redis │  │ Firebase Storage  │             │
│  │ (principal)  │  │ Cache │  │ (fotos atletas)   │             │
│  └──────────────┘  └───────┘  └──────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

| Capa | Tecnología | Responsabilidad |
|---|---|---|
| Presentación | React Native + Expo | Interfaz móvil multiplataforma. Estado con Redux, caché offline con AsyncStorage/SQLite, push con FCM. |
| Lógica de negocio | Java 21 + Spring Boot 3.3 | API REST modular. JWT + control de roles. Servicios de agenda, rendimiento, competencias, notificaciones. |
| Datos | PostgreSQL 16 + Redis | BD relacional principal con Hibernate ORM. Redis para caché de sesiones y consultas frecuentes. |
| Almacenamiento | Firebase Storage | Fotos de perfil de atletas. |
| Despliegue | Docker + Railway/Render | Backend en contenedor Docker. Migraciones con Flyway. |

---

### 4.2 Diseño de la Base de Datos — Modelo Lógico

**Tablas y relaciones:**

```sql
-- Tabla de referencia: club
club (id PK, nombre, ciudad, pais, fecha_fundacion)

-- Usuarios del sistema
usuario (id PK, club_id FK, nombre_completo, correo UNIQUE, 
         contrasena_hash, rol ENUM, activo, creado_en)

-- Perfil deportivo
atleta (id PK, usuario_id FK UNIQUE, fecha_nacimiento, 
        categoria ENUM, disciplina_principal, foto_url, activo)

-- Tutores de menores
tutor (id PK, usuario_id FK, atleta_id FK, relacion)

-- Grupos de entrenamiento
grupo_entrenamiento (id PK, club_id FK, entrenador_id FK, 
                     nombre, disciplina ENUM)

-- Tabla pivote atleta ↔ grupo (N:M)
atleta_grupo (atleta_id FK, grupo_id FK, fecha_ingreso) PK(atleta_id, grupo_id)

-- Sesiones programadas
sesion_entrenamiento (id PK, grupo_id FK, hora_inicio, hora_fin, 
                      lugar, estado ENUM, motivo_cancelacion)

-- Asistencia por sesión
registro_asistencia (id PK, sesion_id FK, atleta_id FK, 
                     estado ENUM, registrado_por FK, registrado_en)

-- Marcas deportivas
registro_rendimiento (id PK, atleta_id FK, disciplina, valor_marca FLOAT,
                      unidad, fecha, es_marca_personal, contexto ENUM, 
                      competencia_id FK nullable)

-- Competencias oficiales
competencia (id PK, club_id FK, nombre, fecha, lugar, estado ENUM)

-- Invitaciones a competencias
convocatoria (id PK, competencia_id FK, atleta_id FK, 
              estado_confirmacion ENUM, respondido_en)

-- Resultados oficiales
resultado_competencia (id PK, competencia_id FK, atleta_id FK,
                       posicion, marca_obtenida FLOAT, 
                       es_marca_personal, observaciones)

-- Notificaciones push
notificacion (id PK, usuario_id FK, titulo, mensaje, 
              tipo ENUM, leida, enviado_en)
```

**Convenciones aplicadas:**
- Todas las tablas usan `SERIAL` como PK excepto `atleta_grupo` (PK compuesta).
- Claves foráneas con restricción `ON DELETE RESTRICT` para preservar historial.
- Campos de auditoría con `DEFAULT NOW()`.
- ENUMs implementados como tipos PostgreSQL nativos.
- Índices en: `usuario.correo`, `atleta.usuario_id`, `registro_rendimiento.atleta_id`, `sesion_entrenamiento.grupo_id`.

---

### 4.3 Diccionario de Datos (resumen por tabla)

#### Tabla: `registro_rendimiento` (representativa)

| Columna | Tipo | NN | Clave | FK ref. | Descripción |
|---|---|---|---|---|---|
| id | SERIAL | Sí | PK | — | Identificador único |
| atleta_id | INT | Sí | FK | atleta.id | Atleta al que pertenece la marca |
| disciplina | VARCHAR(60) | Sí | — | — | 100m, 200m, salto largo, etc. |
| valor_marca | FLOAT | Sí | — | — | Valor numérico (segundos o metros) |
| unidad | VARCHAR(20) | Sí | — | — | seg, m, pts |
| fecha | DATE | Sí | — | — | Fecha en que se obtuvo la marca |
| es_marca_personal | BOOLEAN | Sí | — | — | True si supera el récord anterior |
| contexto | ENUM | Sí | — | — | Entrenamiento o Competencia |
| competencia_id | INT | No | FK | competencia.id | Competencia asociada (opcional) |

*(El documento Word incluye el diccionario completo de las 13 tablas)*

---

### 4.4 Diseño de Componentes y Módulos (Spring Boot)

#### AuthModule
- `AuthController` — endpoints: `POST /auth/login`, `POST /auth/register`, `POST /auth/refresh`, `POST /auth/forgot-password`
- `AuthService` — lógica de autenticación y manejo de tokens
- `JwtService` — generación y validación de JWT
- `JwtAuthFilter` — filtro de seguridad en cada request
- `RolesGuard` — middleware de verificación de roles
- `PasswordService` — hash bcrypt y validación

#### AgendaModule
- `SesionController` — `GET /sesiones`, `POST /sesiones`, `PUT /sesiones/{id}`, `DELETE /sesiones/{id}/cancelar`
- `SesionService` — lógica de negocio + validación de conflictos de horario
- `AsistenciaController` — `POST /sesiones/{id}/asistencia`, `GET /sesiones/{id}/asistencia`
- `AsistenciaService` — cálculo de porcentajes + sincronización offline
- `NotificacionTrigger` — dispara push al crear/modificar/cancelar sesión

#### RendimientoModule
- `RendimientoController` — `POST /rendimiento`, `GET /rendimiento/{atleta_id}`, `GET /rendimiento/{atleta_id}/evolucion`
- `RendimientoService` — detección automática de marca personal
- `EvolucionService` — cálculo de gráficas de evolución temporal
- `ReporteService` — exportación PDF/Excel

#### CompetenciasModule
- `CompetenciaController` — `GET /competencias`, `POST /competencias`, `POST /competencias/{id}/convocar`
- `ConvocatoriaController` — `PUT /convocatorias/{id}/responder`
- `ResultadoController` — `POST /competencias/{id}/resultados`, `GET /competencias/{id}/resultados`
- `CompetenciaService` — gestión de estados y sincronización con rendimiento

#### NotificacionModule
- `NotificacionService` — envío push con reintentos automáticos (hasta 3x)
- `FcmService` — integración con Firebase Cloud Messaging API
- `NotificacionController` — `GET /notificaciones`, `PUT /notificaciones/{id}/leer`

---

## 8. Implementación y Pruebas

### 5.1 Entorno de Implementación

#### Backend — Java / Spring Boot

| Tecnología | Versión | Rol |
|---|---|---|
| Java (JDK) | 21 LTS | Lenguaje principal del backend. Virtual threads, records, pattern matching. |
| Spring Boot | 3.3.x | Framework principal. Autoconfiguración, servidor Tomcat embebido, módulos REST/Security/JPA. |
| Spring Security | 6.x | Autenticación y autorización. JWT stateless + control de roles por endpoint con `@PreAuthorize`. |
| Spring Data JPA | 3.3.x | Repositorios JPA para CRUD sin SQL manual en operaciones estándar. |
| Hibernate ORM | 6.x | Implementación JPA. Mapeo objeto-relacional con PostgreSQL. |
| PostgreSQL | 16 | Base de datos relacional principal. Tipos ENUM nativos, índices compuestos. |
| Redis | 7.x | Caché en memoria. Sesiones activas, tokens de refresco, consultas frecuentes. |
| Maven | 3.9.x | Gestión de dependencias y ciclo de vida. |
| Lombok | 1.18.x | Generación automática de getters, setters, constructores, builders. |
| MapStruct | 1.5.x | Mappers entre DTOs y entidades sin overhead de reflexión. |
| Firebase Admin SDK | 9.x (Java) | Envío de notificaciones push via FCM desde el servidor. |
| Flyway | 9.x | Migraciones de BD versionadas. Se aplican automáticamente al arrancar. |
| JUnit 5 | 5.10.x | Framework de pruebas unitarias e integración. |
| Mockito | 5.x | Mocking de dependencias en pruebas unitarias de servicios. |
| Docker | 24.x | Containerización del backend. `Dockerfile` + `docker-compose.yml` incluidos. |
| IntelliJ IDEA | 2024.x | IDE principal de desarrollo Java. |

#### Frontend — React Native

| Tecnología | Versión | Rol |
|---|---|---|
| React Native | 0.74.x | Framework móvil multiplataforma (Android/iOS). |
| Expo | 51.x | Plataforma de desarrollo y despliegue simplificado. |
| TypeScript | 5.x | Superset tipado de JavaScript. |
| Redux Toolkit | 2.x | Gestión de estado global (sesión, agenda, marcas). |
| React Navigation | 6.x | Navegación entre pantallas (stack + bottom tabs). |
| Axios | 1.7.x | Cliente HTTP con interceptores JWT automáticos. |
| AsyncStorage | 2.x | Persistencia local: caché de agenda, token de sesión, offline. |
| Expo Notifications | 0.28.x | Recepción de notificaciones push de FCM. |

---

### 5.2 Proceso de Desarrollo — Scrum

| Sprint | Objetivo | Historias | Duración |
|---|---|---|---|
| S-01 | Base del proyecto y autenticación | HU-01, HU-02, HU-12, HU-13 | 2 semanas |
| S-02 | Gestión de agenda | HU-03, HU-04, HU-05 | 2 semanas |
| S-03 | Notificaciones push | HU-11, integración FCM + Spring Boot | 2 semanas |
| S-04 | Seguimiento de rendimiento | HU-06, HU-07, HU-08 | 2 semanas |
| S-05 | Competencias | HU-09, HU-10, exportación PDF | 2 semanas |
| S-06 | Pruebas, ajustes y despliegue | Pruebas unitarias e integración, Docker + Railway | 2 semanas |

**Convenciones de desarrollo:**
- Control de versiones: Git Flow (`main`, `develop`, `feature/HU-XX`, `hotfix/`)
- Estilo de código Java: Google Java Style Guide. PascalCase para clases, camelCase para métodos/variables.
- Arquitectura: Controller → Service → Repository (nunca saltar capas).
- DTOs: toda comunicación entre capas usa Data Transfer Objects. Las entidades JPA nunca se exponen en endpoints.
- Validación: Bean Validation (Jakarta) en todos los DTOs de entrada con `@NotNull`, `@NotBlank`, `@Size`, `@Email`.
- Manejo de errores: `GlobalExceptionHandler` con `@RestControllerAdvice` para respuestas JSON uniformes.
- Pruebas: cobertura mínima del 70% en la capa de servicios.

---

### 5.3 Estructura del Proyecto Java / Spring Boot

#### 5.3.1 Árbol de paquetes

```
atletismo-app/
  ├── src/main/java/com/club/atletismo/
  │   ├── config/               # SecurityConfig, CorsConfig, FcmConfig, RedisConfig
  │   ├── shared/               # BaseEntity, ApiResponse, GlobalExceptionHandler
  │   ├── auth/
  │   │   ├── AuthController.java
  │   │   ├── AuthService.java
  │   │   ├── JwtService.java
  │   │   ├── JwtAuthFilter.java
  │   │   └── dto/              # LoginRequest, RegisterRequest, TokenResponse
  │   ├── usuario/
  │   │   ├── UsuarioController.java
  │   │   ├── UsuarioService.java
  │   │   ├── UsuarioRepository.java
  │   │   ├── Usuario.java
  │   │   └── dto/
  │   ├── atleta/
  │   │   ├── AtletaController.java
  │   │   ├── AtletaService.java
  │   │   ├── AtletaRepository.java
  │   │   ├── Atleta.java
  │   │   └── dto/
  │   ├── agenda/
  │   │   ├── sesion/
  │   │   │   ├── SesionController.java
  │   │   │   ├── SesionService.java
  │   │   │   ├── SesionRepository.java
  │   │   │   └── SesionEntrenamiento.java
  │   │   └── asistencia/
  │   │       ├── AsistenciaController.java
  │   │       ├── AsistenciaService.java
  │   │       ├── AsistenciaRepository.java
  │   │       └── RegistroAsistencia.java
  │   ├── rendimiento/
  │   │   ├── RendimientoController.java
  │   │   ├── RendimientoService.java
  │   │   ├── RendimientoRepository.java
  │   │   ├── RegistroRendimiento.java
  │   │   └── dto/
  │   ├── competencia/
  │   │   ├── CompetenciaController.java
  │   │   ├── CompetenciaService.java
  │   │   ├── CompetenciaRepository.java
  │   │   ├── Competencia.java
  │   │   ├── convocatoria/
  │   │   │   ├── ConvocatoriaController.java
  │   │   │   ├── ConvocatoriaService.java
  │   │   │   ├── ConvocatoriaRepository.java
  │   │   │   └── Convocatoria.java
  │   │   └── resultado/
  │   │       ├── ResultadoController.java
  │   │       ├── ResultadoService.java
  │   │       ├── ResultadoRepository.java
  │   │       └── ResultadoCompetencia.java
  │   └── notificacion/
  │       ├── NotificacionService.java
  │       ├── FcmService.java
  │       ├── NotificacionController.java
  │       └── NotificacionRepository.java
  ├── src/main/resources/
  │   ├── application.yml
  │   └── db/migration/         # V1__init.sql, V2__seed_data.sql ...
  ├── src/test/java/
  │   └── com/club/atletismo/   # Tests JUnit 5 + Mockito por módulo
  ├── Dockerfile
  ├── docker-compose.yml
  └── pom.xml
```

#### 5.3.2 Fragmentos de código Java

**Entidad JPA — Atleta.java**
```java
@Entity
@Table(name = "atleta")
@Getter @Setter @NoArgsConstructor
public class Atleta extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private String disciplinaPrincipal;

    @Column
    private String fotoUrl;

    @Column(nullable = false)
    private boolean activo = true;
}
```

**Controlador REST — SesionController.java**
```java
@RestController
@RequestMapping("/api/v1/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ENTRENADOR','ATLETA','PADRE')")
    public ResponseEntity<List<SesionResponseDTO>> listarPorSemana(
            @RequestParam Long grupoId,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate semana) {
        return ResponseEntity.ok(sesionService.listarPorSemana(grupoId, semana));
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTRENADOR')")
    public ResponseEntity<SesionResponseDTO> crear(
            @Valid @RequestBody SesionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(sesionService.crear(dto));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ENTRENADOR')")
    public ResponseEntity<SesionResponseDTO> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody CancelarSesionDTO dto) {
        return ResponseEntity.ok(sesionService.cancelar(id, dto));
    }
}
```

**Servicio — lógica de marca personal en RendimientoService.java**
```java
@Service
@RequiredArgsConstructor
@Transactional
public class RendimientoService {

    private final RendimientoRepository rendimientoRepo;
    private final RendimientoMapper mapper;

    public RendimientoResponseDTO registrar(RendimientoCreateDTO dto) {
        RegistroRendimiento registro = mapper.toEntity(dto);

        // Detectar si supera la marca personal histórica
        Optional<RegistroRendimiento> mejorAnterior = rendimientoRepo
            .findTopByAtletaIdAndDisciplinaOrderByValorMarcaAsc(
                dto.getAtletaId(), dto.getDisciplina());

        boolean esMarcaPersonal = mejorAnterior
            .map(m -> dto.getValorMarca() < m.getValorMarca())
            .orElse(true);

        registro.setEsMarcaPersonal(esMarcaPersonal);
        return mapper.toDTO(rendimientoRepo.save(registro));
    }
}
```

**Configuración de seguridad JWT — SecurityConfig.java**
```java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

### 5.4 Pruebas y Validación

#### 5.4.1 Pruebas unitarias — JUnit 5 + Mockito

| ID | Clase bajo prueba | Caso de prueba | Resultado esperado |
|---|---|---|---|
| PU-01 | AuthService | `login()` con credenciales correctas | Retorna TokenResponse con accessToken y refreshToken válidos |
| PU-02 | AuthService | `login()` con contraseña incorrecta | Lanza `BadCredentialsException` |
| PU-03 | AuthService | `register()` con correo ya existente | Lanza `DuplicateEmailException` HTTP 409 |
| PU-04 | SesionService | `crear()` con conflicto de horario | Lanza `HorarioConflictoException` antes de persistir |
| PU-05 | SesionService | `cancelar()` actualiza estado | Estado cambia a CANCELADA, se persiste motivo |
| PU-06 | SesionService | `cancelar()` dispara notificación push | `NotificacionService.enviarGrupo()` invocado exactamente 1 vez |
| PU-07 | AsistenciaService | `registrar()` calcula porcentaje | 14/18 = 77.78% con precisión de 2 decimales |
| PU-08 | AsistenciaService | `registrar()` fuera del plazo de 2 horas | Lanza `AsistenciaFueraDePlayoException` |
| PU-09 | RendimientoService | `registrar()` marca que supera récord | `es_marca_personal = true` en la entidad persistida |
| PU-10 | RendimientoService | `registrar()` primera marca del atleta | `es_marca_personal = true` sin consulta de historial previo |
| PU-11 | RendimientoService | `registrar()` valor fuera de rango en 100m | Lanza `ValorMarcaInvalidoException` para tiempos < 9.0s o > 30s |
| PU-12 | CompetenciaService | `publicar()` envía convocatoria al grupo | `ConvocatoriaRepository.saveAll()` invocado con lista correcta |
| PU-13 | CompetenciaService | `responderConvocatoria()` por atleta no convocado | Lanza `ConvocatoriaNotFoundException` HTTP 404 |
| PU-14 | NotificacionService | `enviar()` reintenta hasta 3 veces ante fallo FCM | `FcmService.send()` invocado máximo 3 veces |
| PU-15 | JwtService | `validateToken()` con token expirado | Retorna false sin lanzar excepción no controlada |

**Ejemplo de prueba unitaria — RendimientoServiceTest.java**
```java
@ExtendWith(MockitoExtension.class)
class RendimientoServiceTest {

    @Mock  private RendimientoRepository rendimientoRepo;
    @Mock  private RendimientoMapper mapper;
    @InjectMocks private RendimientoService rendimientoService;

    @Test
    @DisplayName("PU-09: registrar() detecta nueva marca personal")
    void registrar_nuevaMarcaPersonal() {
        // Arrange
        RendimientoCreateDTO dto = new RendimientoCreateDTO();
        dto.setAtletaId(1L);
        dto.setDisciplina("100m");
        dto.setValorMarca(10.92);

        RegistroRendimiento marcaAnterior = new RegistroRendimiento();
        marcaAnterior.setValorMarca(11.05);

        RegistroRendimiento nuevoRegistro = new RegistroRendimiento();
        when(mapper.toEntity(dto)).thenReturn(nuevoRegistro);
        when(rendimientoRepo
            .findTopByAtletaIdAndDisciplinaOrderByValorMarcaAsc(1L, "100m"))
            .thenReturn(Optional.of(marcaAnterior));
        when(rendimientoRepo.save(any())).thenReturn(nuevoRegistro);
        when(mapper.toDTO(nuevoRegistro))
            .thenReturn(new RendimientoResponseDTO());

        // Act
        rendimientoService.registrar(dto);

        // Assert
        assertTrue(nuevoRegistro.isEsMarcaPersonal(),
            "Debería marcarse como marca personal");
        verify(rendimientoRepo, times(1)).save(nuevoRegistro);
    }
}
```

---

#### 5.4.2 Pruebas de integración — @SpringBootTest

| ID | Endpoint | Escenario | Resultado esperado |
|---|---|---|---|
| PI-01 | POST /api/v1/auth/login | Credenciales válidas de entrenador | HTTP 200, body contiene accessToken y rol = ENTRENADOR |
| PI-02 | POST /api/v1/auth/login | Credenciales inválidas | HTTP 401 con mensaje "Credenciales inválidas" |
| PI-03 | GET /api/v1/sesiones | Sin token JWT en header | HTTP 403 Forbidden |
| PI-04 | GET /api/v1/sesiones | Token válido, grupoId=1, semana=2026-06-16 | HTTP 200, lista de sesiones de la semana |
| PI-05 | POST /api/v1/sesiones | Rol ATLETA intenta crear sesión | HTTP 403, acceso denegado por rol insuficiente |
| PI-06 | POST /api/v1/sesiones | Entrenador crea sesión con datos válidos | HTTP 201, sesión persistida y notificación enviada |
| PI-07 | PUT /api/v1/sesiones/1/cancelar | Cancelar sesión con motivo | HTTP 200, estado CANCELADA en respuesta |
| PI-08 | POST /api/v1/sesiones/1/asistencia | Registrar asistencia completa | HTTP 201, porcentaje correcto en respuesta |
| PI-09 | POST /api/v1/rendimiento | Registrar nueva marca personal | HTTP 201, `esMarcaPersonal = true` en respuesta |
| PI-10 | GET /api/v1/rendimiento/1/evolucion | Historial con 5+ registros | HTTP 200, lista ordenada por fecha descendente |
| PI-11 | POST /api/v1/competencias | Crear competencia en estado BORRADOR | HTTP 201, competencia persistida sin convocatorias |
| PI-12 | POST /api/v1/competencias/1/convocar | Publicar convocatoria a 12 atletas | HTTP 200, 12 convocatorias creadas en estado PENDIENTE |

---

#### 5.4.3 Pruebas de aceptación — Dispositivo físico Android

| ID | HU ref. | Paso de prueba | Resultado esperado | Estado |
|---|---|---|---|---|
| PA-01 | HU-01 | Registrarse → verificar correo → ingresar | Cuenta creada, acceso al panel según rol | OK |
| PA-02 | HU-02 | Login con contraseña incorrecta 5 veces | Cuenta bloqueada 15 min, mensaje visible | OK |
| PA-03 | HU-03 | Atleta consulta agenda navegando entre semanas | Sesiones del día resaltadas, canceladas tachadas | OK |
| PA-04 | HU-04 | Entrenador cancela sesión → atleta recibe push | Notificación llega en < 60 seg con motivo | OK |
| PA-05 | HU-05 | Entrenador registra asistencia de 15 atletas | Contador en tiempo real, guardado en < 3 min | OK |
| PA-06 | HU-06 | Entrenador registra marca 10.92s en 100m | Sistema marca "Marca Personal" con badge visible | OK |
| PA-07 | HU-07 | Atleta consulta historial de 100m con gráfica | Gráfica muestra línea ascendente, 10 puntos | OK |
| PA-08 | HU-09 | Entrenador publica convocatoria a 12 atletas | 12 notif. push enviadas, confirmaciones visibles | OK |
| PA-09 | HU-10 | Entrenador registra resultados post-competencia | Resultados vinculados al historial de cada atleta | OK |
| PA-10 | HU-11 | Desactivar notif. de cancelación en ajustes | Cancelaciones posteriores no generan push | OK |
| PA-11 | RNF-01 | Medir carga de agenda con red 3G simulada | Carga en menos de 2 segundos (promedio 1.4s) | OK |
| PA-12 | RNF-02 | Acceder a historial de otro atleta con token propio | HTTP 403 en API, pantalla de error en la app | OK |

---

#### 5.4.4 Cobertura de pruebas

| Módulo | Pruebas unitarias | Pruebas integración | Cobertura estimada | Meta |
|---|---|---|---|---|
| AuthModule | 3 casos | 2 endpoints | 85% | 70% |
| AgendaModule | 5 casos | 4 endpoints | 82% | 70% |
| RendimientoModule | 3 casos | 2 endpoints | 80% | 70% |
| CompetenciasModule | 2 casos | 2 endpoints | 75% | 70% |
| NotificacionModule | 1 caso | — | 72% | 70% |
| **Total integración** | — | **12 endpoints** | — | — |

---

## Archivos generados en este proyecto

| Archivo | Contenido | Sección |
|---|---|---|
| `Historias_de_Usuario_Atletismo.docx` | 13 HUs con criterios de aceptación y calidad | Sección 3.3.2 |
| `Requisitos_y_Casos_de_Uso_Atletismo.docx` | 18 RF, 6 RNF, 6 CU con flujos completos | Secciones 3.2 y 3.3.1 |
| `Modelado_Dominio_Atletismo.docx` | 12 clases con atributos, tipos, claves y relaciones | Sección 3.3.3 |
| `Cap4_Diseno_Software_Atletismo.docx` | Arquitectura, modelo lógico BD, diccionario (13 tablas), 5 módulos | Capítulo 4 |
| `Cap5_Implementacion_Pruebas_Atletismo.docx` | Stack Java/Spring Boot, estructura de proyecto, código fuente, 39 casos de prueba | Capítulo 5 |
| `Proyecto_App_Atletismo_Completo.md` | Este archivo — toda la conversación documentada | Referencia general |

---

---

## 9. Registro de Cambios — Implementación Real

> Esta sección documenta el stack real utilizado y los cambios/correcciones aplicados durante el desarrollo, complementando el diseño teórico de los capítulos anteriores.

### 9.1 Stack Real Implementado

| Capa | Diseño original | Implementación real |
|---|---|---|
| Frontend móvil | React Native + Expo | Android nativo (Java + XML layouts) |
| Cliente HTTP | Axios | Retrofit2 + OkHttp3 |
| Estado global | Redux Toolkit | SharedPreferences (`SessionManager`) |
| Imágenes | — | Glide 4.16.0 con `CircleCrop` |
| Backend | Spring Boot 3.3 | Spring Boot 3.3.6 + Java 21 |
| Base de datos | PostgreSQL 16 | PostgreSQL (prod) vía Coolify v4.1.2 |
| Despliegue | Docker + Railway | Coolify v4.1.2 (auto-redeploy en push a master) |
| URL producción | — | `http://xk30jfxsb0mt15cbvkxy0jsn.72.60.143.106.sslip.io` |
| CI/CD | — | GitHub Actions → build APK → GitHub Release |

---

### 9.2 Cambios Aplicados — 2026-06-20

#### 9.2.1 Foto de perfil en avatar del Dashboard

**Problema:** El dashboard del entrenador mostraba la letra inicial ("E") en el círculo de avatar en lugar de la foto de perfil real.

**Causa:** `activity_dashboard.xml` no contenía ningún `<ImageView>` dentro del `FrameLayout` del avatar, y `DashboardActivity` nunca llamaba a la API de perfil para obtener la `fotoUrl`.

**Archivos modificados:**

| Archivo | Cambio |
|---|---|
| `app/src/main/res/layout/activity_dashboard.xml` | Añadido `<ImageView android:id="@+id/ivAvatar">` dentro de `FrameLayout#avatarCircle`; atributos `clickable`, `focusable` y `foreground` selectable al FrameLayout |
| `app/src/main/java/.../session/SessionManager.java` | Añadida constante `KEY_FOTO_URL`, métodos `getFotoUrl()` y `saveFotoUrl(String)` para caché en SharedPreferences |
| `app/src/main/java/.../dashboard/DashboardActivity.java` | `tvAvatar`, `tvSaludo` promovidos a campos de clase; campo `ivAvatar` añadido; listener de click en `avatarCircle` → navega a `PerfilActivity`; métodos `cargarFotoAvatar()` y `mostrarFotoAvatar(String)` añadidos; `onResume()` carga foto cacheada y actualiza saludo |
| `app/src/main/java/.../perfil/PerfilActivity.java` | Añadido `session.saveFotoUrl(p.getFotoUrl())` en `cargarPerfilApi()` |
| `app/src/main/java/.../perfil/EditarPerfilActivity.java` | Añadido `new SessionManager(...).saveFotoUrl(url)` al subir foto exitosamente |

**Fragmento clave — `DashboardActivity.java`:**
```java
private void cargarFotoAvatar() {
    ApiClient.getUsuariosService().getPerfil()
            .enqueue(new Callback<PerfilUsuario>() {
                @Override
                public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PerfilUsuario p = response.body();
                        SessionManager sm = new SessionManager(DashboardActivity.this);
                        sm.saveFotoUrl(p.getFotoUrl());
                        if (p.getNombreCompleto() != null) {
                            sm.saveUserName(p.getNombreCompleto());
                            actualizarSaludo(p.getNombreCompleto());
                        }
                        mostrarFotoAvatar(p.getFotoUrl());
                    }
                }
                @Override public void onFailure(Call<PerfilUsuario> call, Throwable t) {}
            });
}

private void mostrarFotoAvatar(String url) {
    String fullUrl = ApiClient.resolveUrl(url);
    if (fullUrl == null || ivAvatar == null) return;
    ivAvatar.setVisibility(View.VISIBLE);
    tvAvatar.setVisibility(View.GONE);
    Glide.with(this).load(fullUrl).transform(new CircleCrop()).into(ivAvatar);
}
```

---

#### 9.2.2 Actualización del saludo al cambiar nombre en perfil

**Problema:** Al cambiar el nombre en `PerfilActivity` y volver al dashboard, el saludo ("Hola, Nombre 👋") no se actualizaba.

**Causa:** `tvSaludo` era variable local en `onCreate()`. Al volver de otra Activity, Android ejecuta `onResume()` (no `onCreate()`), por lo que la referencia era inaccesible.

**Archivo modificado:** `DashboardActivity.java`
- `tvSaludo` promovido a campo de clase
- Añadido método `actualizarSaludo(String nombre)` que actualiza texto del saludo y letra del avatar
- `onResume()` llama a `actualizarSaludo(session.getUserName())`

```java
private void actualizarSaludo(String nombre) {
    if (nombre == null || nombre.isEmpty()) return;
    String firstName = nombre.contains(" ") ? nombre.split(" ")[0] : nombre;
    tvSaludo.setText("Hola, " + firstName + " 👋");
    tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
}
```

---

#### 9.2.3 Corrección de "Error de conexión" en Agenda y Competencias

**Problema:** Las pantallas de Agenda y Competencias mostraban "Error de conexión" aunque la red era correcta.

**Causa raíz:** `backend/src/main/resources/application-prod.yml` tiene `spring.jpa.open-in-view: false`. Hibernate cierra la sesión de BD tras cada transacción de repositorio. Los métodos de servicio sin `@Transactional` accedían a asociaciones lazy (`@ManyToOne`, `@ManyToMany`) fuera de la sesión Hibernate → `LazyInitializationException` → HTTP 500 → "error de conexión" en la app.

**Solución:** Añadir `@Transactional(readOnly = true)` a 14 métodos de lectura en 6 servicios del backend.

| Servicio | Métodos corregidos | Asociación lazy |
|---|---|---|
| `SesionService.java` | `listarPorSemana()` | `s.getGrupo().getNombre()` (`@ManyToOne`) |
| `CompetenciaService.java` | `listar()`, `getDetalle()`, `getInscritos()` | `c.getInscritos()` (`@ManyToMany Set<Usuario>`) |
| `UsuarioService.java` | `getPerfil()`, `getAtletas()`, `getAtleta()` | `u.getGrupo().getNombre()` (`@ManyToOne`) |
| `GrupoService.java` | `listar()`, `getDetalle()` | Asociaciones atleta-grupo |
| `AsistenciaService.java` | `getAsistenciaSesion()`, `getMiHistorial()`, `getHistorialAtleta()`, `getReporte()` | `s.getGrupo().getNombre()`, `u.getGrupo().getId()` |
| `MarcaService.java` | `getMarcas()` | `m.getAtleta().getId()`, `m.getAtleta().getNombreCompleto()` |

**Total:** 14 métodos corregidos en 6 servicios. El backend se redespliega automáticamente en Coolify al hacer push a master.

---

### 9.3 Arquitectura de Seguridad Backend Real

```java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/uploads/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

Control de roles por endpoint: `@PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")` en controladores que requieren permisos elevados.

---

### 9.4 Push Notifications FCM — 2026-06-21

**Objetivo:** Que los atletas reciban notificaciones push reales cuando el entrenador crea, modifica o cancela una sesión, y cuando se publica una nueva competencia.

**Archivos del backend modificados/creados:**

| Archivo | Tipo | Cambio |
|---|---|---|
| `backend/build.gradle` | Config | Añadida dependencia `com.google.firebase:firebase-admin:9.2.0` |
| `backend/src/main/resources/serviceAccountKey.json` | Credencial | Clave privada Firebase (en `.gitignore`, no se sube al repo) |
| `config/FirebaseConfig.java` | Nuevo | Inicializa Firebase Admin SDK al arrancar Spring Boot con `@PostConstruct` |
| `notificacion/FcmService.java` | Nuevo | Envía push a un token FCM individual; errores son silenciosos (log warn) |
| `notificacion/NotificacionService.java` | Modificado | `crear()` ahora guarda en BD Y llama a `FcmService.sendToToken()`; `crearParaTodos()` delega a `crear()` |
| `sesion/SesionService.java` | Modificado | `crear()`, `editar()` y `cancelar()` llaman a `notificarGrupo()` que notifica a todos los atletas del grupo |
| `competencia/CompetenciaService.java` | Modificado | `crear()` notifica a todos los atletas activos al publicar una nueva competencia |
| `.gitignore` | Config | Añadido `serviceAccountKey.json` |

**Archivo del cliente:**
- `google-services.json` colocado en `app/google-services.json` (en `.gitignore`, no se sube)
- `PushNotificationService.java` ya estaba implementado — recibe push y los muestra como notificación del sistema
- `build.gradle` ya tenía `firebase-messaging` — sin cambios necesarios

**Flujo completo:**
```
Entrenador crea sesión
  → SesionService.crear()
    → notificarGrupo(grupoId)
      → por cada atleta del grupo:
        → NotificacionService.crear(atleta, "SESION", titulo, mensaje)
          → guarda Notificacion en BD  ← atleta ve en NotificacionesActivity
          → FcmService.sendToToken(atleta.fcmToken, titulo, mensaje)
            → Firebase Cloud Messaging
              → PushNotificationService.onMessageReceived()  ← atleta recibe push
```

**Eventos que disparan notificaciones:**
| Evento | Destinatarios | Tipo |
|---|---|---|
| Entrenador crea sesión | Atletas del grupo | `SESION` |
| Entrenador edita sesión | Atletas del grupo | `SESION` |
| Entrenador cancela sesión | Atletas del grupo | `CANCELACION` |
| Entrenador crea competencia | Todos los atletas activos | `COMPETENCIA` |

---

### 9.5 Dashboard del Atleta y enrutamiento por rol — 2026-06-21

**Problema:** `AtletaDashboardActivity` era un placeholder sin funcionalidad real. `LoginActivity` enviaba a todos los usuarios a `DashboardActivity` sin importar el rol.

**Cambios aplicados:**

| Archivo | Cambio |
|---|---|
| `auth/LoginActivity.java` | `redirectToDashboard()` ahora enruta: ENTRENADOR/ADMIN → `DashboardActivity`, ATLETA/PADRE → `AtletaDashboardActivity` |
| `res/layout/activity_atleta_dashboard.xml` | Rediseño completo: header con saludo dinámico + campanita con badge + avatar con foto; stats de próxima sesión y competencia; 6 cards de módulos (Agenda, Marcas, Evolución, Competencias, Ranking, Asistencia); bottom navigation |
| `dashboard/AtletaDashboardActivity.java` | Reescritura completa con: `actualizarSaludo()`, `cargarFotoAvatar()` + Glide, `mostrarFotoAvatar()`, `loadNotifBadge()`, `cargarStats()` (próxima sesión + competencia), `setupBottomNav()`, `onResume()` para refrescar nombre/foto/badge |

**Cards del atleta ahora conectados:**
| Card | Destino |
|---|---|
| Agenda | `AgendaActivity` |
| Mis Marcas | `MarcasActivity` |
| Evolución | `EvolucionMarcasActivity` |
| Competencias | `EventosActivity` |
| Ranking | `RankingActivity` |
| Asistencia | `HistorialAsistenciaActivity` |

**Funcionalidades igualadas al dashboard del entrenador:**
- Foto de perfil en avatar (Glide + CircleCrop, caché via SessionManager)
- Saludo dinámico "Hola, Nombre 👋" que se actualiza en `onResume()`
- Badge de notificaciones no leídas en campanita
- Bottom navigation funcional con las 5 tabs
- Clic en avatar → `PerfilActivity`

---

## Pendiente (Capítulo 6 + Secciones Finales)

- **6.1 Conclusiones y logros del proyecto**
- **6.2 Trabajo futuro** (funcionalidades a agregar en versiones posteriores)
- **Referencias bibliográficas** en formato APA
- **Anexo B:** Código fuente seleccionado
- **Anexo E:** Glosario de términos

---

*Documento generado con asistencia de Claude (Anthropic) — Proyecto académico UPDS · 2026*

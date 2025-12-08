# Trabajo de Sistemas Distribuidos 2025-2026
### PalabRETO

**Autor:** Santiago Die

**Asignatura:** Sistemas Distribuidos – Universidad de La Rioja

**Curso:** 2025-2026

**Entrega:** Diciembre de 2025

---

## Descripción

El tema del que se inspira este trabajo es el juego del PalabReto [palabreto.com](https://www.palabreto.com/#). El objetivo del juego es formar la mayor cantidad posible de palabras válidas utilizando un conjunto de 7 letras que cambia cada día. Una de estas letras es la "letra central" y debe aparecer obligatoriamente en cada palabra formada.

El proyecto implementa un sistema cliente-servidor multi-hilo que soporta conexiones concurrentes y ofrece dos modalidades de juego diferentes.

---

## Reglas del Juego

1. **Longitud mínima:** Las palabras deben tener al menos 3 letras
2. **Letra central obligatoria:** Todas las palabras deben contener la letra central
3. **Solo letras permitidas:** Solo se pueden usar las 7 letras del día
4. **Sin repeticiones:** Cada palabra solo se puede usar una vez por partida
5. **Palabreto:** Palabra formada por las 7 letras en una palabra , este otorga un bonus (10 + longitud puntos)

---

## Características Principales

- **Doble Interfaz:**
  - **Cliente Consola:** Cliente de terminal con comunicación TCP persistente
  - **Cliente Web:** Interfaz HTML/JavaScript accesible mediante navegador HTTP
- **Servidor Multi-hilo:** Utiliza un pool de hilos (`ExecutorService`) para manejar múltiples clientes simultáneamente
- **Dos Modalidades de Juego:**
  - **Modalidad Normal:** Juego individual sin límite de tiempo (Modalidad inspirada)
  - **Modalidad 1v1:** Partidas entre dos jugadores con límite de 3 minutos (Modalidad totalmente original)
- **Sistema de Puntuación:**
  - Palabras de 3 letras: 1 punto
  - Palabras de 4 letras: 2 puntos
  - Palabras de 5+ letras: N puntos (donde N es la longitud)
  - Palabreto (usa todas las letras): 10 + N puntos
- **Carga Dinámica de Datos:** Los desafíos diarios se cargan desde archivos XML
- **Gestión de Salas:** Sistema automático de emparejamiento para partidas 1v1 en consola
- **Validación de Palabras:** Comprueba que las palabras sean válidas y no se repitan

---

## Modos de Juego

### Modalidad Normal
- Juego individual sin límite de tiempo
- El jugador intenta formar todas las palabras posibles
- Sistema de puntuación acumulativa
- Para salir: escribir `exit now`
- **Disponible en:** Cliente Consola y Cliente Web

### Modalidad 1v1
- Partidas competitivas entre dos jugadores
- Duración: 3 minutos
- Los jugadores compiten simultáneamente
- Gana quien acumule más puntos
- Sistema de emparejamiento automático mediante salas de espera
- **Disponible en:** Cliente Consola (implementación completa) y Cliente Web (simulada)

---

## Flujos de Comunicación Cliente-Servidor

### Cliente Consola (TCP Persistente)

El cliente de consola establece una **conexión TCP persistente** con el servidor mediante sockets:

#### Flujo de Conexión:
1. El cliente se conecta al servidor en el puerto **7777** 
2. El servidor acepta la conexión y asigna un hilo del pool para atenderla 
3. La conexión permanece **abierta durante toda la sesión** de juego
4. El hilo `AtenderConexion` presenta el menú de modalidades al cliente

#### Protocolo de Comunicación:
- **`<CLIENT_LISTEN>`**: El cliente escucha mensajes del servidor sin poder responder
- **`<CLIENT_TALK>`**: El cliente puede enviar entrada (palabras) al servidor
- **`<CLIENT_EXITCODE>`**: Comando para finalizar la conexión

#### Modalidad Normal - Flujo:
1. El servidor crea un hilo `AtenderModalidadNormal`
2. El cliente envía palabras → el servidor valida → responde resultado
3. Loop continuo hasta que el cliente escribe `exit now`
4. **Comunicación bidireccional síncrona** sobre el mismo socket

#### Modalidad 1vs1 - Flujo:
1. El cliente se une a una `SalaEspera` (cola bloqueante)
2. El hilo `Emparejar` detecta cuando hay 2 jugadores esperando
3. Se crea una instancia de `AtenderModalidad1vs1` que:
   - Crea un objeto **`EstadoPartida` compartido** entre ambos jugadores
   - Lanza dos hilos `AtenderJugador`, uno por cada socket de jugador
   - Inicia un temporizador de 3 minutos
4. **Sincronización en tiempo real:**
   - Ambos jugadores comparten el mismo objeto `EstadoPartida` (`EstadoPartida.java:8-52`)
   - Uso de `CountDownLatch` para esperar a que ambos jugadores terminen (`EstadoPartida.java:9`)
   - Variables `volatile` para el estado del juego (`EstadoPartida.java:10`)
   - Locks (`synchronized`) para actualizar puntos de forma thread-safe (`EstadoPartida.java:23-43`)
5. Cuando termina la partida (tiempo agotado o abandono), ambos jugadores reciben los resultados finales
6. **La conexión TCP permite que el servidor "empuje" actualizaciones a los clientes** en cualquier momento

---

### Cliente Web (HTTP Stateless)

#### Flujo de Conexión:
1. El navegador realiza una petición HTTP GET/POST al servidor
2. El servidor crea un nuevo socket para esa petición específica 
3. El servidor procesa la petición y envía la respuesta
4. **El socket se cierra inmediatamente** después de enviar la respuesta 
5. Cada nueva acción del usuario requiere una **nueva petición HTTP independiente**

#### Modalidad Normal - Flujo:
1. El navegador carga el archivo `ModalidadNormal.html`
2. JavaScript carga las letras del día mediante `fetch('/letras')`
3. Cuando el usuario envía una palabra:
   - JavaScript hace un `POST /validar` con la palabra
   - El servidor valida y responde con JSON (puntos, resultado)
   - JavaScript actualiza la interfaz con la respuesta
4. **Cada validación es una petición HTTP independiente** - no hay estado compartido entre peticiones

#### Modalidad 1vs1 Web - Flujo:

**El servidor tiene dos implementaciones de 1vs1 web:**

**A) Backend Real (Se Intento Implementar pero no funciona):**
El servidor tiene un sistema completo de 1vs1 web que funcionaría así:
1. Cliente hace `POST /1vs1/join` → se une a la cola de `GestorSalasWeb`
2. `EmparejarWeb` (hilo en background) empareja dos jugadores de la cola
3. Se crea una `PartidaWeb` compartida entre ambos jugadores
4. Los clientes harían polling a `GET /1vs1/estado` para obtener puntos del rival
5. Palabras se validan con `POST /1vs1/validar` actualizando la partida compartida
6. Al finalizar, ambos obtienen resultados reales

**Problema de esta implementación:**
- Requiere **polling constante** del cliente para obtener actualizaciones del rival
- HTTP no permite que el servidor "empuje" datos sin que el cliente lo solicite
- Genera mucha carga en el servidor (muchas peticiones por segundo)
- No es verdaderamente "en tiempo real"
- **No funcionciona correctamente** porque como cada peticion es independiente se guarda la sesión , por lo que al gestionar el emparejamiento web, no funcionaba como deberia, una solucion es usar cookies.

**B) Frontend Simulado:**
Por las limitaciones de HTTP, el frontend optó por una simulación completamente cliente-side:
1. El cliente JavaScript simula la "búsqueda de rival" con un temporizador local 
2. **El "rival" es generado completamente en el navegador del cliente** mediante código JavaScript 
   - Genera puntos aleatorios cada 5-15 segundos
   - Los puntos del rival se incrementan localmente, sin comunicación con el servidor
   - **No existe un segundo jugador real**
3. El temporizador de 3 minutos se ejecuta en el navegador del cliente
4. Las validaciones de palabras del usuario SÍ se envían al servidor mediante `POST /validar` 
5. Al finalizar, se comparan los puntos del usuario (reales) con los del "rival" 


**¿Por qué no se usa el backend 1vs1 real en la versión web?**

1. **HTTP no permite comunicación en tiempo real:**
   - **Consola:** El socket TCP permanece abierto. El servidor puede enviar actualizaciones cuando el rival suma puntos (`AtenderJugador.java:41`)
   - **HTTP:** El servidor solo responde a peticiones. No puede "empujar" datos al cliente proactivamente

2. **Polling sería necesario pero ineficiente:**
   - Para obtener los puntos del rival en tiempo real, el cliente JavaScript debería:
     - Hacer `GET /1vs1/estado` cada 1-2 segundos constantemente
     - Esto genera cientos de peticiones HTTP durante una partida de 3 minutos
     - Carga innecesaria en el servidor
     - Latencia visible (no es verdadero tiempo real)

3. **Estado compartido funciona distinto:**
   - **Consola:** `EstadoPartida` es un objeto Java compartido en memoria entre dos hilos que atienden a los jugadores (`AtenderModalidad1vs1.java:17-25`)
   - **HTTP con backend:** `PartidaWeb` existe en el servidor pero cada cliente debe consultarlo activamente. No hay notificación automática

4. **Complejidad del frontend:**
   - Implementar polling en JavaScript con `setInterval` + `fetch()`
   - Manejar sincronización de temporizadores entre navegadores
   - Gestionar desconexiones, abandonos, timeouts
   - Mucho más complejo que la simulación local

**Soluciones alternativas (no implementadas):**
- **WebSockets:** Permitirían conexiones persistentes bidireccionales desde el navegador (requiere servidor WebSocket)
- **Server-Sent Events (SSE):** El servidor podría enviar actualizaciones al cliente cuando cambia el estado
- **Long Polling:** Peticiones que se mantienen abiertas hasta que hay actualizaciones

Por simplicidad, y para mantener el proyecto enfocado en sockets TCP y concurrencia básica sin tecnologías adicionales, **se optó por una simulación puramente cliente-side** donde el rival es generado localmente por JavaScript.

**Nota:** En el desarrollo de la comunicación cliente HTTP-servidor mediante JavaScript y la lógica de la modalidad 1vs1 simulada, se utilizó asistencia de **IA** para resolver la complejidad de manejar peticiones asincrónicas con `fetch()`, manipulación del DOM, y la orquestación de temporizadores y estados del juego en el navegador.

---

## Arquitectura del Sistema

### Servidor Unificado
**`Servidor.java`**: Punto de entrada único que permite elegir el modo de despliegue al iniciar:
- **Modo Consola (Puerto 7777)**: Para clientes TCP de terminal
- **Modo Web (Puerto 7070)**: Para clientes HTTP mediante navegador

El servidor utiliza un pool de hilos (`ExecutorService`) para manejar múltiples clientes concurrentemente.

---

### Componentes del Modo Consola (TCP - Puerto 7777)

- **`AtenderConexion.java`**: Maneja la conexión inicial y presenta el menú de modalidades
- **`AtenderModalidadNormal.java`**: Gestiona partidas individuales en consola
- **`AtenderModalidad1vs1.java`**: Coordina partidas entre dos jugadores mediante estado compartido
- **`AtenderJugador.java`**: Maneja la interacción con cada jugador en modo 1v1
- **`GestorSalas.java`**: Administra las salas de espera para el emparejamiento
- **`SalaEspera.java`**: Cola bloqueante para emparejar jugadores
- **`Emparejar.java`**: Hilo dedicado a crear partidas cuando hay dos jugadores esperando

---

### Componentes del Modo Web (HTTP - Puerto 7070)

**Backend (Implementado pero no utilizado por el frontend):**
- **`AtenderHTTP.java`**: Procesa peticiones HTTP GET/POST y enruta a los handlers correspondientes
- **`GestorSalasWeb.java`**: Sistema completo de gestión de partidas 1vs1 web 
- **`EmparejarWeb.java`**: Hilo que empareja jugadores desde una cola bloqueante 
- **`PartidaWeb.java`**: Modelo de partida que mantiene estado de dos jugadores reales 
- **`Web.java`**: Utilidades para construir respuestas HTTP, servir archivos estáticos, y manejar AJAX
- **Endpoints disponibles:**
  - `POST /1vs1/join` - Unirse a la cola de emparejamiento
  - `GET /1vs1/estado` - Obtener estado actual de la partida (puntos de ambos jugadores)
  - `POST /1vs1/validar` - Validar palabra en contexto de partida 1vs1
  - `POST /1vs1/rendirse` - Rendirse en una partida activa
  - `GET /letras` - Obtener las letras del dia
  - `POST /validar` - Validar palabra
- **Archivos estáticos:** HTML, CSS, y JavaScript en `src/servidor/web/statics/`

**Frontend (Cliente Web):**
- **`modalidadNormal.js`**
- **`modalidad1vs1.js`**


---

### Cliente Consola
- **`Cliente.java`**: Interfaz de línea de comandos para interactuar con el servidor TCP
- Protocolo simple basado en comandos `<CLIENT_TALK>` y `<CLIENT_LISTEN>`
- Mantiene conexión TCP persistente durante toda la sesión

### Modelo de Dominio
- **`Usuario.java`**: Representa un jugador con su puntuación y palabras encontradas
- **`GestorSalas.java`**: Administración de salas de espera para modo 1v1
- **`SalaEspera.java`**: Cola bloqueante para emparejar jugadores

### Utilidades
- **`Funcionalidad.java`**: Lógica de validación de palabras y sistema de puntuación
- **`Serializador.java` / `Deserializador.java`**: Gestión de archivos XML con JAXB

### Datos XML
- **`Dia.java`**, **`Palabra.java`**, **`Palabreto.java`**: Clases JAXB para mapear los archivos XML de desafíos diarios

---

## Estructura del Proyecto

```
TrabajoDistribuidos/
├── src/                          # Código fuente
│   ├── cliente/
│   │   └── Cliente.java          # Cliente de consola TCP
│   ├── servidor/
│   │   ├── Servidor.java         # Servidor unificado (permite elegir puerto 7777 o 7070)
│   │   ├── AtenderConexion.java  # Gestor de conexiones TCP para modo consola
│   │   ├── AtenderModalidadNormal.java  # Modalidad normal para consola
│   │   ├── modalidad1v1/         # Implementación 1v1 para consola
│   │   │   ├── AtenderModalidad1vs1.java
│   │   │   ├── AtenderJugador.java
│   │   │   └── Emparejar.java
│   │   └── web/                  # Componentes para modo web
│   │       ├── AtenderHTTP.java  # Handler de peticiones HTTP
│   │       ├── PartidaWeb.java   # Modelo de partida web 
│   │       ├── GestorSalasWeb.java  # Gestor de partidas web 
│   │       ├── EmparejarWeb.java    # Emparejamiento web 
│   │       └── statics/          # Frontend HTML/CSS/JS
│   │           ├── index.html
│   │           ├── ModalidadNormal.html
│   │           ├── Modalidad1vs1.html
│   │           ├── modalidadNormal.js
│   │           ├── modalidad1vs1.js  # Usa simulación
│   │           └── styles.css
│   ├── modeloDominio/
│   │   ├── Usuario.java          # Modelo de usuario
│   │   ├── EstadoPartida.java    # Estado compartido para 1v1 consola
│   │   ├── EstadoPartidaWeb.java # Estado para 1v1 web 
│   │   ├── GestorSalas.java      # Administrador de salas TCP
│   │   └── SalaEspera.java       # Cola de espera bloqueante
│   ├── utils/
│   │   ├── Funcionalidad.java    # Lógica del juego y validación
│   │   ├── Web.java              # Utilidades HTTP (respuestas, MIME, JSON)
│   │   ├── Serializador.java     # Serialización XML
│   │   └── Deserializador.java   # Deserialización XML
│   └── xml/
│       ├── JAXB/
│       │   ├── Dia.java
│       │   ├── Palabra.java
│       │   └── Palabreto.java
│       └── Dias/                 # Archivos XML de desafíos diarios
│           ├── Dia1.xml
│           ├── Dia2.xml
│           └── ...
└── README.md
```

---

## Requisitos

- **Java Development Kit** (JDK 8 o superior)
- **JAXB:** Para parsear archivos XML (incluido en JDK)
- **Compiler compliance level:** 1.8

---

## Cómo Ejecutar

### Iniciar el Servidor
1. Compilar y ejecutar `Servidor.java`
2. El servidor preguntará qué modo de despliegue usar:
   - **Opción 1 - Modo Web:** El servidor escuchará en el puerto **7070** (HTTP)
   - **Opción 2 - Modo Consola:** El servidor escuchará en el puerto **7777** (TCP)

### Modo Web (Opción 1)
1. Iniciar el servidor y seleccionar opción `1`
2. Abrir un navegador web y navegar a `http://localhost:7070`
3. Seleccionar la modalidad desde la página principal
4. **Modalidad Normal:** Funciona completamente
5. **Modalidad 1vs1:** Es una simulación cliente-side 

### Modo Consola (Opción 2)
1. Iniciar el servidor y seleccionar opción `2`
2. Ejecutar uno o más clientes con `Cliente.java`
3. Los clientes se conectan a `localhost:7777`
4. Seleccionar modalidad de juego desde el menú del cliente
5. **Modalidad 1vs1:** Funciona completamente, empareja dos clientes reales


**Nota:** Solo puedes ejecutar una instancia del servidor a la vez en un modo específico. Para probar ambos modos, debes reiniciar el servidor.

---

## Protocolo de Comunicación

El sistema utiliza un protocolo simple basado en comandos:

- **`<CLIENT_LISTEN>`**: El cliente debe escuchar y mostrar mensajes del servidor
- **`<CLIENT_TALK>`**: El cliente puede enviar entrada al servidor
- **`<CLIENT_EXITCODE>`**: Comando interno para finalizar la conexión

---


## Tecnologías Utilizadas

- **Java SE:** Lenguaje de programación principal
- **Java Sockets:** Comunicación cliente-servidor (TCP)
- **ExecutorService:** Pool de hilos para concurrencia
- **JAXB (Java Architecture for XML Binding):** Deserialización de archivos XML
- **Concurrent Collections:**
  - `ConcurrentHashMap` para gestión de salas
  - `BlockingQueue` para sincronización de jugadores
  - `CountDownLatch` para coordinación de finalización de partidas

---

## Créditos y Licencias

Este proyecto utiliza componentes de interfaz web desarrollados por terceros. A continuación se listan los recursos utilizados con sus respectivos autores y enlaces originales:

- **Animated Card** - [UIverse](https://uiverse.io/alexruix/heavy-elephant-39) - Autor: Alex Ruiz
- **Botones Play** - [UIverse](https://uiverse.io/CashOnlySeb/mighty-husky-81) - Autor: CashOnlySeb
- **3D NEXBOT** - [Spline Community](https://app.spline.design/community/file/615b9422-9985-43f6-8593-d7d7bc3b0be1) - Autor: aximoris
- **Botón Inicio** - [UIverse](https://uiverse.io/portseif/popular-octopus-83) - Autor: portseif
- **Botón Letras** - [UIverse](https://uiverse.io/levxyca/tidy-mayfly-7) - Autor: levxyca
- **Botón Validar** - [UIverse](https://uiverse.io/TISEPSE/ugly-badger-82) - Autor: TISEPSE
- **Loader** - [UIverse](https://uiverse.io/andrew-manzyk/unlucky-mouse-21) - Autor: Andrew Manzyk

---

## Autor

**Santiago Die**

Universidad de La Rioja

Sistemas Distribuidos 2025-2026


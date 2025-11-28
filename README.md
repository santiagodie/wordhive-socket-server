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

## Características Principales

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
- **Gestión de Salas:** Sistema automático de emparejamiento para partidas 1v1
- **Validación de Palabras:** Comprueba que las palabras sean válidas y no se repitan

---

## Arquitectura del Sistema

### Servidor
El servidor escucha en el puerto **7777** y utiliza los siguientes componentes:

- **`Servidor.java`**: Punto de entrada principal, inicializa el pool de hilos y acepta conexiones
- **`AtenderConexion.java`**: Maneja la conexión inicial y presenta el menú de modalidades
- **`AtenderModalidadNormal.java`**: Gestiona partidas individuales
- **`AtenderModalidad1vs1.java`**: Coordina partidas entre dos jugadores
- **`AtenderJugador.java`**: Maneja la interacción con cada jugador en modo 1v1
- **`GestorSalas.java`**: Administra las salas de espera para el emparejamiento
- **`SalaEspera.java`**: Cola de espera para emparejar jugadores
- **`Emparejar.java`**: Hilo dedicado a crear partidas cuando hay dos jugadores esperando

### Cliente
- **`Cliente.java`**: Interfaz de línea de comandos para interactuar con el servidor
- Protocolo simple basado en comandos `<CLIENT_TALK>` y `<CLIENT_LISTEN>`

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
├── bin/                          # Archivos compilados (.class)
│   ├── cliente/
│   ├── servidor/
│   ├── modeloDominio/
│   ├── utils/
│   └── xml/
├── src/                          # Código fuente
│   ├── cliente/
│   │   ├── Cliente.java          # Cliente principal
│   │   └── Cliente2.java         # Cliente alternativo
│   ├── servidor/
│   │   ├── Servidor.java         # Servidor principal
│   │   ├── AtenderConexion.java  # Gestor de conexiones
│   │   ├── AtenderModalidadNormal.java
│   │   └── modalidad1v1/
│   │       ├── AtenderModalidad1vs1.java
│   │       ├── AtenderJugador.java
│   │       └── Emparejar.java
│   ├── modeloDominio/
│   │   ├── Usuario.java          # Modelo de usuario
│   │   ├── GestorSalas.java      # Administrador de salas
│   │   └── SalaEspera.java       # Cola de espera
│   ├── utils/
│   │   ├── Funcionalidad.java    # Lógica del juego
│   │   ├── Serializador.java
│   │   └── Deserializador.java
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

- **Java Development Kit (JDK):** 8 o superior
- **JAXB:** Para parsear archivos XML (incluido en JDK 8, requiere dependencia adicional en JDK 9+)
- **Sistema Operativo:** Windows, Linux o macOS

---

## Modos de Juego

### Modalidad Normal
- Juego individual sin límite de tiempo
- El jugador intenta formar todas las palabras posibles
- Sistema de puntuación acumulativa
- Para salir: escribir `exit now`

### Modalidad 1v1
- Partidas competitivas entre dos jugadores
- Duración: 3 minutos
- Los jugadores compiten simultáneamente
- Gana quien acumule más puntos
- Sistema de emparejamiento automático mediante salas de espera

---

## Protocolo de Comunicación

El sistema utiliza un protocolo simple basado en comandos:

- **`<CLIENT_LISTEN>`**: El cliente debe escuchar y mostrar mensajes del servidor
- **`<CLIENT_TALK>`**: El cliente puede enviar entrada al servidor
- **`<CLIENT_EXITCODE>`**: Comando interno para finalizar la conexión

---

## Reglas del Juego

1. **Longitud mínima:** Las palabras deben tener al menos 3 letras
2. **Letra central obligatoria:** Todas las palabras deben contener la letra central
3. **Solo letras permitidas:** Solo se pueden usar las 7 letras del día
4. **Sin repeticiones:** Cada palabra solo se puede usar una vez por partida
5. **Palabreto:** Palabra formada por las 7 letras en una palabra , este otorga un bonus (10 + longitud puntos)

---

## Tecnologías Utilizadas

- **Java SE:** Lenguaje de programación principal
- **Java Sockets:** Comunicación cliente-servidor (TCP)
- **ExecutorService:** Pool de hilos para concurrencia
- **JAXB (Java Architecture for XML Binding):** Deserialización de archivos XML
- **Concurrent Collections:**
  - `ConcurrentHashMap` para gestión de salas
  - `BlockingQueue` para sincronización de jugadores
  - `AtomicInteger`, `AtomicBoolean` para contadores thread-safe
  - `CountDownLatch` para coordinación de finalización de partidas

---

## Autor

**Santiago Die**

Universidad de La Rioja

Sistemas Distribuidos 2025-2026


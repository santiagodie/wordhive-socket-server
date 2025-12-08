/**
 * @author Santiago Die
 * Modalidad 1vs1 SIMULADA 
 */

const palabrasValidadas = new Set();
let puntosAcumulados = 0;
let puntosRival = 0;
let tiempoRestante = 180; 
let intervalTemporizador = null;
let juegoActivo = false;

let inputPalabra, btnValidar, resultadoDiv, puntosSpan, puntosRivalSpan, listaPalabras, gameContainer;

window.addEventListener('DOMContentLoaded', async () => {
    document.body.classList.add('show-content');
    gameContainer = document.querySelector('.game-container');
    
    if (!gameContainer) {
        console.error('No se encontró el game-container');
        return;
    }
    iniciar1vs1Simulado();
});

async function iniciar1vs1Simulado() {
    mostrarPantallaCarga();
    await simularBusquedaRival();   
    mostrarRivalEncontrado();
    setTimeout(async () => {
        // Llamamos directamente a configurarInterfazJuego sin recargar
        await configurarInterfazJuego();
        iniciarTemporizador();
        simularRivalJugando();
    }, 2000);
}

/**
 * Simula la búsqueda de rival con animación
 */
function simularBusquedaRival() {
    return new Promise(resolve => {
        const tiempoEspera = Math.random() * 2000 + 3000;
        
        let puntitos = 0;
        const intervalPuntitos = setInterval(() => {
            puntitos = (puntitos + 1) % 4;
            const loaderText = gameContainer.querySelector('.loader-searching-text');
            if (loaderText) {
                loaderText.textContent = 'Buscando rival' + '.'.repeat(puntitos);
            }
        }, 500);
        
        setTimeout(() => {
            clearInterval(intervalPuntitos);
            resolve();
        }, tiempoEspera);
    });
}

/**
 * Muestra pantalla de carga inicial en el game-container (simulada)
 */
function mostrarPantallaCarga() {
    if (!gameContainer) return;
    
    gameContainer.innerHTML = `
        <div class="loader-searching-container">
            <div class="loader">
              <div class="text"><span>Partida Buscando </span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="text"><span>Partida Buscando</span></div>
              <div class="line"></div>
            </div>
        </div>
    `;
}

function mostrarRivalEncontrado() {
    if (!gameContainer) return;
    
    gameContainer.innerHTML = `
        <div class="rival-encontrado">
            <h2>Rival encontrado</h2>
            <p>Preparandose para comenzar...</p>
        </div>
    `;
}

async function configurarInterfazJuego() {
    gameContainer.innerHTML = `
<div class="score-section">
						<h2>Modalidad 1 vs 1</h2>
					</div>
					<div id= "temporizador" class="temporizador"></div>
					<div id="letrasDelDia" class="letras-dia"></div>
					<div id="inputPalabra" class="input-group">
						<input id="palabraInput" required="" type="text" name="text"
							autocomplete="off" class="input"> <label
							class="user-label">Palabra</label>
					</div>
					<button id="validarBtn" class="btn2">
						<span class="spn2">VALIDAR</span>
					</button>

					<div id="resultado" class="resultado-mensaje"></div>
    `;

    // Reiniciamos las referencias a los elementos
    inputPalabra = document.getElementById('palabraInput');
    btnValidar = document.getElementById('validarBtn');
    resultadoDiv = document.getElementById('resultado');
    puntosSpan = document.getElementById('puntos');
    puntosRivalSpan = document.getElementById('puntosRival');
    listaPalabras = document.getElementById('listaPalabras');
    
    await cargarLetrasDelDia();
    configurarEventos();
    juegoActivo = true;

    mostrarResultado('Rival encontrado! ¡A jugar!', 'success');

    if (puntosSpan) puntosSpan.textContent = '0';
    if (puntosRivalSpan) puntosRivalSpan.textContent = '0';
}

async function cargarLetrasDelDia() {
    console.log('Cargando letras del día...');
    try {
        const response = await fetch('/letras');
        if (!response.ok) {
            console.error('Error al cargar letras');
            return;
        }
        const data = await response.json();

        const letrasContainer = document.getElementById('letrasDelDia');
        if (letrasContainer) {
            const letrasArray = data.letras.split('').filter(letra => letra !== data.letraCentral);
            letrasContainer.innerHTML = `
                <div class="letras-info">
                    <button onclick="agregarTexto(this)" class="L1">${letrasArray[4] || ''}</button>
                    <button onclick="agregarTexto(this)" class="L2">${letrasArray[1] || ''}</button>
                    <button onclick="agregarTexto(this)" class="L3">${letrasArray[5] || ''}</button>
                    <button onclick="agregarTexto(this)" class="C1">${data.letraCentral}</button>
                    <button onclick="agregarTexto(this)" class="L4">${letrasArray[2] || ''}</button>
                    <button onclick="agregarTexto(this)" class="L5">${letrasArray[3] || ''}</button>
                    <button onclick="agregarTexto(this)" class="L6">${letrasArray[0] || ''}</button>
                </div>
            `;
            console.log('Letras cargadas correctamente');
        }
    } catch (error) {
        console.error('Error al cargar letras:', error);
    }
}

function agregarTexto(boton) {
    const input = document.getElementById('palabraInput');
    if (input) {
        input.value += boton.textContent;
    }
}

async function validarPalabra() {
    if (!juegoActivo) return;
    
    const palabra = inputPalabra.value.trim().toLowerCase();

    if (!palabra) {
        mostrarResultado('Ingresa una palabra', 'error');
        return;
    }

    if (palabrasValidadas.has(palabra)) {
        mostrarResultado('Esta palabra ya fue ingresada', 'error');
        inputPalabra.value = '';
        inputPalabra.focus();
        return;
    }

    try {
        const response = await fetch('/validar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ palabra: palabra })
        });

        if (!response.ok) {
            mostrarResultado('Error al validar la palabra', 'error');
            return;
        }

        const data = await response.json();
        const esValida = data.puntos > 0;

        if (esValida) {
            palabrasValidadas.add(palabra);
            puntosAcumulados += data.puntos;
            if (puntosSpan) puntosSpan.textContent = puntosAcumulados;
            mostrarResultado(data.resultado, 'success');
            agregarPalabraALista(data.palabra);
        } else {
            mostrarResultado(data.resultado, 'error');
        }

        inputPalabra.value = '';
        inputPalabra.focus();

    } catch (error) {
        mostrarResultado('Error de conexión', 'error');
        console.error('Error:', error);
    }
}
/**
 * Simula que el rival está jugando
 */
function simularRivalJugando() {
    const intervalRival = setInterval(() => {
        if (!juegoActivo) {
            clearInterval(intervalRival);
            return;
        }
        const puntosGanados = Math.random() < 0.1 
            ? Math.floor(Math.random() * 6) + 10
            : Math.floor(Math.random() * 5) + 1;
        puntosRival += puntosGanados;
        if (puntosRivalSpan) {
            puntosRivalSpan.textContent = puntosRival;
            
            puntosRivalSpan.style.transform = 'scale(1.3)';
            puntosRivalSpan.style.color = '#4CAF50';
            setTimeout(() => {
                puntosRivalSpan.style.transform = 'scale(1)';
                puntosRivalSpan.style.color = '';
            }, 300);
        }
    }, Math.random() * 10000 + 5000);
}
/**
 * Inicia el temporizador de 3 minutos
 */
function iniciarTemporizador() {
    let temporizadorDiv = document.getElementById('temporizador');
    if (!temporizadorDiv) {
        temporizadorDiv = document.createElement('div');
        temporizadorDiv.id = 'temporizador';
        temporizadorDiv.className = 'temporizador';
        document.body.insertBefore(temporizadorDiv, document.body.firstChild);
    }   
    intervalTemporizador = setInterval(() => {
        tiempoRestante--;
        
        const minutos = Math.floor(tiempoRestante / 60);
        const segundos = tiempoRestante % 60;
        const tiempoFormateado = `${minutos}:${segundos.toString().padStart(2, '0')}`;
        
        temporizadorDiv.textContent = `Tiempo : ${tiempoFormateado}`;
        
        if (tiempoRestante <= 30) {
            temporizadorDiv.className = 'temporizador danger';
        } else if (tiempoRestante <= 60) {
            temporizadorDiv.className = 'temporizador warning';
        }
        
        if (tiempoRestante <= 0) {
            clearInterval(intervalTemporizador);
            finalizarPartida();
        }
    }, 1000);
}
function finalizarPartida() {
    juegoActivo = false;
    clearInterval(intervalTemporizador);
    
    if (inputPalabra) inputPalabra.disabled = true;
    if (btnValidar) btnValidar.disabled = true;
    
    mostrarModalResultados();
}
function mostrarModalResultados() {
    if (!gameContainer) {
        gameContainer = document.querySelector('.game-container');
    }
    if (!gameContainer) return;
    
    const misPuntos = puntosAcumulados;
    const puntosRivalFinal = puntosRival;
    
    let mensaje = '';
    let titulo = '';
    let clase = '';

    if (misPuntos > puntosRivalFinal) {
        titulo = 'Ganaste!';
        mensaje = `Has derrotado a tu rival`;
        clase = 'victoria';
    } else if (misPuntos < puntosRivalFinal) {
        titulo = 'Perdiste';
        mensaje = `Tu rival te ha superado`;
        clase = 'derrota';
    } else {
        titulo = 'Empate!';
        mensaje = `Habeis quedado igualados`;
        clase = 'empate';
    }
    gameContainer.innerHTML = `
        <div class="modal-resultados-content ${clase}">
            <h2>${titulo}</h2>
            <div class="puntuacion-final">
                <div class="puntos-item tuyo">
                    <span class="label">Tus puntos</span>
                    <span class="valor">${misPuntos}</span>
                </div>
                <div class="separador">VS</div>
                <div class="puntos-item rival">
                    <span class="label">Rival</span>
                    <span class="valor">${puntosRivalFinal}</span>
                </div>
            </div>
            <p class="mensaje-resultado">${mensaje}</p>
            <div class="palabras-encontradas">
                <h3>Palabras encontradas (${palabrasValidadas.size})</h3>
            </div>
        </div>
    `;
}
function reiniciarPartida() {
    location.reload();
}
function mostrarResultado(mensaje, tipo) {
    if (!resultadoDiv) return;
    
    resultadoDiv.textContent = mensaje;
    resultadoDiv.className = 'resultado-mensaje ' + tipo;
    resultadoDiv.style.display = 'block';

    setTimeout(() => {
        resultadoDiv.style.display = 'none';
    }, 3000);
}
function agregarPalabraALista(palabra) {
    if (!listaPalabras) return;
    
    const li = document.createElement('li');
    li.textContent = palabra;
    listaPalabras.appendChild(li);
}
function configurarEventos() {
    if (btnValidar) {
        btnValidar.addEventListener('click', validarPalabra);
    }
    
    if (inputPalabra) {
        inputPalabra.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                validarPalabra();
            }
        });
    }
}
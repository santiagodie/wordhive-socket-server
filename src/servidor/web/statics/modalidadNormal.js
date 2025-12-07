/**
 * @author Santiago Die
 */
const inputPalabra = document.getElementById('palabraInput');
const btnValidar = document.getElementById('validarBtn');
const resultadoDiv = document.getElementById('resultado');
const puntosSpan = document.getElementById('puntos');
const listaPalabras = document.getElementById('listaPalabras');

const palabrasValidadas = new Set();
let puntosAcumulados = 0;

setTimeout(() => {
	document.body.classList.add('show-content');
}, 1000);

async function cargarLetrasDelDia() {
	console.log('Intentando cargar letras del día...');
	try {
		const response = await fetch('/letras');
		console.log('Respuesta del servidor:', response);
		if (!response.ok) {
			console.error('Error al cargar las letras del día - Status:', response.status);
			return;
		}
		const data = await response.json();
		console.log('Datos recibidos:', data);

		const letrasContainer = document.getElementById('letrasDelDia');
		console.log('Contenedor encontrado:', letrasContainer);
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
			console.log('Letras del día cargadas correctamente');
		} else {
			console.error('No se encontró el contenedor letrasDelDia');
		}
	} catch (error) {
		console.error('Error al cargar las letras del día:', error);
	}
}

function agregarTexto(boton) {
	const input = document.getElementById('palabraInput'); // ← Usa el ID correcto
	input.value += boton.textContent; 
}

async function validarPalabra() {
	const palabra = inputPalabra.value.trim().toLowerCase();

	if (!palabra) {
		mostrarResultado('Ingresa una palabra', 'error');
		return;
	}

	// Verificar si la palabra ya fue validada
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
			puntosSpan.textContent = puntosAcumulados;
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
function mostrarResultado(mensaje, tipo) {
	resultadoDiv.textContent = mensaje;
	resultadoDiv.className = 'resultado-mensaje ' + tipo;
	resultadoDiv.style.display = 'block';

	setTimeout(() => {
		resultadoDiv.style.display = 'none';
	}, 3000);
}

function agregarPalabraALista(palabra) {
	const li = document.createElement('li');
	li.textContent = palabra;
	listaPalabras.appendChild(li);
}
btnValidar.addEventListener('click', validarPalabra);
inputPalabra.addEventListener('keypress', (e) => {
	if (e.key === 'Enter') {
		e.preventDefault();
		validarPalabra();
	}
});

cargarLetrasDelDia();
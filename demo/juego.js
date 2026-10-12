/**
 * @author Santiago Die
 * Motor del juego de la demo estática (GitHub Pages), sin servidor Java.
 *
 * Sustituye a GET /letras y POST /validar: lee los retos de data/retos.json
 * (generado con tools/xml-a-json.mjs) y valida las palabras en el navegador con la
 * misma lógica que utils.Funcionalidad.
 */
const Juego = (() => {
	const URL_REPO = 'https://github.com/santiagodie/wordhive-socket-server';
	let carga = null;

	function cargarDia() {
		if (!carga) {
			carga = fetch('data/retos.json')
				.then(response => {
					if (!response.ok) {
						throw new Error('No se pudieron cargar los retos (' + response.status + ')');
					}
					return response.json();
				})
				.then(retos => {
					// Mismo criterio que Funcionalidad.cargarDiaXml: el día del mes actual
					const dia = retos[String(new Date().getDate())];
					if (!dia) {
						throw new Error('No hay reto para el día de hoy');
					}
					return dia;
				});
		}
		return carga;
	}

	// Equivale a GET /letras
	async function obtenerLetras() {
		const dia = await cargarDia();
		return { letras: dia.letras, letraCentral: dia.letraCentral };
	}

	// Equivale a POST /validar: devuelve { palabra, resultado, puntos }
	async function validar(palabra) {
		return comprobarPalabra(palabra, await cargarDia());
	}

	// Réplica de Funcionalidad.validarPalabra
	function validarPalabra(palabra, dia) {
		if (!palabra || palabra.length < 3) {
			return false;
		}
		if (!palabra.includes(dia.letraCentral)) {
			return false;
		}
		return palabra.split('').every(letra => dia.letras.indexOf(letra) !== -1);
	}

	// Réplica de Funcionalidad.comprobarPalabra para un usuario nuevo, como en cada
	// petición HTTP de AtenderHTTP. Las palabras repetidas las filtra la página antes.
	function comprobarPalabra(palabra, dia) {
		let resultado = 'Palabra no valida';
		let puntos = 0;
		if (validarPalabra(palabra, dia)) {
			const encontrada = dia.palabras.find(p => p.contenido.toLowerCase() === palabra.toLowerCase());
			if (encontrada) {
				const longitud = encontrada.longitud;
				if (encontrada.palabreto) {
					puntos = 10 + longitud;
					resultado = '¡PALABRETO! +' + puntos + 'pts';
				} else if (longitud === 3) {
					puntos = 1;
					resultado = 'Palabra valida +1pts';
				} else if (longitud === 4) {
					puntos = 2;
					resultado = 'Palabra valida +2pts';
				} else {
					puntos = longitud;
					resultado = 'Palabra valida +' + longitud + 'pts';
				}
			}
		}
		return { palabra, resultado, puntos };
	}

	function mostrarAvisoDemo() {
		const aviso = document.createElement('div');
		aviso.className = 'aviso-demo';
		aviso.setAttribute('role', 'note');
		aviso.innerHTML = 'Demo estática — la versión completa funciona sobre un servidor Java con sockets. '
			+ '<a href="' + URL_REPO + '" target="_blank" rel="noopener">Ver el código</a>';
		document.body.appendChild(aviso);
	}

	mostrarAvisoDemo();

	return { obtenerLetras, validar, comprobarPalabra };
})();

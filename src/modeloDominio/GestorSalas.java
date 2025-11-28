/**
 * @author Santiago Die
 */
package modeloDominio;

import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import servidor.modalidad1v1.Emparejar;

public class GestorSalas {
	private final ConcurrentHashMap<Integer, SalaEspera> mapSalas;
	private final AtomicInteger contadorSalas;
	private final ExecutorService pool;

	public GestorSalas(ExecutorService pool) {
		this.mapSalas = new ConcurrentHashMap<>();
		this.contadorSalas = new AtomicInteger(0);
		this.pool = pool;
	}

	private int crearSala() {
		int idSala = contadorSalas.incrementAndGet();
		String nombre = "Sala 1v1 #" + idSala;
		SalaEspera sala = new SalaEspera(nombre);
		mapSalas.put(idSala, sala);
		System.out.println("Sala de espera " + nombre + " creada");
		new Thread(new Emparejar(idSala, sala, pool)).start();
		return idSala;
	}

	private SalaEspera buscarSalaEnEspera() {
		if (mapSalas.isEmpty()) {
			int id = crearSala();
			return mapSalas.get(id);
		}

		for (Entry<Integer, SalaEspera> entry : mapSalas.entrySet()) {
			SalaEspera sala = entry.getValue();
			if (!sala.salaLlena()) {
				return sala;
			}
		}
		int id = crearSala();
		return mapSalas.get(id);
	}

	public void eliminarSala(int idSala) {
		mapSalas.remove(idSala);
		System.out.println("Sala " + idSala + " eliminada");
	}

	public SalaEspera obtenerSala(int idSala) {
		return mapSalas.get(idSala);
	}

	public boolean procesarConexion(String protocolo, Socket s) throws InterruptedException {
		if ("<CONNECT_PROTOCOL>".equals(protocolo)) {
			boolean agregado = false;
			int intentos = 0;
			while (!agregado && intentos < 3) {
				SalaEspera sala = buscarSalaEnEspera();
				if (sala != null) {
					agregado = sala.añadirJugador(s);
					if (!agregado) {
						System.out.println("Sala se llenó justo antes de añadir. Reintentando...");
						intentos++;
					}
				}
			}
			return agregado;
		}
		return false;
	}
}

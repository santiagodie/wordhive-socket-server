/**
 * @author Santiago Die
 */
package modeloDominio;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import servidor.Emparejar;

public class GestorSalas {
	private final ConcurrentHashMap<Integer, SalaEspera> mapSalas;
	private final AtomicInteger contadorSalas;
	private final ExecutorService pool;

	public GestorSalas(ExecutorService pool) {
		this.mapSalas = new ConcurrentHashMap<>();
		this.contadorSalas = new AtomicInteger(0);
		this.pool = pool;
	}

	public int crearSala() {
		int idSala = contadorSalas.incrementAndGet();
		String nombre = "Sala 1v1 #" + idSala;
		SalaEspera sala = new SalaEspera(nombre);
		mapSalas.put(idSala, sala);
		System.out.println("Sala de espera " + nombre + " creada");
		new Thread(new Emparejar(idSala, sala, pool, this)).start();
		return idSala;
	}

	public void unirseASala(int idSala, Socket s) throws InterruptedException {
		SalaEspera sala = mapSalas.get(idSala);
		if (sala != null) {
			sala.añadirJugador(s);
		}
	}

	public void eliminarSala(int idSala) {
		mapSalas.remove(idSala);
		System.out.println("Sala " + idSala + " eliminada");
	}

	public SalaEspera obtenerSala(int idSala) {
		return mapSalas.get(idSala);
	}

	public SalaEspera buscarSalaEnEspera() {
		int id = 0;
		if (mapSalas.isEmpty()) {
			id = crearSala();
		} else {
			boolean todoLleno = true;
			for (SalaEspera sala : mapSalas.values()) {
				if (!sala.salaLlena()) {
					id = sala.hashCode();
					todoLleno = false;
					break;
				}
			}
			if (todoLleno) {
				id = crearSala();
			}
		}
		return mapSalas.get(id);
	}
}

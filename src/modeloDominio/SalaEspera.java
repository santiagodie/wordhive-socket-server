/**
 * @author Santiago Die
 */
package modeloDominio;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SalaEspera {

	private final BlockingQueue<Socket> cola;
	private final String nombre;
	private final AtomicInteger jugadoresActuales = new AtomicInteger(0);

	public SalaEspera(String name) {
		this.nombre = name;
		this.cola = new LinkedBlockingQueue<>();
	}

	public boolean añadirJugador(Socket s) throws InterruptedException {
		synchronized (this) {
			if (jugadoresActuales.get() >= 2) {
				return false;
			}
		}
		cola.put(s);
		jugadoresActuales.incrementAndGet();
		System.out.println("Jugador añadido a " + nombre);
		return true;
	}

	public Socket[] esperarJugadores() throws InterruptedException {
		Socket[] pareja = new Socket[2];
		pareja[0] = cola.take();
		pareja[1] = cola.take();
		synchronized (this) {
			jugadoresActuales.set(0);
		}

		return pareja;
	}

	public int getJugadoresEsperando() {
		return cola.size();
	}

	public String getNombre() {
		return nombre;
	}

	public boolean salaLlena() {
		return jugadoresActuales.get() >= 2;
	}
}

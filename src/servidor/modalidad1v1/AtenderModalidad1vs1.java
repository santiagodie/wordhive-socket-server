/**
 * @author Santiago Die
 */
package servidor.modalidad1v1;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import modeloDominio.EstadoPartida;
import xml.JAXB.Dia;

public class AtenderModalidad1vs1 implements Runnable {
	private Socket jugador1;
	private Socket jugador2;
	private Dia dia;
	private final EstadoPartida estado;
	private final ExecutorService pool;

	public AtenderModalidad1vs1(Socket jugador1, Socket jugador2, Dia dia, ExecutorService pool) {
		this.jugador1 = jugador1;
		this.jugador2 = jugador2;
		this.dia = dia;
		this.estado = new EstadoPartida();
		this.pool = pool;
	}

	@Override
	public void run() {
		Future<?> temporizador = null;
		try {
			pool.execute(new AtenderJugador(jugador1, estado, true, dia));
			pool.execute(new AtenderJugador(jugador2, estado, false, dia));

			temporizador = pool.submit(() -> {
				try {
					TimeUnit.MINUTES.sleep(3);
					estado.finalizarJuego();
					System.out.println("Tiempo agotado");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});

			estado.esperarFinalizacion();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			if (temporizador != null && !temporizador.isDone()) {
				temporizador.cancel(true);
			}
		}
	}
}

/**
 * @author Santiago Die
 */
package servidor.modalidad1v1;

import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import modeloDominio.Usuario;
import xml.JAXB.Dia;

public class AtenderModalidad1vs1 implements Runnable {
	private Socket jugador1;
	private Socket jugador2;
	private Dia dia;
	private final AtomicInteger puntosJ1 = new AtomicInteger(0);
	private final AtomicInteger puntosJ2 = new AtomicInteger(0);
	private final AtomicBoolean juegoActivo = new AtomicBoolean(true);
	private final CountDownLatch finalizacion = new CountDownLatch(2);

	public AtenderModalidad1vs1(Socket jugador1, Socket jugador2, Dia dia) {
		this.jugador1 = jugador1;
		this.jugador2 = jugador2;
		this.dia = dia;
	}

	@Override
	public void run() {
		try {
			Thread hiloJ1 = new Thread(new AtenderJugador(jugador1, puntosJ1, puntosJ2, juegoActivo, dia,finalizacion));
			Thread hiloJ2 = new Thread(new AtenderJugador(jugador2, puntosJ2, puntosJ1, juegoActivo, dia,finalizacion));

			Thread temporizador = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(3 * 60 * 1000);// 3 minutos
						juegoActivo.set(false);

					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});

			hiloJ1.start();
			hiloJ2.start();
			temporizador.start();

			finalizacion.await();
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/**
 * @author Santiago Die
 */
package servidor.modalidad1v1;

import java.net.Socket;
import java.util.concurrent.ExecutorService;

import modeloDominio.GestorSalas;
import modeloDominio.SalaEspera;
import utils.Funcionalidad;
import xml.JAXB.Dia;

public class Emparejar implements Runnable {
	private final int idSala;
	private final SalaEspera sala;
	private final ExecutorService pool;
	private final Dia dia;

	public Emparejar(int idSala, SalaEspera sala, ExecutorService pool) {
		this.idSala = idSala;
		this.sala = sala;
		this.pool = pool;
		this.dia = Funcionalidad.cargarDiaXml(true);
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Socket[] jugadores = sala.esperarJugadores();
				System.out.println("Sala " + idSala + " llena. Iniciando partida...");
				pool.execute(new AtenderModalidad1vs1(jugadores[0], jugadores[1], dia, pool));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}

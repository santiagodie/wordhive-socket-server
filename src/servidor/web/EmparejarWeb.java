/**
 * @author Santiago Die
 */
package servidor.web;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import utils.Funcionalidad;

public class EmparejarWeb implements Runnable {
	
	private final BlockingQueue<String> cola; 
	private final ConcurrentHashMap<String, PartidaWeb> partidas;
	private final ConcurrentHashMap<String, String> jugadores;
	
	public EmparejarWeb(BlockingQueue<String> cola, ConcurrentHashMap<String, PartidaWeb> partidas,
			ConcurrentHashMap<String, String> jugadores) {
		this.cola = cola;
		this.partidas = partidas;
		this.jugadores = jugadores;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String jugador1 = cola.take();
				System.out.println("J1 esperando: " + jugador1);
				String jugador2 = cola.poll(60, TimeUnit.SECONDS);
				if (jugador2 == null) {
					cola.offer(jugador1);
					continue;
				}
				String partidaId = "PARTIDA-" + UUID.randomUUID().toString();
				PartidaWeb partida = new PartidaWeb(partidaId,Funcionalidad.cargarDiaXml(true));
				partida.agregarJugador(jugador1);
				partida.agregarJugador(jugador2);
				partidas.put(partidaId, partida);
				jugadores.put(jugador1, partidaId);
				jugadores.put(jugador2, partidaId);
				System.out.println("Partida creada: " + jugador1 + " vs " + jugador2);

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}

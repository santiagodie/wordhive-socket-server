/**
 * @author Santiago Die
 */
package servidor.web;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import modeloDominio.EstadoPartidaWeb;

public class GestorSalasWeb {

	private final BlockingQueue<String> cola;
	private final ConcurrentHashMap<String, PartidaWeb> partidas;
	private final ConcurrentHashMap<String, String> jugadores;
	private final ExecutorService pool;

	public GestorSalasWeb( ExecutorService pool) {
		this.cola = new LinkedBlockingQueue<>();
		this.partidas = new ConcurrentHashMap<>();
		this.jugadores = new ConcurrentHashMap<>();
		this.pool = pool;
		pool.execute(new EmparejarWeb(cola,partidas,jugadores));

		System.out.println("GestorSalasWeb inicializado");
	}

	public boolean unirseAPartida(String clienteID) {
		String partidaId = jugadores.get(clienteID);
		if (partidaId != null) {
			PartidaWeb partida = partidas.get(partidaId);
			if (partida != null && partida.getEstado() != Estados.FINALIZADA) {
				return true;
			}
			jugadores.remove(clienteID);
		}
		boolean agregado = cola.offer(clienteID);
		if (agregado) {
			System.out.println("Jugador en cola. Esperando");
		}
		return agregado;
	}

	public EstadoPartidaWeb obtenerEstado(String clienteID) {
		String partidaId = jugadores.get(clienteID);

		if (partidaId == null) {
			return new EstadoPartidaWeb("esperando_rival", 0, 0, "", 0);
		}
		PartidaWeb partida = partidas.get(partidaId);
		if (partida == null) {
			jugadores.remove(clienteID);
			return new EstadoPartidaWeb("esperando_rival", 0, 0, "", 0);
		}

		int numeroJugador = partida.getNumeroJugador(clienteID);
		int misPuntos = (numeroJugador == 1) ? partida.getPuntosJ1() : partida.getPuntosJ2();
		int puntosRival = (numeroJugador == 1) ? partida.getPuntosJ2() : partida.getPuntosJ1();

		return new EstadoPartidaWeb(partida.getEstado().toString().toLowerCase(), misPuntos, puntosRival,
				partida.getResultado(clienteID), numeroJugador);
	}

	public PartidaWeb obtenerPartidaDeJugador(String clienteID) {
		String partidaId = jugadores.get(clienteID);
		return partidas.get(partidaId);
	}

	public boolean rendirse(String clienteID) {
		PartidaWeb partida = obtenerPartidaDeJugador(clienteID);
		if (partida != null && partida.getEstado() == Estados.JUGANDO) {
			partida.rendirse(clienteID);
			return true;
		}
		return false;
	}
}
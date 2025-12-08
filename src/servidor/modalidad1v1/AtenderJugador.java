/**
 * @author Santiago Die
 */
package servidor.modalidad1v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import modeloDominio.EstadoPartida;
import modeloDominio.Usuario;
import utils.Funcionalidad;
import xml.JAXB.Dia;

public class AtenderJugador implements Runnable {
	private final Socket socket;
	private final Usuario usuario;
	private final EstadoPartida estado;
	private final boolean esJugador1;
	private final Dia dia;

	public AtenderJugador(Socket socket, EstadoPartida estado, boolean esJugador1, Dia dia) {
		this.socket = socket;
		this.usuario = new Usuario(socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
		this.estado = estado;
		this.esJugador1 = esJugador1;
		this.dia = dia;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {

			enviarMensajesIniciales(pw);

			String palabra;
			while (estado.isJuegoActivo()) {
				pw.println("<CLIENT_TALK>");
				palabra = br.readLine();

				if (palabra == null) {
					System.out.println("Jugador " + usuario.getIdUsuario() + " se desconectó");
					estado.finalizarJuego();
					estado.setPuntos(esJugador1, -1);
					break;
				}

				if ("<CLIENT_EXITCODE>".equalsIgnoreCase(palabra)) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("Te has rendido. Fin de la partida.");
					estado.finalizarJuego();
					estado.setPuntos(esJugador1, -1);
					break;
				}

				if (!estado.isJuegoActivo()) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("La partida ha acabado");
					break;
				}

				String respuesta = Funcionalidad.comprobarPalabra(palabra, usuario, dia);
				estado.setPuntos(esJugador1, usuario.getPuntos());
				pw.println(respuesta);
			}

			mostrarResultados(pw);

		} catch (IOException e) {
			System.err.println("Error de conexión con jugador " + usuario.getIdUsuario());
			if (estado.isJuegoActivo()) {
				estado.finalizarJuego();
				estado.setPuntos(esJugador1, -1);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			estado.jugadorTerminado();
			cerrarSocket();
		}
	}

	private void enviarMensajesIniciales(PrintWriter pw) throws InterruptedException {
		pw.println("<CLIENT_LISTEN>");
		pw.println("Rival encontrado");
		pw.println("Escribe todas las palabras que puedas en 3 minutos");
		pw.println("Recuerda que siempre tienes que usar la letra central en tu palabra");
		pw.println("La partida comienza en 3...");
		TimeUnit.SECONDS.sleep(1);
		pw.println("2...");
		TimeUnit.SECONDS.sleep(1);
		pw.println("1...");
		TimeUnit.SECONDS.sleep(1);
		pw.println("¡YA! Escribe palabras:");
		pw.println("Las letras que hay que usar hoy son '" + dia.letrasToString() + "' y la letra central es '"
				+ dia.getLetraCentral() + "'");
	}

	private void mostrarResultados(PrintWriter pw) {
		int misPuntos = estado.getPuntos(esJugador1);
		int puntosRival = estado.getPuntosRival(esJugador1);

		pw.println("<CLIENT_LISTEN>");
		pw.println("Tu puntuación: " + misPuntos + " | Rival: " + puntosRival);

		if (puntosRival == -1) {
			pw.println("¡Felicidades, has ganado!");
			pw.println("Tu rival se ha rendido");
		} else if (misPuntos == -1) {
			pw.println("Te has rendido");
		} else if (misPuntos > puntosRival) {
			pw.println("¡Felicidades, has ganado!");
		} else if (misPuntos < puntosRival) {
			pw.println("Por poco, has perdido");
		} else {
			pw.println("¡Increíble, habéis empatado!");
		}
	}

	private void cerrarSocket() {
		System.out.println("Cliente desconectado: " + usuario.getIdUsuario());
		try {
			if (!socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			System.err.println("Error cerrando socket: " + e.getMessage());
		}
	}
}
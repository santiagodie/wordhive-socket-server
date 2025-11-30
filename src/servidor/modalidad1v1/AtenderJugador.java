/**
 * @author Santiago Die
 */
package servidor.modalidad1v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import modeloDominio.Usuario;
import utils.Funcionalidad;
import xml.JAXB.Dia;

public class AtenderJugador implements Runnable {

	private Socket socket;
	private Usuario usuario;
	private AtomicInteger puntos;
	private AtomicInteger puntosRival;
	private AtomicBoolean juegoActivo;
	private CountDownLatch finalizacion;

	private Dia dia;

	public AtenderJugador(Socket s, AtomicInteger puntos, AtomicInteger puntosRival, AtomicBoolean juegoActivo, Dia dia,
			CountDownLatch finalizacion) {
		this.socket = s;
		this.usuario = new Usuario(s.getInetAddress().getHostAddress() + ":" + s.getPort());
		this.puntos = puntos;
		this.puntosRival = puntosRival;
		this.juegoActivo = juegoActivo;
		this.dia = dia;
		this.finalizacion = finalizacion;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);) {
			pw.println("<CLIENT_LISTEN>");
			pw.println("Rival encontrado");
			pw.println("Escribe todas las palabras que puedas en 3 minutos");
			pw.println("Recuerda que siempre tienes que usar la letra central en tu palabra");
			pw.println("La partida comienza en 3...");
			Thread.sleep(1000);
			pw.println("2...");
			Thread.sleep(1000);
			pw.println("1...");
			Thread.sleep(1000);
			pw.println("¡YA! Escribe palabras:");
			pw.println("Las letras que hay que usar hoy son '" + dia.letrasToString() + "'y la letra central es '"
					+ dia.getLetraCentral() + "'");
			String palabra = "";
			while (juegoActivo.get()) {
				pw.println("<CLIENT_TALK>");
				palabra = br.readLine();

				if (palabra == null) {
					System.out.println("Jugador " + usuario.getIdUsuario() + " se desconectó");
					juegoActivo.set(false);
					break;
				}
				if ("<CLIENT_EXITCODE>".equalsIgnoreCase(palabra)) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("Te has rendido. Fin de la partida.");
					juegoActivo.set(false);
					puntos.set(-1);
					break;
				}
				if (!juegoActivo.get()) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("La partida a acabado");
					break;
				}
				String respuesta = Funcionalidad.comprobarPalabra(palabra, usuario, dia);
				puntos.set(usuario.getPuntos());
				pw.println(respuesta);
			}
			pw.println("<CLIENT_LISTEN>");
			pw.println("Tu puntuación: " + puntos.get() + " | Rival: " + puntosRival.get());
			if (puntosRival.get() == -1) {
				pw.println("¡Felicidades, has ganado!");
				pw.println("Tu rival se ha rendido");
			} else if (puntos.get() > puntosRival.get()) {
				pw.println("¡Felicidades, has ganado!");
			} else if (puntos.get() < puntosRival.get()) {
				pw.println("Por poco, has perdido");
			} else {
				pw.println("¡Increíble, habéis empatado!");
			}

		} catch (IOException e) {
			e.printStackTrace();
			if (juegoActivo.get()) {
				System.out.println("Error de conexión con jugador " + usuario.getIdUsuario());
				juegoActivo.set(false);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			finalizacion.countDown();
			System.out.println("Cliente desconectado");
			try {

				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

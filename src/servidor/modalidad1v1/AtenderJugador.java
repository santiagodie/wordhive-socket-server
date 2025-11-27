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

import utils.Funcionalidad;
import xml.JAXB.Dia;
import xml.JAXB.Usuario;

public class AtenderJugador implements Runnable {

	private Socket socket;
	private Usuario usuario;
	private AtomicInteger puntos;
	private AtomicInteger puntosRival;
	private AtomicBoolean juegoActivo;
	private int numeroJugador;
	private CountDownLatch finalizacion;
	
	private Dia dia;

	public AtenderJugador(Socket s, Usuario user, AtomicInteger puntos,AtomicInteger puntosRival, AtomicBoolean juegoActivo, int num,Dia dia,CountDownLatch finalizacion) {
		this.socket = s;
		this.usuario = user;
		this.puntos = puntos;
		this.puntosRival = puntosRival;
		this.numeroJugador = num;
		this.juegoActivo = juegoActivo;
		this.dia = dia;
		this.finalizacion = finalizacion;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);) {
			pw.println("Rival encontrado");
			pw.println("Escribe todas las palabras que puedas en 3 minutos");
			pw.println("La partida comienza en 3...");
			Thread.sleep(1000);
			pw.println("2...");
			Thread.sleep(1000);
			pw.println("1...");
			Thread.sleep(1000);
			pw.println("¡YA! Escribe palabras:");
			String palabra;
			while (juegoActivo.get() && (palabra = br.readLine()) != null) {

				if ("exitCode".equalsIgnoreCase(palabra)) {
					pw.println("Te has rendido. Fin de la partida.");
					juegoActivo.set(false);
					break;
				}
				if (!juegoActivo.get()) {
					pw.println("La partida a acabado");
					break;
				}
				String respuesta = Funcionalidad.comprobarPalabra(palabra, usuario, dia);
				puntos.set(usuario.getPuntos());
                pw.println(respuesta);
			}
			if(puntos.get() > puntosRival.get()) {pw.println("Felicidades has ganado!");}
			else if(puntos.get() < puntosRival.get()) {pw.println("Por poco, has perdido");}
			else if(puntos.get() == puntosRival.get()) {pw.println("Increible, habeis empatado");}
			

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}finally {
            finalizacion.countDown();
        }

	}

}

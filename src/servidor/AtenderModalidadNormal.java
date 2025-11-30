package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import modeloDominio.Usuario;
import utils.Funcionalidad;
import xml.JAXB.Dia;

public class AtenderModalidadNormal implements Runnable {

	private Socket s;
	private Dia dia;
	private Usuario user;

	public AtenderModalidadNormal(Socket s, Dia serverDia) {

		this.s = s;
		this.dia = serverDia;
		user = new Usuario(s.getInetAddress().getHostAddress() + ":" + s.getPort());
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);) {

			pw.println("<CLIENT_LISTEN>");
			pw.println("Bienvenido a la modalidad Normal");
			pw.println("Escribe todas palabras que puedas solo con las letras del dia para ganar puntos");
			pw.println("Recuerda que siempre tienes que usar la letra central en tu palabra");
			pw.println("Las letras que hay que usar hoy son '"+dia.letrasToString()+ "'y la letra central es '"+dia.getLetraCentral()+"'");
			
			String palabra;
			String respuesta;
			
			while (true) {
				pw.println("<CLIENT_TALK>");
				palabra = br.readLine();
				
				if (palabra == null) {
					System.out.println("Cliente desconectado");
					break;
				}
				
				if ("<CLIENT_EXITCODE>".equals(palabra)) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("Gracias por jugar, desconectando...");
					pw.println("Puntuacion final: " + user.getPuntos() + " puntos");
					break;
				}
				
				respuesta = Funcionalidad.comprobarPalabra(palabra, user, dia);
				pw.println("<CLIENT_LISTEN>");
				pw.println(respuesta);
				pw.println("Puntos actuales: " + user.getPuntos());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Cliente desconectado");
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

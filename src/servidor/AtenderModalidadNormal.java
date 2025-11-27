package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import utils.Funcionalidad;
import xml.JAXB.Dia;
import xml.JAXB.Usuario;

public class AtenderModalidadNormal implements Runnable {

	private Socket s;
	private Dia dia;
	private Usuario user;

	public AtenderModalidadNormal(Socket s, Dia serverDia) {

		this.s = s;
		this.dia = serverDia;
		user = Funcionalidad.buscarUsuario(s.getInetAddress().getHostAddress());
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);) {

			pw.println("Bienvenido a la modalidad Normal");

			String palabra;
			String respuesta;

			while ((palabra = br.readLine()) != null) {
				if ("exitCode".equals(palabra)) {
					pw.println("Gracias por jugar, desconectando...");
					return;
				}
				respuesta = Funcionalidad.comprobarPalabra(palabra, user, dia);
				pw.println(respuesta);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Funcionalidad.guardarUsuario(user, user.getIp());
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

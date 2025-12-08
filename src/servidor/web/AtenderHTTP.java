/**
 * @author Santiago Die
 */
package servidor.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;

import modeloDominio.EstadoPartidaWeb;
import modeloDominio.Usuario;
import utils.Web;
import xml.JAXB.Dia;

public class AtenderHTTP implements Runnable {

	private static final String HOMEDIR = Paths.get("src", "servidor", "web", "statics").toString();
	private Socket socket;
	private Dia dia;
	private Usuario usuario;
	private GestorSalasWeb gestorSalas;

	public AtenderHTTP(Socket socket, Dia dia, GestorSalasWeb gestorSalas) {
		this.socket = socket;
		this.dia = dia;
		this.gestorSalas = gestorSalas;
		this.usuario = new Usuario(socket.getInetAddress().getHostAddress());
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream out = socket.getOutputStream()) {

			String peticion = in.readLine();
			if (peticion == null || peticion.isEmpty())
				return;

			System.out.println(peticion);
			String sessionId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

			if (peticion.startsWith("GET ") || peticion.startsWith("HEAD ")) {
				
				if (peticion.contains("/letras")) {
					Web.atenderGetLetras(out, dia);
				} else if (peticion.contains("/1vs1/estado")) {
					EstadoPartidaWeb estado = gestorSalas.obtenerEstado(sessionId);
					Web.enviarJSON(out, estado.toJSON());
				} else {
					Web.atenderGETyHEAD(peticion, out, peticion.startsWith("HEAD "), HOMEDIR);
				}
			} 
			else if (peticion.startsWith("POST ")) {
				if (peticion.contains("/1vs1/join")) {
					boolean exito = gestorSalas.unirseAPartida(sessionId);
					String json = "{\"exito\":" + exito + ",\"sessionId\":\"" + sessionId + "\"}";
					Web.enviarJSON(out, json);
				} else if (peticion.contains("/1vs1/validar")) {
					Web.atenderValidar1vs1(peticion, in, out, gestorSalas, sessionId);
				} else if (peticion.contains("/1vs1/rendirse")) {
					boolean exito = gestorSalas.rendirse(sessionId);
					Web.enviarJSON(out, "{\"exito\":" + exito + "}");
				} else if (peticion.contains("/validar")) {
					Web.atenderValidacionAJAX(peticion, in, out, usuario, dia);
				} else {
					Web.atenderPOST(peticion, in, out, usuario, dia);
				}
			} else {
				String html = Web.makeHTMLErrorText(501, "Not Implemented");
				byte[] contenido = html.getBytes("UTF-8");
				Web.sendMIMEHeading(out, 501, "text/html", contenido.length);
				out.write(contenido);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
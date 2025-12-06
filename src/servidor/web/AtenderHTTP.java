package servidor.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;

import modeloDominio.Usuario;
import utils.Web;
import xml.JAXB.Dia;

public class AtenderHTTP implements Runnable {

	private static final String HOMEDIR = Paths.get("src", "servidor", "web", "statics").toString();
	private Socket socket;
	private Dia dia;
	private Usuario usuario;

	public AtenderHTTP(Socket socket, Dia dia) {
		this.socket = socket;
		this.dia = dia;
		this.usuario = new Usuario(socket.getInetAddress().getHostAddress());
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream out = socket.getOutputStream()) {

			String peticion = in.readLine();

			if (peticion == null || peticion.isEmpty()) {
				return;
			}

			System.out.println(peticion);
			if (peticion.startsWith("GET ") || peticion.startsWith("HEAD ")) {
				Web.atenderGETyHEAD(peticion, out, peticion.startsWith("HEAD "),HOMEDIR);
			} else if (peticion.startsWith("POST ")) {
				Web.atenderPOST(peticion, in, out,usuario,dia);
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

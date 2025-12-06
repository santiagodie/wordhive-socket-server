package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import modeloDominio.Usuario;
import xml.JAXB.Dia;

public class Web {
	public static void atenderGETyHEAD(String peticion, OutputStream out, boolean soloHeaders, String HOMEDIR)
			throws IOException {

		File archivo = buscaFichero(peticion, HOMEDIR);

		if (!archivo.exists() || !archivo.isFile()) {
			String html = makeHTMLErrorText(404, "File Not Found");
			byte[] contenido = html.getBytes("UTF-8");
			sendMIMEHeading(out, 404, "text/html", contenido.length);
			out.write(contenido);
			return;
		}
		String contentType = obtenerContentType(archivo.getName());
		sendMIMEHeading(out, 200, contentType, archivo.length());

		if (!soloHeaders) {
			enviarArchivo(out, archivo);
		}
	}

	public static void atenderPOST(String lineaPrincipal, BufferedReader in, OutputStream out, Usuario usuario, Dia dia)
			throws IOException {

		// Leer headers
		String linea;
		int contentLength = 0;
		while ((linea = in.readLine()) != null && !linea.isEmpty()) {
			if (linea.toLowerCase().startsWith("content-length:")) {
				contentLength = Integer.parseInt(linea.substring(15).trim());
			}
		}

		// Leer body
		char[] buffer = new char[contentLength];
		in.read(buffer, 0, contentLength);
		String body = new String(buffer);

		// Parsear palabra
		String palabra = "";
		if (body.startsWith("palabra=")) {
			palabra = body.substring(8);
			palabra = java.net.URLDecoder.decode(palabra, "UTF-8");
		}

		// Comprobar palabra
		String respuesta = Funcionalidad.comprobarPalabra(palabra, usuario, dia);

		// Generar respuesta HTML
		String html = crearHTMLRespuesta(palabra, respuesta, usuario.getPuntos());
		byte[] contenido = html.getBytes("UTF-8");

		sendMIMEHeading(out, 200, "text/html", contenido.length);
		out.write(contenido);
		out.flush();
	}

	public static void enviarArchivo(OutputStream out, File archivo) throws IOException {
		try (FileInputStream fis = new FileInputStream(archivo)) {
			byte[] buffer = new byte[8192];
			int bytesLeidos;
			while ((bytesLeidos = fis.read(buffer)) != -1) {
				out.write(buffer, 0, bytesLeidos);
			}
			out.flush();
		}
	}

	public static String obtenerContentType(String nombreArchivo) {
		if (nombreArchivo.endsWith(".html"))
			return "text/html";
		if (nombreArchivo.endsWith(".css"))
			return "text/css";
		if (nombreArchivo.endsWith(".js"))
			return "application/javascript";
		if (nombreArchivo.endsWith(".png"))
			return "image/png";
		if (nombreArchivo.endsWith(".jpg"))
			return "image/jpeg";
		return "text/plain";
	}

	public static String crearHTMLRespuesta(String palabra, String resultado, int puntos) {
		return String.format("<!DOCTYPE html>" + "<html>" + "<head>" + "    <meta charset=\"UTF-8\">"
				+ "    <title>Resultado</title>" + "</head>" + "<body>" + "    <h1>Palabreto</h1>"
				+ "    <p>Palabra: <strong>%s</strong></p>" + "    <p>%s</p>" + "    <p>Puntos: %d</p>"
				+ "    <a href=\"/\">Volver</a>" + "</body>" + "</html>", palabra, resultado, puntos);
	}

	// metodos practica 4

	public static File buscaFichero(String m, String HOMEDIR) {
		String fileName = "";
		if (m.startsWith("GET ")) {
			fileName = m.substring(4, m.indexOf(" ", 5));
			if (fileName.equals("/")) {
				fileName += "index.html";
			} else if (fileName.equals("/normal")) {
				fileName += "ModalidadNormal.html";
			} else if (fileName.equals("/1vs1")) {
				fileName += "Modalidad1vs1.html";
			}
		}
		if (m.startsWith("HEAD ")) {
			// A partir de una cadena de mensaje (m) correcta (comienza por HEAD)
			fileName = m.substring(6, m.indexOf(" ", 7));
			if (fileName.equals("/")) {
				fileName += "index.html";
			}else if (fileName.equals("/normal")) {
				fileName += "ModalidadNormal.html";
			} else if (fileName.equals("/1vs1")) {
				fileName += "Modalidad1vs1.html";
			}
		}
		return new File(HOMEDIR, fileName);
	}

	public static void sendMIMEHeading(OutputStream os, int code, String cType, long fSize) {
		PrintStream dos = new PrintStream(os);
		dos.print("HTTP/1.1 " + code + " ");
		if (code == 200) {
			dos.print("OK\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -6.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-length: " + fSize + "\r\n");
			dos.print("Content-type: " + cType + "\r\n");
			dos.print("\r\n");
		} else if (code == 404) {
			dos.print("File Not Found\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -6.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-length: " + fSize + "\r\n");
			dos.print("Content-type: " + "text/html" + "\r\n");
			dos.print("\r\n");
		} else if (code == 501) {
			dos.print("Not Implemented\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -6.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-length: " + fSize + "\r\n");
			dos.print("Content-type: " + "text/html" + "\r\n");
			dos.print("\r\n");
		}
		dos.flush();
	}

	public static String makeHTMLErrorText(int code, String txt) {
		StringBuffer msg = new StringBuffer("<HTML>\r\n");
		msg.append(" <HEAD>\r\n");
		msg.append(" <TITLE>" + txt + "</TITLE>\r\n");
		msg.append(" </HEAD>\r\n");
		msg.append(" <BODY>\r\n");
		msg.append(" <H1>HTTP Error " + code + ": " + txt + "</H1>\r\n");
		msg.append(" </BODY>\r\n");
		msg.append("</HTML>\r\n");
		return msg.toString();
	}

}

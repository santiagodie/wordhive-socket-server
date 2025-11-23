/**
 * @author Santiago Die
 */
package servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import utils.Deserializador;
import utils.Serializador;
import xml.JAXB.Dia;
import xml.JAXB.Palabra;
import xml.JAXB.Usuario;

public class AtenderPeticion extends Thread {
	private Socket s;
	private Dia dia;
	private Usuario user;

	public AtenderPeticion(Socket s, Dia serverDia) {

		this.s = s;
		this.dia = serverDia;
		user = buscarUsuario(s.getInetAddress().getHostAddress());

	}

	@Override
	public void run() {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);) {

			String palabra;
			String respuesta;
			while ((palabra = br.readLine()) != null) {
				if ("exitCode".equals(palabra)) {
					pw.println("Gracias por jugar, desconectando...");
					break;
				}
				respuesta = comprobarPalabra(palabra);
				pw.println(respuesta);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			guardarUsuario(user, user.getIp());
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private Usuario buscarUsuario(String IP) {
		File fileUsuario = new File(Paths.get("src", "xml", "Usuarios", IP + ".xml").toString());
		if (!fileUsuario.exists()) {
			return new Usuario(IP);
		}
		try {
			return Deserializador.deserializar(fileUsuario, Usuario.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			return new Usuario(IP);
		}

	}

	private void guardarUsuario(Usuario user, String IP) {
		File fileUsuario = new File(Paths.get("src", "xml", "Usuarios", IP + ".xml").toString());
		try {
			Serializador.serializar(user, fileUsuario);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private boolean validarPalabra(String palabra) {

		if (palabra == null || palabra.isEmpty() || palabra.length() < 3) {

			return false;
		}
		if (!palabra.contains(dia.getLetraCentral())) {
			return false;
		}
		if (!palabra.chars().allMatch(x -> dia.letrasToString().indexOf(x) != -1)) {
			return false;
		}
		return true;
	}

	private String comprobarPalabra(String mensaje) {
		String respuesta;
		if (validarPalabra(mensaje)) {
			if (user.buscarPalabra(mensaje)) {
				return respuesta = "Palabra ya usada, busca otra";
			}
			Palabra palabra;
			if ((palabra = dia.buscarPalabra(mensaje)) != null) {
				int longitud = palabra.getLongitud();
				int puntuacion = user.getPuntos();
				if (palabra.esPalabreto()) {
					puntuacion += 10 + longitud;
					respuesta = "¡PALABRETO! +" + (10 + longitud) + "pts";
				} else if (longitud == 3) {
					puntuacion += 1;
					respuesta = "Palabra valida +1pts";
				} else if (longitud == 4) {
					puntuacion += 2;
					respuesta = "Palabra valida +2pts";
				} else {
					puntuacion += longitud;
					respuesta = "Palabra valida +" + longitud + "pts";
				}
				user.setPuntos(puntuacion);
				user.agregarItem(palabra);
				return respuesta;
			}
		}
		return respuesta = "Palabra no valida";

	}
}

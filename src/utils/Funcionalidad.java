package utils;

import java.io.File;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import xml.JAXB.Dia;
import xml.JAXB.Palabra;
import xml.JAXB.Usuario;

public class Funcionalidad {

	public static Usuario buscarUsuario(String IP) {
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

	public static void guardarUsuario(Usuario user, String IP) {
		File fileUsuario = new File(Paths.get("src", "xml", "Usuarios", IP + ".xml").toString());
		try {
			Serializador.serializar(user, fileUsuario);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static Dia cargarDiaXml() {

		int numDia = (int) (Math.random() * 30) + 1;
		File fileDia = new File(Paths.get("src", "xml", "Dias", "Dia" + numDia + ".xml").toString());
		if (!fileDia.exists()) {
			return null;
		}
		try {
			return Deserializador.deserializar(fileDia, Dia.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean validarPalabra(String palabra, Dia dia) {

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

	public static String comprobarPalabra(String mensaje, Usuario user, Dia dia) {
		String respuesta;
		if (validarPalabra(mensaje, dia)) {
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

/**
 * @author Santiago Die
 */
package utils;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;

import javax.xml.bind.JAXBException;

import modeloDominio.Usuario;
import xml.JAXB.Dia;
import xml.JAXB.Palabra;

public class Funcionalidad {

	public static Dia cargarDiaXml(boolean random) {
		int numDia;
		if (random) {
			numDia = (int) (Math.random() * 30) + 1;
		} else {
			LocalDate fechaHoy = LocalDate.now();
			numDia = fechaHoy.getDayOfMonth();
		}

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
				user.agregarPalabra(palabra);
				return respuesta;
			}
		}
		return respuesta = "Palabra no valida";
	}
}

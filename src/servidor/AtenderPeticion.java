/**
 * @author Santiago Die
 */
package servidor;

import java.io.File;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import xml.JAXB.Dia;
import xml.JAXB.Palabra;

public class AtenderPeticion extends Thread {
	private Socket s;
	private Dia dia;
	private int puntuacion; 

	public AtenderPeticion(Socket s, Dia serverDia) {

		this.s = s;
		this.dia = serverDia;
		this.puntuacion = 0;

	}

	@Override
	public void run() {

	}

	private static void Menu() {

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

	private void asignarPuntuacion(String mensaje) {
		if (validarPalabra(mensaje)) {
			Palabra palabra;
			if ((palabra = dia.buscarPalabra(mensaje)) != null) {
				int longitud = palabra.getLongitud();
				if (palabra.esPalabreto()) {
					puntuacion += 10 + longitud;
				} else if (longitud == 3) {
					puntuacion += 1;
				} else if (longitud == 4) {
					puntuacion += 2;
				} else {
					puntuacion += longitud;
				}
			}
		}
	}
}

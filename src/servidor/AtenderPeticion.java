/**
 * @author Santiago Die
 */
package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import xml.JAXB.Dia;
import xml.JAXB.Palabra;
import xml.JAXB.Usuario;

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
		try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);) {

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void Menu(BufferedReader br, PrintWriter pw) throws IOException {

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

	private String asignarPuntuacion(String mensaje, Usuario u) {
		String respuesta;
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
				dia.removePalabra(palabra);
			}
			return respuesta = "";
		}
		return respuesta = "Palabra no valida";

	}
}

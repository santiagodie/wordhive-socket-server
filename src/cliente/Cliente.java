/**
 * @author Santiago Die
 */
package cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

	private static final String HOST = "localhost";
	private static final int PUERTO = 7777;

	public static void main(String[] args) {
		try (Socket socket = new Socket(HOST, PUERTO);
				PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataInputStream teclado = new DataInputStream(System.in)) {
			String palabra;
			String respuesta;
			System.out.println("Mensaje ('exit now' para terminar): ");
			
			respuesta = entrada.readLine();
			System.out.println("<Servidor>: " + respuesta);
			
			while (true) {
				System.out.println("Introduce una palabra: ");
				palabra = teclado.readLine().toLowerCase();
				if (palabra == null || palabra.isEmpty()) {
					System.out.println("Entrada no valida");
					continue;
				}
				if ("exit now".equals(palabra)) {
					salida.println("exitCode");
					respuesta = entrada.readLine();
					System.out.println("<Servidor>: " + respuesta);
					break;
				}
				salida.println(palabra);
				respuesta = entrada.readLine();
				System.out.println("<Servidor>: " + respuesta);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

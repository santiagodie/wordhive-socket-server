package cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente2 {

	private static final String HOST = "localhost";
	private static final int PUERTO = 7777;

	public static void main(String[] args) {
		try (Socket socket = new Socket(HOST, PUERTO);
				PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataInputStream teclado = new DataInputStream(System.in)) {
			String palabra;
			String linea;

			while (true) {
				linea = entrada.readLine();

				if (linea == null) {
					System.out.println("Servidor cerró la conexión");
					break;
				}
				if ("<CLIENT_TALK>".equals(linea)) {
					System.out.print("Introduce: ");
					palabra = teclado.readLine();
					if (palabra == null || palabra.isEmpty()) {
						System.out.println("Entrada no válida");
						salida.println(""); 
						continue;
					}
					if ("exit now".equalsIgnoreCase(palabra)) {
						salida.println("exitCode");

						continue;
					}
					salida.println(palabra.toLowerCase());
				} else if ("<CLIENT_LISTEN>".equals(linea)) {
					continue;
				} else {
					System.out.println("<Servidor>: " + linea);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

package cliente;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

	private static final String HOST = "localhost";
	private static final int PUERTO = 6666;

	public static void main(String[] args) {
		try (Socket socket = new Socket(HOST, PUERTO);
				PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedInputStream teclado = new BufferedInputStream(System.in)) 
		{
			System.out.println("Conectado al servidor del PalabReto");
            
            String mensaje;
            
            

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

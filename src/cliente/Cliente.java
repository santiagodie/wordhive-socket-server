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
				DataInputStream teclado = new DataInputStream(System.in)) 
		{
			
            
            String palabra;
            while(true) {
                System.out.print("Mensaje (SALIR para terminar): ");
                palabra = teclado.readLine().toLowerCase();
                
                
            	
            }
            
            

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

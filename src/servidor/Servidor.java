package servidor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.rowset.spi.XmlReader;

public class Servidor {
	

	public static void main(String[] args) {
		int nucleos = Runtime.getRuntime().availableProcessors();

		ExecutorService pool = Executors.newFixedThreadPool(nucleos);

		try (ServerSocket serverSocket = new ServerSocket(6666)) {
			while (true) {
				try {
					Socket conexion = serverSocket.accept();
					pool.execute(new AtenderPeticion(conexion));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			pool.shutdown();
		}
	}
	private void leerDia() {
		
		int numDia = (int) (Math.random() * 29) + 1;
		File fileDia = new File(Paths.get("xml","Dias", "Dia"+numDia+".xml").toString());
	}

}

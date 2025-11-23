/**
 * @author Santiago Die
 */
package servidor;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBException;

import utils.Deserializador;
import xml.JAXB.Dia;

public class Servidor {

	private static Dia diaCargado;

	public static void main(String[] args) {

		int nucleos = Runtime.getRuntime().availableProcessors();

		ExecutorService pool = Executors.newFixedThreadPool(nucleos);

		diaCargado = cargarDiaXml();

		if (diaCargado != null) {
			try (ServerSocket serverSocket = new ServerSocket(7777)) {

				System.out.println("Servidor Palabreto iniciado");
				System.out.println("Día cargado: " + diaCargado.getId());

				while (true) {
					try {
						Socket conexion = serverSocket.accept();
						System.out.println("Conectado al servidor del PalabReto");
						pool.execute(new AtenderPeticion(conexion, diaCargado));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("Cerrando servidor...");
				pool.shutdown();
			}
		} else {
			System.out.println("Fallo al intentar abrir los datos del dia");
			pool.shutdown();
		}

	}

	private static Dia cargarDiaXml() {

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
}

/**
 * @author Santiago Die
 */
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import modeloDominio.GestorSalas;
import utils.Funcionalidad;
import xml.JAXB.Dia;

public class Servidor {

	private static Dia diaCargado;
	private static GestorSalas gestorSalas;

	public static void main(String[] args) {

		int nucleos = Runtime.getRuntime().availableProcessors();

		ExecutorService pool = Executors.newFixedThreadPool(nucleos);

		diaCargado = Funcionalidad.cargarDiaXml(false);

		if (diaCargado != null) {
			gestorSalas = new GestorSalas(pool);
			try (ServerSocket serverSocket = new ServerSocket(7777)) {

				System.out.println("Servidor Palabreto iniciado");
				System.out.println("Día cargado: " + diaCargado.getId());

				while (true) {
					try {
						Socket conexion = serverSocket.accept();
						System.out.println("Conectado al servidor del PalabReto");
						pool.execute(new AtenderConexion(conexion, diaCargado,pool, gestorSalas));
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


}

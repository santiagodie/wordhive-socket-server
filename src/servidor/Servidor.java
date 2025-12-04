/**
 * @author Santiago Die
 */
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
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
		System.out.println("Elige el modo de despliegue del servidor\n1.Web\n2.Consola");
		Scanner s = new Scanner(System.in);
		int puerto;
		String opcion;
		opcion = s.nextLine();
		if ("1".equals(opcion)) {
			puerto = 8080;
		} else {
			puerto = 7777;
		}
		if (diaCargado != null) {
			gestorSalas = new GestorSalas(pool);
			try (ServerSocket serverSocket = new ServerSocket(puerto)) {
				System.out.println("Servidor Palabreto iniciado en "+ puerto);
				System.out.println("Día cargado: " + diaCargado.getId());

				while (true) {
					try {
						Socket conexion = serverSocket.accept();
						System.out.println("Cliente conectado al servidor del PalabReto");
						pool.execute(new AtenderConexion(conexion, diaCargado, pool, gestorSalas));
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

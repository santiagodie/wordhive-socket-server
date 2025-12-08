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
import servidor.web.AtenderHTTP;
import servidor.web.GestorSalasWeb;
import utils.Funcionalidad;
import xml.JAXB.Dia;

public class Servidor {

	private static Dia diaCargado;
	private static GestorSalas gestorSalas;
	private static GestorSalasWeb gestorSalasWeb;

	public static void main(String[] args) {

		int nucleos = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(nucleos);

		diaCargado = Funcionalidad.cargarDiaXml(false);
		System.out.println("Elige el modo de despliegue del servidor\n1.Web\n2.Consola");
		Scanner s = new Scanner(System.in);
		int puerto;
		String opcion = s.nextLine();

		if ("1".equals(opcion)) {
			puerto = 7070;
			gestorSalasWeb = new GestorSalasWeb(pool);
		} else {
			puerto = 7777;
			gestorSalas = new GestorSalas(pool);
		}

		if (diaCargado != null) {
			try (ServerSocket serverSocket = new ServerSocket(puerto)) {
				System.out.println("Servidor Palabreto iniciado en " + puerto);
				System.out.println("Día cargado: " + diaCargado.getId());

				while (true) {
					try {
						Socket conexion = serverSocket.accept();
						if (puerto == 7777) {
							System.out.println("Cliente conectado");
							pool.execute(new AtenderConexion(conexion, diaCargado, pool, gestorSalas));
						} else if (puerto == 7070) {
							pool.execute(new AtenderHTTP(conexion, diaCargado, gestorSalasWeb));
						}
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
		s.close();
	}
}
/**
 * @author Santiago Die
 */
package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import modeloDominio.GestorSalas;
import utils.Funcionalidad;
import xml.JAXB.Dia;
import xml.JAXB.Usuario;

public class AtenderConexion implements Runnable {
	private Socket s;
	private Dia dia;
	private Usuario user;
	private ExecutorService pool;
	private GestorSalas gestorSalas;

	public AtenderConexion(Socket s, Dia serverDia, ExecutorService poolServidor, GestorSalas gestorSalas) {

		this.s = s;
		this.dia = serverDia;
		this.pool = poolServidor;
		this.user = Funcionalidad.buscarUsuario(s.getInetAddress().getHostAddress());
		this.gestorSalas = gestorSalas;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true)) {

			String respuesta;
			pw.println("Bienvenido al Servidor del Palabreto");
			pw.println("Para salir en cualquier momento escribre 'EXIT NOW'");
			while (true) {
				pw.println("Elige la modalidad a jugar");
				pw.println("Escribe 1 o 2");
				pw.println("1.Modalidad normal");
				pw.println("2.Modalidad 1v1");
				respuesta = br.readLine();
				if ("exitCode".equals(respuesta)) {
					pw.println("Gracias por jugar, desconectando...");
					return;
				} else if (respuesta == null || respuesta.isEmpty()) {
					continue;
				} else if ("1".equals(respuesta)) {
					pw.println("Iniciando modo Normal...");
					pool.execute(new AtenderModalidadNormal(s, dia));
					return;
				} else if ("2".equals(respuesta)) {
					pw.println("Preparando modo 1v1...");
					if (!gestorSalas.procesarConexion("<CONNECT_PROTOCOL>", s)) {
						pw.println("Upps! Algo fallo en el emparejamiento...");
						pw.println("Vuelve a intentarlo");
					}
					return;
				} else {
					continue;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
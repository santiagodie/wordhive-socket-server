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
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()))) {

			String respuesta;
			pw.println("Bienvenido al Servidor del Palabreto");
			pw.println("Para salir en cualquier momento escribre 'EXIT NOW'");
			while (true) {
				pw.println("Elige la modalidad a jugar");
				pw.println("Escribe 1 o 2");
				pw.println("1.Modalidad normal");
				pw.println("2.Modalidad 1v1");
				respuesta = br.readLine();
				if (respuesta == null || respuesta.isEmpty()) {
					continue;
				} else if ("1".equals(respuesta)) {
					pw.println("Iniciando modo Normal...");
					pool.execute(new AtenderModalidadNormal(s, dia));
				} else if ("2".equals(respuesta)) {

				} else {
					continue;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
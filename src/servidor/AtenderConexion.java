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
import xml.JAXB.Dia;

public class AtenderConexion implements Runnable {
	private Socket s;
	private Dia dia;
	private ExecutorService pool;
	private GestorSalas gestorSalas;

	public AtenderConexion(Socket s, Dia serverDia, ExecutorService poolServidor, GestorSalas gestorSalas) {

		this.s = s;
		this.dia = serverDia;
		this.pool = poolServidor;
		this.gestorSalas = gestorSalas;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		PrintWriter pw = null;
		boolean delegarSocket = false;
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

			String respuesta;
			pw.println("<CLIENT_LISTEN>");
			pw.println("Bienvenido al Servidor del Palabreto");
			pw.println("Para salir en cualquier momento escribre 'EXIT NOW'");
			while (true) {
				pw.println("<CLIENT_LISTEN>");
				pw.println("Elige la modalidad a jugar");
				pw.println("Escribe 1 o 2");
				pw.println("1.Modalidad normal");
				pw.println("2.Modalidad 1v1");
				pw.println("<CLIENT_TALK>");
				respuesta = br.readLine();
				if ("<CLIENT_EXITCODE>".equals(respuesta)) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("Gracias por jugar, desconectando...");
					break;
				} else if (respuesta == null || respuesta.isEmpty()) {
					continue;
				} else if ("1".equals(respuesta)) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("Iniciando modo Normal...");
					delegarSocket = true;
					pool.execute(new AtenderModalidadNormal(s, dia));
					break;
				} else if ("2".equals(respuesta)) {
					pw.println("<CLIENT_LISTEN>");
					pw.println("Preparando modo 1v1...");
					if (gestorSalas.procesarConexion("<CONNECT_PROTOCOL>", s)) {
						delegarSocket = true;
						break;

					} else {
						pw.println("Upps! Algo fallo en el emparejamiento...");
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (!delegarSocket) {
				try {
					if (br != null)
						br.close();
					if (pw != null)
						pw.close();
					if (s != null && !s.isClosed())
						s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
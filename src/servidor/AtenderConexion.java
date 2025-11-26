/**
 * @author Santiago Die
 */
package servidor;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import utils.Funcionalidad;
import xml.JAXB.Dia;
import xml.JAXB.Usuario;

public class AtenderConexion implements Runnable {
	private Socket s;
	private Dia dia;
	private Usuario user;
	private ExecutorService pool;

	public AtenderConexion(Socket s, Dia serverDia, ExecutorService poolServidor) {

		this.s = s;
		this.dia = serverDia;
		this.pool = poolServidor;
		user = Funcionalidad.buscarUsuario(s.getInetAddress().getHostAddress());
	}

	@Override
	public void run() {
		if (true) {
			pool.execute(new AtenderModalidadNormal(s, dia));
		}
		else if(false) {
			final BlockingQueue<T> cola =
					new ArrayBlockingQueue<T>(2);
		}

	}

}

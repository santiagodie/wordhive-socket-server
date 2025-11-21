package servidor;

import java.io.IOException;
import java.net.Socket;

public class AtenderPeticion extends Thread {
	private Socket s;

	public AtenderPeticion(Socket s) {
		this.s = s;
	}
	@Override
	public void run() {

	}
	
}

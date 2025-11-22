/**
 * @author Santiago Die
 */
package servidor;

import java.io.File;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import xml.JAXB.Dia;

public class AtenderPeticion extends Thread {
	private Socket s;
	private static  Dia dia;

	public AtenderPeticion(Socket s,Dia serverDia) {
		
		this.s = s;
		this.dia = serverDia;
	}
	
	@Override
	public void run() {

	}

	
}

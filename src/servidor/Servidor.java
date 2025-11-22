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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import xml.JAXB.Dia;

public class Servidor {
	
	private static int numDia = (int) (Math.random() * 29) + 1;

	public static void main(String[] args) {
		int nucleos = Runtime.getRuntime().availableProcessors();

		ExecutorService pool = Executors.newFixedThreadPool(nucleos);
		
		Dia dia = deserializarDia();
		
		try (ServerSocket serverSocket = new ServerSocket(7777)) {
			while (true) {
				try {
					Socket conexion = serverSocket.accept();
					System.out.println("Conectado al servidor del PalabReto");
					pool.execute(new AtenderPeticion(conexion,dia));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			pool.shutdown();
		}
	}
	private static void serializarDia(Dia dia) { }
	private static Dia deserializarDia() {	
		try {
            JAXBContext context = JAXBContext.newInstance(Dia.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File fileDia = new File(Paths.get("xml","Dias", "Dia"+numDia+".xml").toString());
            Dia dia = (Dia) unmarshaller.unmarshal(fileDia);           
            return dia;
            
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
	}
}

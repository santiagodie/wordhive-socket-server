/**
 * @author Santiago Die
 */
package utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Deserializador {

	public static <T> T deserializar(File file, Class<T> clazz) throws JAXBException {
		String valorOriginal = System.getProperty("javax.xml.accessExternalDTD");

		try {
			System.setProperty("javax.xml.accessExternalDTD", "all");

			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller um = context.createUnmarshaller();

			@SuppressWarnings("unchecked")
			T objeto = (T) um.unmarshal(file);

			return objeto;

		} finally {
			if (valorOriginal != null) {
				System.setProperty("javax.xml.accessExternalDTD", valorOriginal);
			} else {
				System.clearProperty("javax.xml.accessExternalDTD");
			}
		}
	}
}

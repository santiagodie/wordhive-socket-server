/**
 * @author Santiago Die
 */
package utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Deserializador {

	@SuppressWarnings("unchecked")
	public static <T> T deserializar(File file, Class<T> clazz) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller um = context.createUnmarshaller();
		return (T) um.unmarshal(file);
	}
}

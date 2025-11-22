/**
 * @author Santiago Die
 */
package xml.JAXB;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "contenido", "palabreto" })
public class Palabra {

	@XmlAttribute(name = "long", required = true)
	private int longitud;

	@XmlElement(name = "contenido", required = true)
	private String contenido;

	@XmlElement(required = false)
	private Palabreto palabreto;

	public Palabra() {
	}

	public Palabra(String contenido, int longitud) {
		this.contenido = contenido;
		this.longitud = longitud;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public Palabreto getPalabreto() {
		return palabreto;
	}

	public void setPalabreto(Palabreto palabreto) {
		this.palabreto = palabreto;
	}

	public void marcarComoPalabreto() {
		this.palabreto = new Palabreto();
	}

	public boolean esPalabreto() {
		return this.palabreto != null;
	}
}
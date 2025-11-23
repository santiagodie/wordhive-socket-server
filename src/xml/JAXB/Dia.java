/**
 * @author Santiago Die
 */
package xml.JAXB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "dia")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "letraCentral", "letras", "listaPalabras" })
public class Dia implements Serializable {
	@XmlTransient
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "id", required = true)
	private int id;

	@XmlElement(name = "letraCentral", required = true)
	private String letraCentral;

	@XmlElement(name = "letra", required = true)
	private List<String> letras;

	@XmlElement(name = "palabra", required = true)
	private List<Palabra> listaPalabras;

	public Dia() {
		this.letras = new ArrayList<>();
		this.listaPalabras = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int newId) {
		this.id = newId;
	}

	public String getLetraCentral() {
		return letraCentral;
	}

	public void setLetraCentral(String letraCentral) {
		this.letraCentral = letraCentral;
	}

	public List<String> getLetras() {
		return this.letras;
	}

	public String letrasToString() {
		return String.join("", letras);
	}

	public void setLetras(List<String> newletras) {
		this.letras = newletras;
	}

	public void addLetra(String letra) {
		this.letras.add(letra);
	}

	public List<Palabra> getListaPalabras() {
		return listaPalabras;
	}

	public void setListaPalabras(List<Palabra> newlistaPalabras) {
		this.listaPalabras = newlistaPalabras;
	}

	public void addPalabra(Palabra palabra) {
		this.listaPalabras.add(palabra);
	}

	public Palabra buscarPalabra(String contenido) {
		for (Palabra p : this.listaPalabras) {
			if (p.getContenido().equalsIgnoreCase(contenido)) {
				return p;
			}
		}
		return null;
	}

	public void removePalabra(Palabra palabra) {
		this.listaPalabras.remove(palabra);
	}

}

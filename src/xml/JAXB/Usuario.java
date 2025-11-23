/**
 * @author Santiago Die
 */
package xml.JAXB;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "usuario")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "ip", "puntos", "lista" })
public class Usuario {

	@XmlElement(name = "ip", required = true)
	private String ip;

	@XmlElement(name = "points", required = true)
	private int puntos;

	@XmlElement(name = "palabrasAcertadas")
	private List<Palabra> lista;

	// Constructor vacío (para JAXB)
	private Usuario() {
		this.ip = null;
		this.puntos = 0;
		this.lista = new ArrayList<>();
	}

	// Constructor de nuevo usuario
	public Usuario(String clientIP) {
		this.ip = clientIP;
		this.puntos = 0;
		this.lista = new ArrayList<>();
	}

	// Getters y Setters
	public String getIp() {
		return ip;
	}

	public void setIp(String clientIP) {
		this.ip = clientIP;
	}

	public int getPuntos() {
		return puntos;
	}

	public void setPuntos(int clientPOINTS) {
		this.puntos = clientPOINTS;
	}

	public List<Palabra> getLista() {
		return lista;
	}

	public void setLista(List<Palabra> clientLIST) {
		if (clientLIST != null)
			this.lista = clientLIST;
		else
			this.lista = new ArrayList<>();
	}

	public void agregarItem(Palabra item) {
		this.lista.add(item);
	}

	public void limpiarLista() {
		this.lista.clear();
	}
	public boolean buscarPalabra(String contenido) {
		for (Palabra p : this.lista) {
			if (p.getContenido().equalsIgnoreCase(contenido)) {
				return true;
			}
		}
		return false;
	}
}
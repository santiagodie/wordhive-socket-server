/**
 * @author Santiago Die
 */
package xml.JAXB;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "usuario")
@XmlType(propOrder = {"ip", "points", "lista"})
public class Usuario {

	@XmlAttribute(name = "id", required = true)
	private int id;
	
	@XmlElement(name = "ip", required = true)
	private String ip;
	
	@XmlElement(name = "points", required = true)
	private int puntos;
	
	@XmlElement(name = "item")
	@XmlElementWrapper(name = "lista", required = true)
	private List<String> lista;

	// Constructor vacío (para JAXB)
    private Usuario() {
        this.id = 0;
        this.ip = null;
        this.puntos = 0;
        this.lista = new ArrayList<>();
    }

	// Constructor de nuevo usuario
	public Usuario(int clientID, String clientIP) {
		this.id = clientID;
		this.ip = clientIP;
		this.puntos = 0;
		this.lista = new ArrayList<>();
	}

	// Constructor de usuario ya existente en el XML
	public Usuario(int clientID, String clientIP, int clientPOINTS, List<String> clientLIST) {
		this.id = clientID;
		this.ip = clientIP;
		this.puntos = clientPOINTS;
		if (clientLIST != null)
			this.lista = clientLIST;
		else
			this.lista = new ArrayList<>();
	}
	// Getters y Setters
	public String getIp() {
		return ip;
	}

	public void setIp(String clientIP) {
		this.ip = clientIP;
	}

	public int getId() {
		return id;
	}

	public int getPuntos() {
		return puntos;
	}

	public void setPuntos(int clientPOINTS) {
		this.puntos = clientPOINTS;
	}

	public List<String> getLista() {
		return lista;
	}

	public void setLista(List<String> clientLIST) {
		if (clientLIST != null)
			this.lista = clientLIST;
		else
			this.lista = new ArrayList<>();
	}
    public void agregarItem(String item) {
        this.lista.add(item);
    }
    @Override
    public String toString() {
        return "Usuario{id=" + id + ", ip=" + ip + ", puntos=" + puntos + "}";
    }
}
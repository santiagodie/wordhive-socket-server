/**
 * @author Santiago Die
 */
package modeloDominio;

import java.util.ArrayList;
import java.util.List;

import xml.JAXB.Palabra;

public class Usuario {

	private String idUsuario;
	private int puntos;
	private List<Palabra> lista;

	public Usuario(String clientIP) {
		this.idUsuario = clientIP;
		this.puntos = 0;
		this.lista = new ArrayList<>();
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String clientIP) {
		this.idUsuario = clientIP;
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

	public void setLista(List<Palabra> lista) {
		if (lista != null) {
			this.lista = lista;
		} else {
			this.lista = new ArrayList<>();
		}
	}

	public void agregarPalabra(Palabra palabra) {
		this.lista.add(palabra);
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
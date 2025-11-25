package modeloDominio;

import xml.JAXB.Dia;
import xml.JAXB.Usuario;

public class Partida {

	private Usuario jugador1;
	private Usuario jugador2;
	private Dia dia;

	public Partida(Usuario user1, Usuario user2, Dia diaReto) {

		this.jugador1 = user1;
		this.jugador2 = user2;
		this.dia = diaReto;
	}

}

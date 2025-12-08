/**
 * @author Santiago Die
 */
package servidor.web;

import modeloDominio.Usuario;
import xml.JAXB.Dia;
 
 enum Estados {
	ESPERANDO_RIVAL, CUENTA_REGRESIVA, JUGANDO, FINALIZADA
}
public class PartidaWeb {  
    private String partidaId;
    private Dia dia;
    private String sessionIdJ1;
    private String sessionIdJ2;
    private Usuario usuario1;
    private Usuario usuario2;
    private int puntosJ1 = 0;
    private int puntosJ2 = 0;
    private Estados estado = Estados.ESPERANDO_RIVAL;

    public PartidaWeb(String partidaId, Dia dia) {
        this.partidaId = partidaId;
        this.dia = dia;
    }

    public synchronized int agregarJugador(String sessionId) {
        if (sessionIdJ1 == null) {
            sessionIdJ1 = sessionId;
            usuario1 = new Usuario(sessionId);
            return 1;
        } else if (sessionIdJ2 == null) {
            sessionIdJ2 = sessionId;
            usuario2 = new Usuario(sessionId);
            estado = Estados.JUGANDO;
            return 2;
        }
        return -1;
    }

    public synchronized void actualizarPuntos(String sessionId, int puntos) {
        if (sessionId.equals(sessionIdJ1)) {
            puntosJ1 = puntos;
        } else if (sessionId.equals(sessionIdJ2)) {
            puntosJ2 = puntos;
        }
    }

    public synchronized void finalizarPartida() {
        estado = Estados.FINALIZADA;
    }

    public synchronized void rendirse(String sessionId) {
        if (sessionId.equals(sessionIdJ1)) {
            puntosJ1 = -1;
        } else if (sessionId.equals(sessionIdJ2)) {
            puntosJ2 = -1;
        }
        estado = Estados.FINALIZADA;
    }

    public synchronized String getResultado(String sessionId) {
    	int misPuntos, puntosRival;
    	if (sessionId.equals(sessionIdJ1)) {
    	    misPuntos = puntosJ1;
    	    puntosRival = puntosJ2;
    	} else {
    	    misPuntos = puntosJ2;
    	    puntosRival = puntosJ1;
    	}
        if (puntosRival == -1) return "rival abandono";
        if (misPuntos > puntosRival) return "ganaste";
        if (misPuntos < puntosRival) return "perdiste";
        return "empate";
    }

    public String getPartidaId() {
        return partidaId;
    }

    public Dia getDia() {
        return dia;
    }

    public synchronized Estados getEstado() {
        return estado;
    }

    public synchronized Usuario getUsuario(String sessionId) {
        if (sessionId.equals(sessionIdJ1)) return usuario1;
        if (sessionId.equals(sessionIdJ2)) return usuario2;
        return null;
    }

    public synchronized int getPuntosJ1() {
        return puntosJ1;
    }

    public synchronized int getPuntosJ2() {
        return puntosJ2;
    }

    public synchronized String getSessionIdJ1() {
        return sessionIdJ1;
    }

    public synchronized String getSessionIdJ2() {
        return sessionIdJ2;
    }

    public synchronized boolean estaLlena() {
        return sessionIdJ1 != null && sessionIdJ2 != null;
    }

    public synchronized boolean contieneJugador(String sessionId) {
        return sessionId.equals(sessionIdJ1) || sessionId.equals(sessionIdJ2);
    }

    public synchronized int getNumeroJugador(String sessionId) {
        if (sessionId.equals(sessionIdJ1)) return 1;
        if (sessionId.equals(sessionIdJ2)) return 2;
        return -1;
    }
}
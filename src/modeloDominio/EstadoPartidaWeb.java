/**
 * @author Santiago Die
 */
package modeloDominio;

public class EstadoPartidaWeb {
    public final String estado;
    public final int misPuntos;
    public final int puntosRival;
    public final String resultado;
    public final int numeroJugador;

    public EstadoPartidaWeb(String estado, int misPuntos, int puntosRival, 
                            String resultado, int numeroJugador) {
        this.estado = estado;
        this.misPuntos = misPuntos;
        this.puntosRival = puntosRival;
        this.resultado = resultado;
        this.numeroJugador = numeroJugador;
    }

    public String toJSON() {
        return String.format(
            "{\"estado\":\"%s\",\"misPuntos\":%d,\"puntosRival\":%d," +
            "\"resultado\":\"%s\",\"numeroJugador\":%d}",
            estado, misPuntos, puntosRival, resultado, numeroJugador
        );
    }
}

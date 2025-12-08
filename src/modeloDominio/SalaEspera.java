/**
 * @author Santiago Die
 */
package modeloDominio;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SalaEspera {
    private final BlockingQueue<Socket> cola;
    private final String nombre;

    public SalaEspera(String name) {
        this.nombre = name;
        this.cola = new LinkedBlockingQueue<>(2); 
    }

    public boolean añadirJugador(Socket s) throws InterruptedException {
        boolean añadido = cola.offer(s); 
        if (añadido) {
            System.out.println("Jugador añadido a " + nombre);
        }
        return añadido;
    }

    public Socket[] esperarJugadores() throws InterruptedException {
        Socket[] pareja = new Socket[2];
        pareja[0] = cola.take();
        pareja[1] = cola.take();
        return pareja;
    }

    public int getJugadoresEsperando() {
        return cola.size();
    }

    public String getNombre() {
        return nombre;
    }

    public boolean salaLlena() {
        return cola.size() >= 2;
    }
}
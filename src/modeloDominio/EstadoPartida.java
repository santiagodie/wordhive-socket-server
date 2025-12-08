/**
 * @author Santiago Die
 */
package modeloDominio;

import java.util.concurrent.CountDownLatch;

public class EstadoPartida {
    private final CountDownLatch finalizacion = new CountDownLatch(2);
    private volatile boolean juegoActivo = true;
    private int puntosJ1 = 0;
    private int puntosJ2 = 0;
    private final Object lockPuntos = new Object();

    public boolean isJuegoActivo() {
        return juegoActivo;
    }

    public void finalizarJuego() {
        juegoActivo = false;
    }

    public synchronized void setPuntos(boolean esJugador1, int puntos) {
        synchronized (lockPuntos) {
            if (esJugador1) {
                puntosJ1 = puntos;
            } else {
                puntosJ2 = puntos;
            }
        }
    }

    public synchronized int getPuntos(boolean esJugador1) {
        synchronized (lockPuntos) {
            return esJugador1 ? puntosJ1 : puntosJ2;
        }
    }

    public synchronized int getPuntosRival(boolean esJugador1) {
        synchronized (lockPuntos) {
            return esJugador1 ? puntosJ2 : puntosJ1;
        }
    }

    public void jugadorTerminado() {
        finalizacion.countDown();
    }

    public void esperarFinalizacion() throws InterruptedException {
        finalizacion.await();
    }
}

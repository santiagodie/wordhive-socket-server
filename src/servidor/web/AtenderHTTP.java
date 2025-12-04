package servidor.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import modeloDominio.Usuario;
import xml.JAXB.Dia;

public class AtenderHTTP implements Runnable {

	private Socket socket;
	private Dia dia;
	private Usuario usuario;

	public AtenderHTTP(Socket socket, Dia dia) {
		this.socket = socket;
		this.dia = dia;
		this.usuario = new Usuario(socket.getInetAddress().getHostAddress());
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));                                                         
		        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
}

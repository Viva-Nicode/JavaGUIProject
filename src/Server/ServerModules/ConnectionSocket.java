package Server.ServerModules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import Server.Util.RequestHandler;

public class ConnectionSocket implements Runnable {

	private BufferedWriter out = null;
	private BufferedReader in = null;
	private Socket socket = null;

	public ConnectionSocket(Socket s) throws IOException {
		this.socket = s;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void run() {
		String msg = "";
		while (true) {
			try {
				while (true) {
					if ((msg = in.readLine()) != null) {
						System.out.println(msg + "\n");
						break;
					}
				}
				out.write(RequestHandler.requestHandler(socket, msg) + "\n");
				out.flush();

			} catch (IOException e) {
				System.out.println(socket.getLocalAddress());
				break;
			}
		}
	}
}

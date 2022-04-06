package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientSocket {
  private BufferedReader in = null;
  private BufferedWriter out = null;
  private Socket socket = null;

  public ClientSocket(String ipAddress, int port) throws IOException {

    socket = new Socket(ipAddress, port);

    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  public void sender(String request) {

    try {
      out.write(request + "\n");
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int numberReceiver() {
    String response = "";
    try {
      while (true) {
        if ((response = in.readLine()) != null)
          break;
      }
      return Integer.parseInt(response);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Integer.parseInt(response);
  }
  
  public String stringReceiver() {
    String response = "";
    try {
      while (true) {
        if ((response = in.readLine()) != null)
          break;
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response;
  }

  public void desconnect() {
    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

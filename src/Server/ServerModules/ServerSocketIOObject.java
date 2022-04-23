package Server.ServerModules;

import Server.Util.RequestHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerSocketIOObject implements Runnable {

  private BufferedReader bufferedReader = null;
  private BufferedWriter bufferedWriter = null;

  private Socket socket = null;

  public ServerSocketIOObject(Socket c) throws IOException {

    this.socket = c;
    bufferedReader =
        new BufferedReader(new InputStreamReader(socket.getInputStream()));
    bufferedWriter =
        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  @Override
  public void run() {
    while (true) {
      try {
        String requestLine = "";
        System.out.println("Request Waiting ...");
        requestLine = bufferedReader.readLine();
        System.out.println("Request Received : " + requestLine);
        bufferedWriter.write(
            RequestHandler.requestHandler(socket, requestLine) + "\r\n");
        bufferedWriter.flush();
      } catch (IOException | NullPointerException e) {
        System.out.println("Maybe msg is null because upload was performed.");
        break;
      }
    }
  }
}
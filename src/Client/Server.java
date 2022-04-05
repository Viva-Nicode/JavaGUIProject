package src.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import src.Server.User.UserDTO;
import src.Server.User.UserDAO;

public class Server {

  private BufferedReader in = null;
  private BufferedWriter out = null;
  private ServerSocket listener = null;
  private Socket socket = null;
  private Receiver receiver;

  public Server() {
    try {
      connection();
      Thread th = new Thread(receiver);
      th.start();
      th.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void connection()
      throws IOException { // 소켓 메모리에 올리고 연결대기 상태 만듦

    listener = new ServerSocket(12345);
    System.out.println("waiting connect...");
    socket = listener.accept();
    System.out.println("any client Success to connection!");
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  class Receiver implements Runnable {

    @Override
    public void run() {
      String msg = "";
      try {
        while ((msg = in.readLine()) != null) {
          if (msg.equalsIgnoreCase("exit")) {
            listener.close();
            socket.close();
            break;
          }
          System.out.println("\n" + msg);
          out.write(queryexcute(msg) + "\n");
          out.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /* usersignup argu1 argu2 */
  public int queryexcute(final String request) {
    String[] s = request.split(" ");

    try {
      if (s[0].equals("usersignup"))
        return UserDAO.userSignup(new UserDTO(s[1]), s[2]);
      else if (s[0].equals("userlogin"))
        return UserDAO.userLogin(new UserDTO(s[1]), s[2]);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static void main(String[] args) { new Server(); }
}

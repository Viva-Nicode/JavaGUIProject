package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolServer {

  private final static int THREAD_CNT = 3;
  private static ExecutorService threadpool =
      Executors.newFixedThreadPool(THREAD_CNT);
  private final static int PORT = 13243;

  public static void main(String[] args) throws IOException {
    try (ServerSocket listener = new ServerSocket(PORT)) {
      while (true) {
        System.out.println("Waiting Connection...");
        Socket s = listener.accept();
        System.out.println("pid : " + ProcessHandle.current().pid());
        threadpool.execute(new ServerSocketIOObject(s));
        System.out.println(s.getLocalSocketAddress() + " is Connect");
      }
    }
  }
}

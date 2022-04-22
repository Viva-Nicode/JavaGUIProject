package Client;

import Server.Util.TaskNumbers;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class ClientSocketIOObject {
  private BufferedReader br = null;
  private BufferedWriter bw = null;
  private BufferedInputStream bis = null;
  private BufferedOutputStream bos = null;
  private Socket socket = null;
  private final int DEFALUT_BUFFER_SIZE = 4096;
  private byte[] buffer;

  public ClientSocketIOObject(String ipAddress, int port) throws IOException {

    socket = new Socket(ipAddress, port);
    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  public void sender(final String jsonRequest) {

    try {

      bw.write(jsonRequest + "\r\n");
      bw.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String Receiver() throws ClassNotFoundException, IOException {
    return br.readLine();
  }

  public String binaryStreamSender(final File file)
      throws FileNotFoundException, NoSuchAlgorithmException {

    String s = "";
    try {
      buffer = new byte[DEFALUT_BUFFER_SIZE];
      bis = new BufferedInputStream(new FileInputStream(file),
                                    DEFALUT_BUFFER_SIZE);
      bos = new BufferedOutputStream(socket.getOutputStream());

      while (bis.read(buffer) > 0) {
        s = s + buffer;
        bos.write(buffer);
        bos.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return TaskNumbers.sha256(s);
  }

  public void binaryStreamReceiver() {}

  public void desconnect() {
    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

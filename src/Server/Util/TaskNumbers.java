package Server.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

interface TaskNumberable {
  public final static int _NOT_EXIST_FILELIST = -2;
  public final static int _REQUEST_NOT_PROCESSED_PROPERLY = -1;
  public final static int _REQUEST_SUCCESSFULLY_PROCESSED = 0;
  public final static int _USER_LOGIN_REQUEST = 1;
  public final static int _USER_SIGNUP_REQUEST = 2;
  public final static int _FILELIST_REQUEST = 3;
  public final static int _DESCONNECT_REQUEST = 4;
  public final static int _FILEUPLOAD_REQUEST = 5;
  public final static int _FILEDOWNLOAD_REQUEST = 6;
}

public class TaskNumbers implements TaskNumberable {

  public static String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes)
      builder.append(String.format("%02x", b));
    return builder.toString();
  }

  public static String sha256(String msg) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(msg.getBytes());
    return bytesToHex(md.digest());
  }

  public static String getnow() {
    LocalDate nowdate = LocalDate.now();
    DateTimeFormatter formatterdate = DateTimeFormatter.ofPattern("yyyyMMdd");
    String formateddate = nowdate.format(formatterdate);

    LocalTime nowtime = LocalTime.now();
    DateTimeFormatter formattertime = DateTimeFormatter.ofPattern("HHmmss");
    String formatedtime = nowtime.format(formattertime);

    return formateddate + formatedtime;
  }
}

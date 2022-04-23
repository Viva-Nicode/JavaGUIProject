package Server.File;

import Server.Util.DatabaseUtil;
import Server.Util.TaskNumbers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileDAO {

  public static String getFilelist(final String user_id) throws SQLException {

    ResultSet rs = null;
    Connection connection = DatabaseUtil.getConnection();

    String get_user_filelist_Query =
        "select file_name, file_extention, file_bytesize, upload_date, file_comment from filemetadata where user_id = ?";

    PreparedStatement preforcheckedoverlap =
        connection.prepareStatement(get_user_filelist_Query);

    preforcheckedoverlap.setString(1, user_id);
    rs = preforcheckedoverlap.executeQuery();

    String FileDTOJsonArray = "";
    while (true) {
      if (rs.next()) {
        FileDTOJsonArray +=
            new FileDTO(rs.getString(1), rs.getString(2), rs.getLong(3),
                        rs.getString(4), rs.getString(5)) +
            ", ";

      } else {
        break;
      }
    }
    if (FileDTOJsonArray.length() == 0)
      return "{\"responseType\":" + TaskNumbers._NOT_EXIST_FILELIST + "}";

    return "{\"responseType\":" + TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED +
        ", \"filelist\":[" +
        FileDTOJsonArray.substring(0, FileDTOJsonArray.length() - 2) + "]}";
  }

  public static int insertFileMetaData(final FileDTO t) throws SQLException {

    String insert_FileMetadata_Query =
        "INSERT INTO filemetadata VALUES (?, ?, ?, ?, ?, ?, ?)";

    Connection connection = DatabaseUtil.getConnection();

    PreparedStatement pre =
        connection.prepareStatement(insert_FileMetadata_Query);

    pre.setString(1, t.getFile_name());
    pre.setString(2, t.getUser_id());
    pre.setString(3, t.getFile_extention());
    pre.setLong(4, t.getFile_size());
    pre.setString(5, t.getUpload_date());
    pre.setString(6, t.getFile_comment());
    pre.setString(7, null);

    return pre.executeUpdate();
  }

  public static String insertuploadedfile(final Socket s,
                                          final String filemetadatas)
      throws IOException, InterruptedException, SQLException {

    Thread t = new Thread(new fileIOObject(s, filemetadatas));
    System.out.println("start file Input thread");
    t.start();
    t.join();
    System.out.println("finish file Input thread");
    JsonObject jo = JsonParser.parseString(filemetadatas).getAsJsonObject();

    return getFilelist(jo.get("user_id").getAsString());
  }
}

class fileIOObject implements Runnable {

  private BufferedInputStream bis = null;
  private BufferedOutputStream bos = null;
  private long readupnum;
  private byte[] buffer = new byte[4096];
  private BufferedWriter bufferedWriter;
  private JsonObject jo;
  private FileDTO fd;

  fileIOObject(Socket c, String filemetadata) throws IOException {
    jo = JsonParser.parseString(filemetadata).getAsJsonObject();
    fd = new FileDTO(jo.get("file_name").getAsString(),
                     jo.get("user_id").getAsString(),
                     jo.get("file_extention").getAsString(),
                     jo.get("file_bytesize").getAsLong(),
                     jo.get("file_comment").getAsString());
    readupnum = fd.getFile_size() / 4096 + 1;
    bufferedWriter =
        new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
    bis = new BufferedInputStream(c.getInputStream(), 4096);
  }
  @Override
  public void run() {
    System.out.println("run start");
    try {
      File file = new File("/Users/nicode./MainSpace/testdir/" +
                           fd.getFile_name() + "." + fd.getFile_extention());
      file.createNewFile();
      bos = new BufferedOutputStream(new FileOutputStream(file), 4096);
      byte[] allbytedata = new byte[(int)readupnum * 4096];
      System.out.println("readupnum : " + readupnum);

      bufferedWriter.write(
          "{\"responseType\":" + TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED +
          "}\r\n");
      bufferedWriter.flush();

      System.out.println("allbytedata len : " + allbytedata.length);

      for (int idx = 0; idx < readupnum; idx++) {
        bis.read(buffer);
        bos.write(buffer);
      }

      /* String hashcode = TaskNumbers.sha256(alldata); */

      String insert_blob_data_query =
          "INSERT INTO uploadedfile VALUES (?, ?, ?)";

      Connection connection = DatabaseUtil.getConnection();
      PreparedStatement pre =
          connection.prepareStatement(insert_blob_data_query);

      pre.setString(1, fd.getFile_name());
      pre.setString(2, fd.getUser_id());
      pre.setString(3, file.getPath());

      if (pre.executeUpdate() == 1) {
        fd.setuploaddate(TaskNumbers.getnow());
        FileDAO.insertFileMetaData(fd);
      }
    } catch (IOException | SQLException /* | NoSuchAlgorithmException */ e) {
      e.printStackTrace();
    }
  }
}

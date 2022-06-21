package Server;

import Server.Util.DatabaseUtil;
import Common.FileDTO;
import Common.TaskNumbers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class FileDAO {
  private static final int __BUFFER_BYTESIZE = 4096;

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
      throws IOException, InterruptedException, SQLException,
             FileNotFoundException {

    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    long readupnum;
    byte[] buffer = new byte[__BUFFER_BYTESIZE];
    byte[] please;
    BufferedWriter bufferedWriter;
    JsonObject jo;
    FileDTO fd;

    jo = JsonParser.parseString(filemetadatas).getAsJsonObject();
    fd = new FileDTO(jo.get("file_name").getAsString(),
                     jo.get("user_id").getAsString(),
                     jo.get("file_extention").getAsString(),
                     jo.get("file_bytesize").getAsLong(),
                     jo.get("file_comment").getAsString());
    if (fd.getFile_size() % __BUFFER_BYTESIZE == 0)
      readupnum = fd.getFile_size() / __BUFFER_BYTESIZE;
    else
      readupnum = fd.getFile_size() / __BUFFER_BYTESIZE + 1;

    bufferedWriter =
        new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    bis = new BufferedInputStream(s.getInputStream(), __BUFFER_BYTESIZE);
    try {
      File file =
          new File("/Users/nicode./MainSpace/uploadDir/" + fd.getUser_id() +
                   "/" + fd.getFile_name() + "." + fd.getFile_extention());
      file.createNewFile();
      bos = new BufferedOutputStream(new FileOutputStream(file),
                                     __BUFFER_BYTESIZE);
      please = new byte[(int)(__BUFFER_BYTESIZE * readupnum)];

      bufferedWriter.write(
          "{\"responseType\":" + TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED +
          "}\r\n");
      bufferedWriter.flush();

      for (int idx = 0; idx < readupnum; idx++) {
        bis.read(buffer);
        System.arraycopy(buffer, 0, please, idx * __BUFFER_BYTESIZE,
                         __BUFFER_BYTESIZE);
      }
      System.out.println("readupnum : " + readupnum);
      bos.write(Arrays.copyOfRange(please, 0, (int)fd.getFile_size()));

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

    return getFilelist(jo.get("user_id").getAsString());
  }

  public static String downloadRequestProcessor(final Socket s,
                                                final String request)
      throws SQLException, FileNotFoundException, IOException {
    JsonObject jo = JsonParser.parseString(request).getAsJsonObject();

    String getfilePathquery =
        "select file_path from uploadedfile where user_id = ? && file_name = ?";

    Connection connection = DatabaseUtil.getConnection();
    PreparedStatement pre = connection.prepareStatement(getfilePathquery);

    pre.setString(1, jo.get("user_id").getAsString());
    pre.setString(2, jo.get("file_name").getAsString());

    ResultSet rs = pre.executeQuery();
    File f;
    if (rs.next()) {
      f = new File(rs.getString(1));

      BufferedInputStream bis =
          new BufferedInputStream(new FileInputStream(f), __BUFFER_BYTESIZE);
      BufferedOutputStream bos =
          new BufferedOutputStream(s.getOutputStream(), __BUFFER_BYTESIZE);
      byte[] buffer = new byte[__BUFFER_BYTESIZE];
      while (bis.read(buffer) > 0) {
        bos.write(buffer);
        bos.flush();
      }
      bis.close();

      return "{\"responseType\":" +
          TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED + "}";
    }
    return "{\"responseType\":" + TaskNumbers._REQUEST_NOT_PROCESSED_PROPERLY +
        "}";
  }

  public static String modifyFileInfo(final Socket c,
                                      final String modifyRequest)
      throws SQLException {
    JsonObject jo = JsonParser.parseString(modifyRequest).getAsJsonObject();

    String modifyFileInfoQuery =
        "update uploadedfile set file_name = ? where user_id = ? && file_name = ?";

    Connection connection = DatabaseUtil.getConnection();
    PreparedStatement pre = connection.prepareStatement(modifyFileInfoQuery);

    pre.setString(1, jo.get("new_filename").getAsString());
    pre.setString(2, jo.get("user_id").getAsString());
    pre.setString(3, jo.get("file_name").getAsString());

    pre.executeUpdate();

    modifyFileInfoQuery =
        "update filemetadata set file_comment = ? where user_id = ? && file_name = ?";

    pre = connection.prepareStatement(modifyFileInfoQuery);

    pre.setString(1, jo.get("new_fileComment").getAsString());
    pre.setString(2, jo.get("user_id").getAsString());
    pre.setString(3, jo.get("new_filename").getAsString());

    pre.executeUpdate();

    return "{\"responseType\":" + TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED +
        "}";
  }

  public static String deleteFile(final Socket s, final String deleteRequest)
      throws SQLException {

    JsonObject jo = JsonParser.parseString(deleteRequest).getAsJsonObject();

    String getfilePathquery =
        "select file_path from uploadedfile where user_id = ? && file_name = ?";

    Connection connection = DatabaseUtil.getConnection();
    PreparedStatement pre = connection.prepareStatement(getfilePathquery);

    pre.setString(1, jo.get("user_id").getAsString());
    pre.setString(2, jo.get("file_name").getAsString());

    ResultSet rs = pre.executeQuery();
    rs.next();
    File f = new File(rs.getString(1));
    f.delete();

    String fileDeletQuery = "delete from uploadedfile where file_path = ?";

    pre = connection.prepareStatement(fileDeletQuery);
    pre.setString(1, f.getPath());
    pre.executeUpdate();

    return "{\"responseType\":" + TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED +
        "}";
  }
}

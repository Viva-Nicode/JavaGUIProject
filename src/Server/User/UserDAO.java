package Server.User;

import Client.SendReceiveSerializationObject;
import Server.Util.DatabaseUtil;
import Server.Util.EncryptUtil;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/* Love ain't a science Don't need no license 머리 싸매고 고민 할수록 minus */

public class UserDAO {

  private static String iddd;
  private static String ciphertext;

  private static boolean isOverlapUserid(final String user_id)
      throws Exception {

    ResultSet rs = null;
    Connection connection = DatabaseUtil.getConnection();

    String checked_UserID_Overlap_Query =
        "SELECT * FROM user_info WHERE user_id = ?";

    PreparedStatement preforcheckedoverlap =
        connection.prepareStatement(checked_UserID_Overlap_Query);

    preforcheckedoverlap.setString(1, user_id);

    rs = preforcheckedoverlap.executeQuery();

    if (rs.next()) { /* 결과가 존재한다면 즉 중복된 id가 존재한다면 */
      iddd = rs.getString(1);
      ciphertext = rs.getString(2);
      return true;
    } else
      return false;
  }

  public static SendReceiveSerializationObject userSignup(final UserDTO user)
      throws SQLException, UnsupportedEncodingException, Exception {

    EncryptUtil e = new EncryptUtil();

    String insert_User_Info_Query = "INSERT INTO user_info VALUES (?, ?)";
    String insert_User_key_Query = "INSERT INTO user_key VALUES (?, ?)";

    Connection connection = DatabaseUtil.getConnection();

    if (isOverlapUserid(user.getUserID())) {
      return new SendReceiveSerializationObject( // id is overlap
          SendReceiveSerializationObject._REQUEST_NOT_PROCESSED_PROPERLY);
    } else {
      PreparedStatement pre =
          connection.prepareStatement(insert_User_Info_Query);

      pre.setString(1, user.getUserID());
      pre.setString(
          2, new String(e.encryptsion(user.getplainPasswordText()), "UTF-8"));

      if (pre.executeUpdate() == 1) {
        pre = connection.prepareStatement(insert_User_key_Query);

        pre.setString(1, user.getUserID());
        pre.setString(2, e.getiv());
      }
      // 쿼리가 정상 처리 되었다면(정상 회원가입되었다면) 1 반환
      if (pre.executeUpdate() == 1) {
        return new SendReceiveSerializationObject(
            SendReceiveSerializationObject._REQUEST_SUCCESSFULLY_PROCESSED);
      }
    }
    return new SendReceiveSerializationObject(
        SendReceiveSerializationObject._REQUEST_NOT_PROCESSED_PROPERLY);
  }

  public static SendReceiveSerializationObject userLogin(final UserDTO user)
      throws SQLException, UnsupportedEncodingException, Exception {

    if (isOverlapUserid(user.getUserID())) {

      ResultSet rs = null;

      String insert_User_Info_Query =
          "SELECT user_key FROM user_key WHERE user_id = ?";

      Connection connection = DatabaseUtil.getConnection();
      PreparedStatement preforcheckedoverlap =
          connection.prepareStatement(insert_User_Info_Query);

      preforcheckedoverlap.setString(1, iddd);

      rs = preforcheckedoverlap.executeQuery();
      rs.next();
      EncryptUtil e = new EncryptUtil(rs.getString(1));

      if (new String(e.encryptsion(user.getplainPasswordText()), "UTF-8")
              .equals(ciphertext)) {
        /* 아디 비번 정상 존재하다면 0 반환 */
        return new SendReceiveSerializationObject(
            SendReceiveSerializationObject._REQUEST_SUCCESSFULLY_PROCESSED);
      } else {
        /* 아이디 있지만 비번 없으면 -1 반환 */
        return new SendReceiveSerializationObject(
            SendReceiveSerializationObject._REQUEST_NOT_PROCESSED_PROPERLY);
      }
    } else {
      /* 아이디 조차 없으면 -2 반환 */
      return new SendReceiveSerializationObject(
          SendReceiveSerializationObject._REQUEST_NOT_PROCESSED_PROPERLY);
    }
  }
}

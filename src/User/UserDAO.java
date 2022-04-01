package src.User;

import java.sql.Connection;
import java.sql.PreparedStatement;

import src.Util.DatabaseUtil;

/* Love ain't a science Don't need no license 머리 싸매고 고민 할수록 minus */
public class UserDAO {

  public static int userSignup(UserDTO user) throws Exception {
    String sql = "INSERT INTO user_info VALUES (?, ?, ?)";
    Connection connection = DatabaseUtil.getConnection();
    PreparedStatement pre = connection.prepareStatement(sql);

    pre.setString(1, user.getUserID());
    /* pre.setString(2, encryption(user.getCiphertext(), user.getSaltValue())); */
    // 쿼리가 정상적으로 처리 되었다면 1 반환
    return pre.executeUpdate();
  }


}

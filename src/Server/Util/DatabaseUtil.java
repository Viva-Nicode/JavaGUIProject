package Server.Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil {
  public static Connection getConnection() {
    try {
      String dbURL = "jdbc:mysql://localhost:3306/JavaBasicCloudProject";
      String dbID = "root";
      String dbPW = "asd238tr";
      Class.forName("com.mysql.cj.jdbc.Driver");
      return DriverManager.getConnection(dbURL, dbID, dbPW);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}



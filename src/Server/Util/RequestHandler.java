package Server.Util;

import Server.File.FileDAO;
import Server.User.UserDAO;
import Server.User.UserDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.Socket;

public class RequestHandler {

  public static String requestHandler(final Socket so, final String jsondata) {
    JsonObject jo = JsonParser.parseString(jsondata).getAsJsonObject();
    int RquestTypeNum = jo.get("requestType").getAsInt();
    try {
      if (RquestTypeNum == TaskNumberable._USER_SIGNUP_REQUEST) {
        System.out.println("requestHandler");
        return UserDAO.userSignup(new UserDTO(jo.get("user_id").getAsString(),
                                              jo.get("user_pw").getAsString(),
                                              jo.get("user_em").getAsString()));
      } else if (RquestTypeNum == TaskNumberable._USER_LOGIN_REQUEST) {
        return UserDAO.userLogin(new UserDTO(jo.get("user_id").getAsString(),
                                             jo.get("user_pw").getAsString()));
      } else if (RquestTypeNum == TaskNumberable._DESCONNECT_REQUEST) {
        if (so.isConnected())
          so.close();
        return "{\"responseType\":" +
            TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED + "}";
      } else if (RquestTypeNum == TaskNumberable._FILELIST_REQUEST) {
        return FileDAO.getFilelist(jo.get("user_id").getAsString());
      } else if (RquestTypeNum == TaskNumberable._FILEUPLOAD_REQUEST) {
        return FileDAO.insertuploadedfile(so, jsondata);
      } else if (RquestTypeNum == TaskNumberable._FILEDOWNLOAD_REQUEST) {
		  return FileDAO.downloadRequestProcessor(so, jsondata);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
/* 각종 DAO들안에 구현된 static 함수들은 직렬화 클래스를 반환해야함 */

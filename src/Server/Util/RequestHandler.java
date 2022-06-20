package Server.Util;

import Common.TaskNumbers;
import Server.FileDAO;
import Server.UserDAO;
import Common.UserDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.Socket;

public class RequestHandler {

  public static String requestHandler(final Socket so, final String jsondata) {
    JsonObject jo = JsonParser.parseString(jsondata).getAsJsonObject();
    int RquestTypeNum = jo.get("requestType").getAsInt();
    try {
      if (RquestTypeNum == TaskNumbers._USER_SIGNUP_REQUEST) {
        System.out.println("requestHandler");
        return UserDAO.userSignup(new UserDTO(jo.get("user_id").getAsString(),
                                              jo.get("user_pw").getAsString(),
                                              jo.get("user_em").getAsString()));
      } else if (RquestTypeNum == TaskNumbers._USER_LOGIN_REQUEST) {
        return UserDAO.userLogin(new UserDTO(jo.get("user_id").getAsString(),
                                             jo.get("user_pw").getAsString()));
      } else if (RquestTypeNum == TaskNumbers._DESCONNECT_REQUEST) {
        if (so.isConnected())
          so.close();
        return "{\"responseType\":" +
            TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED + "}";
      } else if (RquestTypeNum == TaskNumbers._FILELIST_REQUEST) {
        return FileDAO.getFilelist(jo.get("user_id").getAsString());
      } else if (RquestTypeNum == TaskNumbers._FILEUPLOAD_REQUEST) {
        return FileDAO.insertuploadedfile(so, jsondata);
      } else if (RquestTypeNum == TaskNumbers._FILEDOWNLOAD_REQUEST) {
        return FileDAO.downloadRequestProcessor(so, jsondata);
      } else if (RquestTypeNum == TaskNumbers._FILEDELETE_REQUEST) {
        return FileDAO.deleteFile(so, jsondata);
      } else if (RquestTypeNum ==
                 TaskNumbers._FILEINFO_MODIFICATION_REQUEST) {
        return FileDAO.modifyFileInfo(so, jsondata);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

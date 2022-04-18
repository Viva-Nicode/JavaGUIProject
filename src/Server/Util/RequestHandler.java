package Server.Util;

import Client.SendReceiveSerializationObject;
import Server.File.FileDAO;
import Server.User.UserDAO;
import java.net.Socket;

public class RequestHandler {

  public static SendReceiveSerializationObject
  requestHandler(final Socket so, final SendReceiveSerializationObject o) {
    final int TIN = o.getTaskIdentificationNumber();
    try {
      if (TIN == SendReceiveSerializationObject._USER_SIGNUP_REQUEST)
        return UserDAO.userSignup(o.getUserDTO());
      else if (TIN == SendReceiveSerializationObject._USER_LOGIN_REQUEST)
        return UserDAO.userLogin(o.getUserDTO());
      else if (TIN == SendReceiveSerializationObject._DESCONNECT_REQUEST) {
        if (so.isConnected())
          so.close();
        return new SendReceiveSerializationObject(
            SendReceiveSerializationObject._REQUEST_SUCCESSFULLY_PROCESSED);
      } else if (TIN == SendReceiveSerializationObject._FILELIST_REQUEST) {
        return FileDAO.getFilelist(o.getUserDTO().getUserID());
      } else if (TIN == SendReceiveSerializationObject._FILEUPLOAD_REQUEST) {
        return FileDAO.insertuploadedfile(so, o);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
/* 각종 DAO들안에 구현된 static 함수들은 직렬화 클래스를 반환해야함 */

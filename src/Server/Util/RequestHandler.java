package Server.Util;

import Server.File.FileDAO;
import Server.File.FileDTOList;
import Server.User.UserDAO;
import Server.User.UserDTO;
import java.net.Socket;

public class RequestHandler {

  public static ResponseObject requestHandler(final Socket so,
                                              final String request) {
    String[] s = request.split(" ");

    try {
      if (s[0].equals("usersignup"))
        return new ResponseObject(UserDAO.userSignup(new UserDTO(s[1]), s[2]));
      else if (s[0].equals("userlogin"))
        return new ResponseObject(UserDAO.userLogin(new UserDTO(s[1]), s[2]));
      else if (s[0].equals("desconnect")) {
        if (so.isConnected())
          so.close();
        return new ResponseObject(0);
      } else if (s[0].equals("filelist")) {
        FileDTOList l = FileDAO.getFilelist(s[1]);
        if (l == null)
          return new ResponseObject(-1);
        return new ResponseObject(0, l);
      } else if (s[0].equals("upload")) {
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

class ResponseObject {
  private int responseNumber;
  private FileDTOList l;

  public ResponseObject(int responseNumber, FileDTOList l) {
    this.responseNumber = responseNumber;
    this.l = l;
  }

  public ResponseObject(int responseNumber) {
    this.responseNumber = responseNumber;
    l = null;
  }

  @Override
  public String toString() {
    if (l == null)
      return responseNumber + "";
    return responseNumber + " " + l;
  }
}

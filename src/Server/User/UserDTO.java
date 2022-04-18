package Server.User;

import java.io.Serializable;

public class UserDTO implements Serializable{

  private String userID;
  private String plainPasswordText;

  public UserDTO(String userID, String plainPasswordText) {
    this.userID = userID;
    this.plainPasswordText = plainPasswordText;
  }
  
  public UserDTO(String userID) {
    this.userID = userID;
    plainPasswordText = null;
  }

  public String getUserID() { return userID; }

  public String getplainPasswordText() { return plainPasswordText; }
};
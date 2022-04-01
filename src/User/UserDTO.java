package src.User;

/* user_info : user_id(PK), nickname, ciphertext */

public class UserDTO {

  private String userID;
  private String userNickname;
  private String ciphertext;

  public UserDTO(String userID, String userNickname) {
    this.userID = userID;
    this.userNickname = userNickname;
  }

  public String getUserID() { return userID; }

  public void setUserID(String userID) { this.userID = userID; }

  public String getUserNickname() { return userNickname; }

  public void setUserNickname(String userNick) { this.userNickname = userNick; }

  public String getCiphertext() { return ciphertext; }

  public void setCiphertext(String ciphertext) { this.ciphertext = ciphertext; }
};
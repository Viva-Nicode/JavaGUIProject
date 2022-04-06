package Server.User;

public class UserDTO {

  private String userID;
  private String ciphertext;

  public UserDTO(String userID, String ciphertext) {
    this.userID = userID;
    this.ciphertext = ciphertext;
  }
  public UserDTO(String userID) {
    this.userID = userID;
  }

  public String getUserID() { return userID; }

  public void setUserID(String userID) { this.userID = userID; }

  public String getCiphertext() { return ciphertext; }

  public void setCiphertext(String ciphertext) { this.ciphertext = ciphertext; }
};
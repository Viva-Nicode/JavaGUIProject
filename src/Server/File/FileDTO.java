package Server.File;

import java.io.Serializable;

public class FileDTO implements Serializable {

  private String file_name;
  private String user_id;
  private String file_extention;
  private long file_bytesize;
  private String upload_date;
  private String file_comment;
  private String fileHashcode;

  public FileDTO(String file_name, String user_id, String file_extention,
                 long file_bytesize, String upload_date, String file_comment) {
    this.user_id = user_id;
    this.file_name = file_name;
    this.file_extention = file_extention;
    this.file_bytesize = file_bytesize;
    this.upload_date = upload_date;
    this.file_comment = file_comment;
  }

  public FileDTO(String file_name, String user_id, String file_extention,
                 long file_bytesize, String file_comment) {
    this.file_name = file_name;

    this.user_id = user_id;
    this.file_extention = file_extention;
    this.file_bytesize = file_bytesize;
    this.file_comment = file_comment;
  }

  public FileDTO(String user_id, String file_extention, long file_bytesize) {
    this.user_id = user_id;
    this.file_extention = file_extention;
    this.file_bytesize = file_bytesize;
  }

  public FileDTO(String file_name, String file_extention, long file_bytesize,
                 String upload_date, String file_comment) {
    this.file_name = file_name;
    this.file_extention = file_extention;
    this.file_bytesize = file_bytesize;
    this.upload_date = upload_date;
    this.file_comment = file_comment;
  }

  @Override
  public String toString() {
    return "{\"file_name\":\"" + file_name + "\", \"file_extention\":\"" +
        file_extention + "\", \"file_bytesize\":" + file_bytesize +
        ", \"upload_date\":\"" + upload_date + "\", \"file_comment\":\"" +
        file_comment + "\"}";
  }

  public String getUser_id() { return user_id; }

  public String getFile_name() { return file_name; }

  public String getFile_extention() { return file_extention; }

  public long getFile_size() { return file_bytesize; }

  public String getUpload_date() { return upload_date; }

  public String getFile_comment() { return file_comment; }

  public void setuploaddate(String date) { this.upload_date = date; }

  public void setFileHashcode(String Hashcode) { this.fileHashcode = Hashcode; }

  public String getFileHashcode() { return fileHashcode; }
  public static void main(String[] args) {
    System.out.println(new FileDTO("annyfile", "dmswns0147", "exe", 324, "20220516124333",
	"aaannnyyy commment death"));
        
  }
}
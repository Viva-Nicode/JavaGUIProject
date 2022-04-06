package Server.Util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class EncryptUtil {
  private String iv;
  private Key keySpec;

  
  public EncryptUtil() throws UnsupportedEncodingException {

    this.iv = producedvi();
    if (iv.getBytes("UTF-8").length == 16) {
      SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes("UTF-8"), "AES");
      
      this.keySpec = keySpec;
    }
  }
  public String getiv() { return iv; }

 
  public EncryptUtil(String iv) throws UnsupportedEncodingException {

    this.iv = iv;
    if (iv.getBytes("UTF-8").length == 16) {
      SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes("UTF-8"), "AES");
   
      this.keySpec = keySpec;
    }
  }

  /* 33 ~ 126 */
  public static String producedvi() {
    Random r = new Random();
    String result = "";
    for (int i = 0; i < 16; i++)
      result = result + (char)(r.nextInt(94) + 33);
    return result;
  }

 
  public byte[] encryptsion(String plainText)
      throws NoSuchAlgorithmException, GeneralSecurityException,
             UnsupportedEncodingException {

   
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

    
    cipher.init(Cipher.ENCRYPT_MODE, keySpec,
                new IvParameterSpec(iv.getBytes()));

    byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
    return Base64.encodeBase64(encrypted);
  }

 
  public String decipher(String cipherText)
      throws NoSuchAlgorithmException, GeneralSecurityException,
             UnsupportedEncodingException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, keySpec,
                new IvParameterSpec(iv.getBytes()));
    byte[] byteStr = Base64.decodeBase64(cipherText.getBytes());
    return new String(cipher.doFinal(byteStr), "UTF-8");
  }

  public static void main(String[] args) {

    String userPassword = "asd238tr335dw"; /* 10 ~ 15 */
    try {
      EncryptUtil e = new EncryptUtil("T}p{)c/1}eY02K`L");
      System.out.println("random generated key value : ");
      System.out.println("ciphertext : " +
                         new String(e.encryptsion(userPassword), "UTF-8"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
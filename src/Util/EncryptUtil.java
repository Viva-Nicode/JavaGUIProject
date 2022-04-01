package src.Util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
/* CBC의 Initialiaztion Vector를 만들기 위해 사용 */
import javax.crypto.spec.SecretKeySpec;
/* secret Key를 만들기 위해 사용 */
import org.apache.commons.codec.binary.Base64;

/*
        AES암호화 알고리즘은 256bit 즉 256byte길이의 키를 사용한다.

        AES는 16byte의 고정된 블록크기 단위로 암호화를 수행한다.때문에 iv도
   16byte여야 한다.

        암호화를 수행할때 여러가지 Block Cipher Mode를 선택가능한데 크게

        CBC(Cipher Block Chaning), ECB(Electronic CodeBook)가 있지만 ECB큰
   결함으로 CBC가 권장된다.

        CBC는 ECB와 달리 IV(Initialization Vector)를 사용하여 첫 평문블록과
   XOR하고 만들어진 암호문 블럭을 다음 평문 블록과 암호화한다.

        때문에 같은 평문도 다른 암호문이 생성된다. IV는 제2의 키 같은 느낌이다.

        병렬처리가 불가하므로 느리지만 보안에 더 강하다.

        암호화할 데이터가 128비트 미만인 경우 부족한 부분을 특정 값으로 채워야
   하는데 이를 패딩이라고 하며 PKCS#5, PKCS#7등의 방식으로 이를 수행한다.
   +iPAZc3v5bVpi90hno/aew==
*/

public class EncryptUtil {
  private String iv;
  private Key keySpec;

  /* 키의 길이는 16 이상이어야 한다. */

  public EncryptUtil(String iv) throws UnsupportedEncodingException {

    this.iv = iv.substring(0, 16);
    if (iv.getBytes("UTF-8").length == 16) {
      SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes("UTF-8"), "AES");
      /* iv값이 같으면 항상 동일한 키를 생성 */
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

    /* 암/복호화를 위한 사이퍼 객체 생성 */
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

    /* 사이퍼 객체를 암호화 모드로 키값, iv값을 넣어주고 초기화 */
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

    String userPassword = "asd238tr33!+@#7"; /* 10 ~ 15 */
    for (int i = 0; i < 20; i++) {
      try {
        EncryptUtil e = new EncryptUtil(producedvi());
        System.out.println("ciphertext : " +
                           new String(e.encryptsion(userPassword), "UTF-8"));
        System.out.println("ciphertext size : " +
                           e.encryptsion(userPassword).length);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
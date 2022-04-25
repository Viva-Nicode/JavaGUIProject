package GuiComponents;

import Client.ClientSocketIOObject;
import Server.Util.TaskNumbers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class HintTextField extends JTextField {

  Font gainFont = new Font("Tahoma", Font.PLAIN, 13);
  Font lostFont = new Font("Tahoma", Font.ITALIC, 13);

  public HintTextField(final String hint) {

    setText(hint);
    setFont(lostFont);
    setForeground(Color.GRAY);

    this.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (getText().equals(hint)) {
          setText("");
          setFont(gainFont);
        } else {
          setText(getText());
          setFont(gainFont);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (getText().equals(hint) || getText().length() == 0) {
          setText(hint);
          setFont(lostFont);
          setForeground(Color.GRAY);
        } else {
          setText(getText());
          setFont(gainFont);
          setForeground(Color.BLACK);
        }
      }
    });
  }
}

interface LoginFrameSetable {
  public final int LOGINFRAMEWIDTH = 390;
  public final int LOGINFRAMEHEIGHT = 590;
  public final Color bgcolor = new Color(252, 236, 92);
  public final String PRIVATEIP = "192.168.219.112";
  public final String PUBLICIP = "116.39.246.101";
  public final String LOCALHOST = "127.0.0.1";
  public final int PORT = 13243;
}

public class LoginFrame extends JFrame implements LoginFrameSetable {

  public ClientSocketIOObject c;

  class LoginPanel extends JPanel implements LoginFrameSetable {

    Gson json = new Gson();

    private HintTextField IDtextField = new HintTextField("User ID");
    private HintTextField PWtextField = new HintTextField("User Pw");

    private JLabel kakaoMark = new JLabel(new ImageIcon(
        "/Users/nicode./MainSpace/vscodeworkspace/JavaCloudProject/src/GuiComponents/imgs/kakao.png"));

    private JButton loginbtn = new JButton("Login");
    private JLabel SignUpLabel = new JLabel("SignUp");
    private JLabel FindPWLabel = new JLabel("FindPw");
    private JCheckBox autoLoginCheckBox = new JCheckBox("Automatic login");

    public LoginPanel() {

      try {
        c = new ClientSocketIOObject(LOCALHOST, PORT);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      this.setSize(LOGINFRAMEWIDTH, LOGINFRAMEHEIGHT);
      this.setLayout(null);
      setBackground(bgcolor);

      loginbtn.setBounds(75, 305, 240, 50);
      autoLoginCheckBox.setBounds(71, 350, 150, 30);
      loginbtn.requestFocusInWindow();
      kakaoMark.setBounds(80, 30, 230, 216);
      IDtextField.setBounds(75, 220, 240, 45);
      PWtextField.setBounds(75, 258, 240, 45);
      SignUpLabel.setBounds(135, 510, 45, 20);
      FindPWLabel.setBounds(205, 510, 45, 20);

      loginbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

          String loginFreamIDTextFieldValue = IDtextField.getText();
          String loginFreamPWTextFieldValue = PWtextField.getText();
          try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("requestType",
                                   TaskNumbers._USER_LOGIN_REQUEST);
            jsonObject.addProperty("user_id", loginFreamIDTextFieldValue);
            jsonObject.addProperty("user_pw", loginFreamPWTextFieldValue);
            c.sender(json.toJson(jsonObject));

            JsonObject jo =
                JsonParser.parseString(c.Receiver()).getAsJsonObject();

            if (jo.get("responseType").getAsInt() ==
                TaskNumbers._REQUEST_NOT_PROCESSED_PROPERLY) {
              JOptionPane.showMessageDialog(null, "id or pw is not exists!",
                                            "omg", 2);
            } else {
              dispose();
              new MainFrame(loginFreamIDTextFieldValue, c);
            }
          } catch (Exception eee) {
            eee.printStackTrace();
          }
        }
      });
      SignUpLabel.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          SignUpFrame SignUp = new SignUpFrame(c);
          SignUp.setVisible(true);
        }
      });

      this.add(SignUpLabel);
      this.add(IDtextField);
      this.add(PWtextField);
      this.add(kakaoMark);
      this.add(loginbtn);
      this.add(FindPWLabel);
      this.add(autoLoginCheckBox);

      requestFocus();
      setFocusable(true);
    }
  }

  public LoginFrame() {
    this.setTitle("kakao cloud");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new LoginPanel());
    this.setResizable(false);
    this.setSize(LOGINFRAMEWIDTH, LOGINFRAMEHEIGHT);
    setVisible(true);
  }
  public static void main(String[] args) { new LoginFrame(); }
}

/*

추가되어야 할 기능

1. 같은 계정 동시 접속 금지
2. 파일 이름, 코멘트 수정
3. 파일 이름순, 날짜 순, 크기 순 정렬

*/

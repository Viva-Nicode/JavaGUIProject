package src.GuiComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import src.Client.ClientSocket;

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
  public final int LOGINFRAMEHEIGHT = 490;
  public final Color bgcolor = new Color(252, 236, 92);
  public final String PRIVATEIP = "192.168.219.112";
  public final String PUBLICIP = "116.39.246.101";
  public final String LOCALHOST = "127.0.0.1";
  public final int PORT = 13243;
}

public class InitialScreen extends JFrame implements LoginFrameSetable {

  LoginPanel p = new LoginPanel();
  ClientSocket c;
  class LoginPanel extends JPanel implements LoginFrameSetable {

    private HintTextField IDtextField = new HintTextField("User ID");
    private HintTextField PWtextField = new HintTextField("User PW");

    private JLabel kakaoMark = new JLabel(new ImageIcon(
        "/Users/nicode./MainSpace/vscodecpp/JavaGUIProject/src/GuiComponents/imgs/kakao.png"));

    private JButton loginbtn = new JButton("Login");
    private JLabel SignUpLabel = new JLabel("SignUp");

    public LoginPanel() {
      this.setSize(LOGINFRAMEWIDTH, LOGINFRAMEHEIGHT);
      this.setLayout(null);
      setBackground(bgcolor);

      loginbtn.setBounds(75, 320, 240, 40);
      kakaoMark.setBounds(80, 30, 230, 216);
      IDtextField.setBounds(75, 250, 240, 40);
      PWtextField.setBounds(75, 280, 240, 40);
      SignUpLabel.setBounds(80, 360, 45, 20);

      loginbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String iiiddd = IDtextField.getText();
          String pppwww = PWtextField.getText();
          try {
            c = new ClientSocket(LOCALHOST, PORT);
            c.sender("userlogin " + iiiddd + " " + pppwww);
            if (c.receiver() < 0) {
              JOptionPane.showMessageDialog(null, "id or pw is not exists!",
                                            "omg", 2);
            } else {
              dispose();
              new MainFrame(iiiddd, c);
            }
          } catch (Exception eee) {
            eee.printStackTrace();
          }
        }
      });
      SignUpLabel.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          SignUp SignUp = new SignUp(c);
          SignUp.setVisible(true);
        }
      });

      this.add(SignUpLabel);
      this.add(IDtextField);
      this.add(PWtextField);
      this.add(kakaoMark);
      this.add(loginbtn);

      requestFocus();
      setFocusable(true);
    }
  }
  
  public InitialScreen() {
    this.setTitle("Basic Cloud - login");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(p);
    this.setResizable(false);
    this.setSize(LOGINFRAMEWIDTH, LOGINFRAMEHEIGHT);
    setVisible(true);
  }
  public static void main(String[] args) { new InitialScreen(); }
}

/* 데이터 베이스에 대형 객체 저장 하기 */
/* 업로드, 다운로드 기능 */
/* 클라이언트의 화면에 유저가 업로드한 파일들을 표시해주기 위해 데이터를 어떤 양식으로 주고 받을것인지 (즉 리퀘스트 핸들러의 문제가 되시겠다.) */
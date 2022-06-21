package Client;

import Common.TaskNumbers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

interface SignUpFrameSetable {
  public final int SIGNUPFRAMEWIDTH = 300;
  public final int SIGNUPFRAMEHEIGHT = 200;
  public final Color bgcolor = new Color(252, 236, 92);
}

public class SignUpFrame extends JDialog implements SignUpFrameSetable {

  private Gson j = new Gson();
  private JTextField iidd = new JTextField();
  private JTextField ppww = new JTextField();
  private JTextField email = new JTextField();
  private JLabel idlabel = new JLabel("ID");
  private JLabel pwlabel = new JLabel("PW");
  private JLabel emlabel = new JLabel("EM");
  private JButton SignUpbtn = new JButton("SignUP");
  private JLabel cautionMsg = new JLabel("please insert ID and PW");

  public SignUpFrame(ClientSocketIOObject c) {
    this.setTitle("Kakao Cloud - login");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new JPanel());

    getContentPane().setLayout(null);
    getContentPane().setSize(SIGNUPFRAMEWIDTH, SIGNUPFRAMEHEIGHT);
    getContentPane().setBackground(bgcolor);

    iidd.setBounds(30, 50, 200, 20);
    ppww.setBounds(30, 80, 200, 20);
    email.setBounds(30, 110, 200, 20);
    idlabel.setBounds(10, 50, 20, 20);
    pwlabel.setBounds(10, 80, 20, 20);
    emlabel.setBounds(10, 110, 20, 20);
    SignUpbtn.setBounds(30, 140, 200, 20);

    SignUpbtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String id = iidd.getText();
        String pw = ppww.getText();
        String em = email.getText();
        int idlen = id.length();
        int pwlen = pw.length();
        int emlen = em.length();
        if ((8 <= idlen && idlen <= 13) && (10 <= pwlen && pwlen <= 15) &&
            (0 <= emlen && emlen <= 30)) {
          try {
            JsonObject jo = new JsonObject();
            jo.addProperty("requestType", TaskNumbers._USER_SIGNUP_REQUEST);
            jo.addProperty("user_id", id);
            jo.addProperty("user_pw", pw);
            jo.addProperty("user_em", em);

            c.sender(j.toJson(jo));
            JsonObject joo =
                JsonParser.parseString(c.Receiver()).getAsJsonObject();
            int responseNum = joo.get("responseType").getAsInt();
            if (responseNum == TaskNumbers._REQUEST_NOT_PROCESSED_PROPERLY) {
              JOptionPane.showMessageDialog(null, "id overlap", "error", 2);
            } else if (responseNum ==
                       TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED) {
              dispose();
              JOptionPane.showMessageDialog(null, "sign up success");
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(
              null, "ID len : 8 ~ 13\nPW len : 10 ~ 15\nEM len : 0 ~ 30",
              "length error", 2);
        }
      }
    });
    cautionMsg.setBounds(10, 10, 250, 30);
    cautionMsg.setFont(new Font("Serif", Font.BOLD, 20));

    getContentPane().add(iidd);
    getContentPane().add(ppww);
    getContentPane().add(email);
    getContentPane().add(idlabel);
    getContentPane().add(pwlabel);
    getContentPane().add(emlabel);
    getContentPane().add(SignUpbtn);
    getContentPane().add(cautionMsg);

    this.setSize(SIGNUPFRAMEWIDTH, SIGNUPFRAMEHEIGHT);
    this.setResizable(false);
    setVisible(true);
  }
}

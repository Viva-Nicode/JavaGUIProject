package GuiComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Client.ClientSocket;

interface SignUpFrameSetable {
  public final int SIGNUPFRAMEWIDTH = 300;
  public final int SIGNUPFRAMEHEIGHT = 200;
  public final Color bgcolor = new Color(252, 236, 92);
}

public class SignUp extends JFrame implements SignUpFrameSetable {

  JTextField iidd = new JTextField();
  JTextField ppww = new JTextField();
  JLabel idlabel = new JLabel("ID");
  JLabel pwlabel = new JLabel("PW");
  JButton SignUpbtn = new JButton("SignUP");
  JLabel cautionMsg = new JLabel("please insert ID and PW");

  public SignUp(ClientSocket c) {
    this.setTitle("Basic Cloud - login");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new JPanel());

    getContentPane().setLayout(null);
    getContentPane().setSize(SIGNUPFRAMEWIDTH, SIGNUPFRAMEHEIGHT);
    getContentPane().setBackground(bgcolor);

    iidd.setBounds(30, 80, 200, 20);
    ppww.setBounds(30, 110, 200, 20);
    idlabel.setBounds(10, 80, 20, 20);
    pwlabel.setBounds(10, 110, 20, 20);
    SignUpbtn.setBounds(30, 140, 200, 20);

    SignUpbtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String id = iidd.getText();
        String pw = ppww.getText();
        int idlen = id.length();
        int pwlen = pw.length();
        if ((8 <= idlen && idlen <= 13) && (10 <= pwlen && pwlen <= 15)) {
          try {
            c.sender("usersignup " + id + " " + pw);
            int resultNum = c.numberReceiver();
            if (resultNum < 0) {
              JOptionPane.showMessageDialog(null, "id overlap", "error", 2);
            } else if (resultNum >= 0) {
              dispose();
              JOptionPane.showMessageDialog(null, "sign up success");
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(
              null, "ID len : 8 ~ 13\nPW len : 10 ~ 15", "length error", 2);
        }
      }
    });
    cautionMsg.setBounds(10, 10, 250, 30);
    cautionMsg.setFont(new Font("Serif", Font.BOLD, 20));

    getContentPane().add(iidd);
    getContentPane().add(ppww);
    getContentPane().add(idlabel);
    getContentPane().add(pwlabel);
    getContentPane().add(SignUpbtn);
    getContentPane().add(cautionMsg);

    this.setSize(SIGNUPFRAMEWIDTH, SIGNUPFRAMEHEIGHT);
    this.setResizable(false);
    setVisible(true);
  }
}

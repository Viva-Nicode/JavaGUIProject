package GuiComponents;

import Client.ClientSocketIOObject;
import Server.File.FileDTO;
import Server.Util.TaskNumbers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

interface setable {
  public final int FRAMEWIDTH = 650;
  public final int FRAMEHEIGHT = 600;
  public final Color jlistBackgroundColor = new Color(0x8977ad);
}

public class MainFrame extends JFrame implements setable {

  private final String connected_user_id;
  private ClientSocketIOObject c;
  private JList<String> fileList;

  public MainFrame(final String id, final ClientSocketIOObject c)
      throws ClassNotFoundException, IOException {
    this.connected_user_id = id;
    this.c = c;
    this.setTitle("kakao Cloud");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new MainPanel());
    this.setResizable(false);
    this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
    setVisible(true);
  }

  private class MainPanel extends JPanel {

    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem logout;
    private JButton uploadButton;
    private String extention = "";
    JFileChooser fileComponent;

    /* refersh fileDTOvector */
    public void updateFilelist(DefaultListModel<String> model)
        throws ClassNotFoundException, IOException {
      c.sender("{\"requestType\":" + TaskNumbers._FILELIST_REQUEST +
               ",\"user_id\":\"" + connected_user_id + "\""
               + "}");
      JsonObject jo = JsonParser.parseString(c.Receiver()).getAsJsonObject();
      int responseNum = jo.get("responseType").getAsInt();

      if (responseNum == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED) {
        JsonArray filelist = jo.get("filelist").getAsJsonArray();
        for (JsonElement filedto : filelist) {
          JsonObject j = filedto.getAsJsonObject();
          model.addElement(j.get("file_name").getAsString());
        }
      } else {
        model.clear();
        model.addElement("not exist any file..!");
      }
    }
    Image background =
        new ImageIcon(MainPanel.class.getResource("./imgs/bgimg.jpg"))
            .getImage();

    public void paintComponent(Graphics g) {
      g.drawImage(background, 0, 0, null);
    }

    public static Optional<String>
    getExtensionByStringHandling(String filename) {
      return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public MainPanel() throws ClassNotFoundException, IOException {

      this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
      this.setLayout(null);
      menubar = new JMenuBar();
      menu = new JMenu("MENU");
      menubar.add(menu);
      logout = new JMenuItem("logout");
      logout.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          c.sender("{\"requestType\":" + TaskNumbers._DESCONNECT_REQUEST + "}");
          c.desconnect();
          dispose();
          new LoginFrame();
        }
      });

      menu.add(logout);
      setJMenuBar(menubar);

      uploadButton = new JButton("upload");

      fileList = new JList<String>(new DefaultListModel());

      DefaultListModel<String> model =
          (DefaultListModel<String>)fileList.getModel();

      updateFilelist(model);

      uploadButton.setBounds(240, 50, 240, 40);
      uploadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileComponent = new JFileChooser();
          int ret = fileComponent.showOpenDialog(null);
          if (ret == JFileChooser.APPROVE_OPTION) {
            File f = new File(fileComponent.getSelectedFile().toString());
            Optional<String> ext = getExtensionByStringHandling(f.getName());
            ext.ifPresent(s -> extention = s);

            new FileUploadFrame(
                new FileDTO(connected_user_id, extention, f.length()), c, f);

            try {
              updateFilelist(model);
            } catch (ClassNotFoundException e1) {
              e1.printStackTrace();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        }
      });

      fileList.setBounds(20, 30, 200, 400);
      fileList.setBackground(jlistBackgroundColor);
      fileList.setForeground(Color.WHITE);
      fileList.setFixedCellHeight(20);

      add(fileList);
      add(uploadButton);

      requestFocus();
      setFocusable(true);
    }
  }

  class FileUploadFrame extends JDialog implements ActionListener {

    private ClientSocketIOObject c;
    private File f;
    private FileDTO fdto;

    public FileUploadFrame(FileDTO fdto, final ClientSocketIOObject c,
                           final File f) {

      this.c = c;
      this.f = f;
      this.fdto = fdto;
      this.setTitle("File Upload Window");
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.add(new MyCenterPanel(), BorderLayout.CENTER);
      this.setLocationRelativeTo(getOwner());
      this.setSize(300, 250);
      this.setVisible(true);
    }

    class MyCenterPanel extends JPanel {
      private JTextField filenametf;
      private JButton submitbtn;
      private JTextArea commentta;

      MyCenterPanel() {
        filenametf = new JTextField(20);
        filenametf.setDocument(new JTextFieldLimit(15));
        submitbtn = new JButton("submit");

        submitbtn.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            /* 이름과 코멘트가 비어있지는 않은지 체크해주어야 되지 않을까....!
             */
            JsonObject jo = new JsonObject();
            jo.addProperty("requestType", TaskNumbers._FILEUPLOAD_REQUEST);
            jo.addProperty("file_name", filenametf.getText());
            jo.addProperty("user_id", fdto.getUser_id());
            jo.addProperty("file_extention", fdto.getFile_extention());
            jo.addProperty("file_bytesize", fdto.getFile_size());
            jo.addProperty("file_comment", commentta.getText());
            c.sender(new Gson().toJson(jo));

            try {

              JsonObject joo =
                  JsonParser.parseString(c.Receiver()).getAsJsonObject();
              int r = joo.get("responseType").getAsInt();
              if (r == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED)
                c.binaryStreamSender(f);

            } catch (ClassNotFoundException | IOException e1) {
              e1.printStackTrace();
            } catch (NoSuchAlgorithmException e1) {
              e1.printStackTrace();
            }
            dispose();
          }
        });

        commentta = new JTextArea("", 7, 20);
        this.add(filenametf);
        this.add(new JScrollPane(commentta));
        this.add(submitbtn);
        this.setVisible(true);
        commentta.addKeyListener(new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
            int max = 49;
            int textLen = commentta.getText().length();
            if (textLen > max + 1) {
              e.consume();
              String shortened = commentta.getText().substring(0, max);
              commentta.setText(shortened);
            } else if (textLen > max) {
              e.consume();
            }
          }
          @Override
          public void keyPressed(KeyEvent e) {}

          @Override
          public void keyReleased(KeyEvent e) {}
        });
      }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
  }

  class JTextFieldLimit extends PlainDocument {

    private int limit;

    public JTextFieldLimit(int limit) {
      super();
      this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr)
        throws BadLocationException {
      if (str == null)
        return;

      if (getLength() + str.length() <= limit)
        super.insertString(offset, str, attr);
    }
  }
}

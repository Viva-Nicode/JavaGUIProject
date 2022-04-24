package GuiComponents;

import Client.ClientSocketIOObject;
import Server.File.FileDTO;
import Server.Util.TaskNumbers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
  public final Color jlistBackgroundColor = new Color(000);
}

public class MainFrame extends JFrame implements setable {

  private final String connected_user_id;
  private ClientSocketIOObject c;
  private JList<String> fileList;
  private List<FileDTO> l;

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

  private boolean isFilenameOverlap(final String filename) {
    for (FileDTO t : l) {
      if (t.getFile_name().equals(filename))
        return true;
    }
    return false;
  }

  private class MainPanel extends JPanel {

    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem logout;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton refreshfilelistButton;
    private String extention = "";
    private DefaultListModel<String> model;
    private fileInfoPrintPanel fip;
    JFileChooser fileComponent;

    private FileDTO getSelectItemFileDTO(final String filename) {

      for (FileDTO t : l) {
        if (t.getFile_name().equals(filename))
          return t;
      }
      return null;
    }

    public void updateFilelist(DefaultListModel<String> model)
        throws ClassNotFoundException, IOException {
      c.sender("{\"requestType\":" + TaskNumbers._FILELIST_REQUEST +
               ",\"user_id\":\"" + connected_user_id + "\""
               + "}");
      JsonObject jo = JsonParser.parseString(c.Receiver()).getAsJsonObject();
      int responseNum = jo.get("responseType").getAsInt();

      if (responseNum == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED) {
        JsonArray filelist = jo.get("filelist").getAsJsonArray();
        model.clear();
        l.clear();
        for (JsonElement filedto : filelist) {
          JsonObject j = filedto.getAsJsonObject();
          String com = j.get("file_comment").getAsString().replace("\f", "\n");
          l.add(new FileDTO(j.get("file_name").getAsString(),
                            j.get("file_extention").getAsString(),
                            j.get("file_bytesize").getAsLong(),
                            j.get("upload_date").getAsString(), com));
          model.addElement(j.get("file_name").getAsString());
        }
      } else {
        model.clear();
        l.clear();
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
      l = new ArrayList<>();
      fip = new fileInfoPrintPanel();
      fip.setLocation(240, 30);
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
      downloadButton = new JButton("download");
      refreshfilelistButton = new JButton("refresh");

      fileList = new JList<String>(new DefaultListModel());

      model = (DefaultListModel<String>)fileList.getModel();

      fileList.addMouseListener(new MouseListener() {
        @Override
        public void mousePressed(MouseEvent e) {
          FileDTO t = getSelectItemFileDTO(fileList.getSelectedValue());
          fip.setfileInfo(t);
          String ext = t.getFile_extention();
          if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") ||
              ext.equalsIgnoreCase("png")) {
            fip.setImageIcon("img");
            return;
          }
          fip.setImageIcon(ext);
        }
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
      });
      updateFilelist(model);

      uploadButton.setBounds(510, 30, 120, 40);
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
                new FileDTO(connected_user_id, extention, f.length()), c, f,
                model);
          }
        }
      });

      downloadButton.setBounds(510, 110, 120, 40);
      downloadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (fileList.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(null, "please choose file", "omg", 2);
            return;
          }
          fileComponent = new JFileChooser();
          fileComponent.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          int ret = fileComponent.showOpenDialog(null);
          if (ret == JFileChooser.APPROVE_OPTION) {
            FileDTO t = getSelectItemFileDTO(fileList.getSelectedValue());
            File f = new File(fileComponent.getSelectedFile().toString() + "/" +
                              t.getFile_name() + "." + t.getFile_extention());
            try {
              f.createNewFile();
              c.binaryStreamReceiver(f, t, connected_user_id);

              c.Receiver();

            } catch (JsonSyntaxException | ClassNotFoundException |
                     IOException e1) {
              e1.printStackTrace();
            }
          }
        }
      });

      refreshfilelistButton.setBounds(510, 70, 120, 40);
      refreshfilelistButton.addActionListener(new ActionListener() {
        JsonObject jo;
        @Override
        public void actionPerformed(ActionEvent e) {
          c.sender("{\"requestType\":" + TaskNumbers._FILELIST_REQUEST +
                   ",\"user_id\":\"" + connected_user_id + "\""
                   + "}");
          try {
            jo = JsonParser.parseString(c.Receiver()).getAsJsonObject();
          } catch (JsonSyntaxException | ClassNotFoundException |
                   IOException e1) {
            e1.printStackTrace();
          }
          int responseNum = jo.get("responseType").getAsInt();

          if (responseNum == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED) {
            JsonArray filelist = jo.get("filelist").getAsJsonArray();
            model.clear();
            l.clear();
            for (JsonElement filedto : filelist) {
              JsonObject j = filedto.getAsJsonObject();
              String csom =
                  j.get("file_comment").getAsString().replace("\f", "\n");
              l.add(new FileDTO(j.get("file_name").getAsString(),
                                j.get("file_extention").getAsString(),
                                j.get("file_bytesize").getAsLong(),
                                j.get("upload_date").getAsString(), csom));
              model.addElement(j.get("file_name").getAsString());
            }
          } else {
            model.clear();
            l.clear();
            model.addElement("not exist any file..!");
          }
        }
      });

      fileList.setBounds(20, 30, 200, 400);
      fileList.setBackground(jlistBackgroundColor);
      fileList.setForeground(Color.WHITE);
      fileList.setFixedCellHeight(20);

      add(fileList);
      add(uploadButton);
      add(downloadButton);
      add(refreshfilelistButton);
      add(fip);

      requestFocus();
      setFocusable(true);
    }

    class fileInfoPrintPanel extends JPanel {
      private final String iconsPath =
          "/Users/nicode./MainSpace/vscodeworkspace/JavaCloudProject/src/GuiComponents/imgs/ext_icons/";
      private JLabel extIconLabel;
      private JLabel filenameLabel;
      private JLabel fileSize;
      private JLabel fileUploadDate;
      private JTextArea fileCommentLabel;
      public fileInfoPrintPanel() {

        extIconLabel = new JLabel();
        filenameLabel = new JLabel();
        fileUploadDate = new JLabel();
        fileSize = new JLabel();
        fileCommentLabel = new JTextArea();
        fileCommentLabel.setEnabled(false);

        extIconLabel.setBounds(10, 10, 120, 120);
        filenameLabel.setBounds(10, 140, 220, 20);
        fileUploadDate.setBounds(10, 160, 220, 20);
        fileSize.setBounds(10, 180, 220, 20);
        fileCommentLabel.setBounds(10, 220, 240, 100);

        this.setSize(250, 400);
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        this.add(extIconLabel);
        this.add(filenameLabel);
        this.add(fileUploadDate);
        this.add(fileSize);
        this.add(fileCommentLabel);
      }

      public void setImageIcon(final String ext) {
        File f = new File(iconsPath + ext + ".png");
        if (f.exists())
          extIconLabel.setIcon(new ImageIcon(iconsPath + ext + ".png"));
        else
          extIconLabel.setIcon(new ImageIcon(iconsPath + "file.png"));
        extIconLabel.repaint();
        this.repaint();
      }
      public void setfileInfo(final FileDTO f) {
        filenameLabel.setText("file name : " + f.getFile_name());
        fileUploadDate.setText("upload date : " + f.getUpload_date());
        fileSize.setText(new String(f.getFile_size() + " byte"));
        fileCommentLabel.setText(f.getFile_comment());
      }
    }
  }

  class FileUploadFrame extends JDialog implements ActionListener {

    private ClientSocketIOObject c;
    private File f;
    private FileDTO fdto;
    private DefaultListModel<String> model;

    public FileUploadFrame(FileDTO fdto, final ClientSocketIOObject c,
                           final File f, DefaultListModel<String> model) {
      this.model = model;
      this.c = c;
      this.f = f;
      this.fdto = fdto;
      this.setTitle("File Upload Window");
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.add(new nameAndCommentInputPanel(), BorderLayout.CENTER);
      this.setLocationRelativeTo(getOwner());
      this.setSize(300, 250);
      this.setVisible(true);
    }

    class nameAndCommentInputPanel extends JPanel {
      private JTextField filenametf;
      private JButton submitbtn;
      private JTextArea commentta;

      nameAndCommentInputPanel() {
        filenametf = new JTextField(20);
        filenametf.setDocument(new JTextFieldLimit(30));
        submitbtn = new JButton("submit");

        submitbtn.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (filenametf.getText().equals("") ||
                commentta.getText().equals("") ||
                isFilenameOverlap(filenametf.getText())) {
              JOptionPane.showMessageDialog(
                  null, "please insert name and comment", "omg", 2);
            } else {
              JsonObject jo = new JsonObject();
              String com = commentta.getText().replaceAll("\\R", "\f");
              jo.addProperty("requestType", TaskNumbers._FILEUPLOAD_REQUEST);
              jo.addProperty("file_name", filenametf.getText());
              jo.addProperty("user_id", fdto.getUser_id());
              jo.addProperty("file_extention", fdto.getFile_extention());
              jo.addProperty("file_bytesize", fdto.getFile_size());
              jo.addProperty("file_comment", com);
              c.sender(new Gson().toJson(jo));

              try {
                JsonObject joo =
                    JsonParser.parseString(c.Receiver()).getAsJsonObject();
                int r = joo.get("responseType").getAsInt();
                if (r == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED)
                  c.binaryStreamSender(f);
                joo = JsonParser.parseString(c.Receiver()).getAsJsonObject();
                r = joo.get("responseType").getAsInt();
                if (r == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED) {
                  JsonArray filelist = joo.get("filelist").getAsJsonArray();
                  model.clear();
                  l.clear();
                  for (JsonElement filedto : filelist) {
                    JsonObject j = filedto.getAsJsonObject();
                    String comm =
                        j.get("file_comment").getAsString().replace("\f", "\n");
                    l.add(new FileDTO(j.get("file_name").getAsString(),
                                      j.get("file_extention").getAsString(),
                                      j.get("file_bytesize").getAsLong(),
                                      j.get("upload_date").getAsString(),
                                      comm));
                    model.addElement(j.get("file_name").getAsString());
                  }
                } else {
                  model.clear();
                  l.clear();
                  model.addElement("not exist any file..!");
                }
              } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
              } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
              }
              dispose();
            }
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
            int max = 60;
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

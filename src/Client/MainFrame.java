package Client;

import Common.FileDTO;
import Common.TaskNumbers;
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
import java.util.HashMap;
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
  public final int FRAMEHEIGHT = 400;
  public final Color jlistBackgroundColor = new Color(0xFFFFFF);
}

public class MainFrame extends JFrame implements setable {

  private final String connected_user_id;
  private ClientSocketIOObject c;
  private JList<String> fileList;
  private HashMap<String, FileDTO> filehashmap;

  public MainFrame(final String id, final ClientSocketIOObject c)
      throws ClassNotFoundException, IOException {
    this.connected_user_id = id;
    this.c = c;
    this.setTitle("kakao Cloud");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new MainPanel(this));
    this.setResizable(false);
    this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
    setVisible(true);
  }

  private class MainPanel extends JPanel {

    private MainFrame m;
    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem logout;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton deleteButton;
    private JButton modifyButton;
    private JButton refreshButton;
    private JButton sortByDate;
    private JButton sortBySize;
    private String extention = "";
    private DefaultListModel<String> model;
    private fileInfoPrintPanel fip;
    JFileChooser fileComponent;

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
        filehashmap.clear();
        for (JsonElement filedto : filelist) {
          JsonObject j = filedto.getAsJsonObject();
          String com = j.get("file_comment").getAsString().replace("\f", "\n");
          filehashmap.put(j.get("file_name").getAsString(),
                          new FileDTO(j.get("file_name").getAsString(),
                                      j.get("file_extention").getAsString(),
                                      j.get("file_bytesize").getAsLong(),
                                      j.get("upload_date").getAsString(), com));
          model.addElement(j.get("file_name").getAsString());
        }
      } else {
        model.clear();
        filehashmap.clear();
        model.addElement("not exist any file..!");
      }
    }

    Image background =
        new ImageIcon(MainPanel.class.getResource("./imgs/grabg.jpg"))
            .getImage();

    public void paintComponent(Graphics g) {
      g.drawImage(background, 0, 0, null);
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
      return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public MainPanel(MainFrame m) throws ClassNotFoundException, IOException {
      this.m = m;
      this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
      this.setLayout(null);
      filehashmap = new HashMap<>();
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
      deleteButton = new JButton("delete");
      modifyButton = new JButton("modify");
      sortByDate = new JButton("sort by date");
      sortBySize = new JButton("sort by size");
      refreshButton = new JButton("refresh");

      fileList = new JList<String>(new DefaultListModel());

      model = (DefaultListModel<String>)fileList.getModel();

      fileList.addMouseListener(new MouseListener() {
        @Override
        public void mousePressed(MouseEvent e) {
          FileDTO t = filehashmap.get(fileList.getSelectedValue());
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
                m, new FileDTO(connected_user_id, extention, f.length()), c, f,
                model);
          }
        }
      });

      downloadButton.setBounds(510, 70, 120, 40);
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
            FileDTO t = filehashmap.get(fileList.getSelectedValue());
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

      deleteButton.setBounds(510, 110, 120, 40);
      deleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String selectFilename = fileList.getSelectedValue();
          if (selectFilename == null) {
            JOptionPane.showMessageDialog(null, "please choose file", "omg", 2);
            return;
          }
          c.sender("{\"requestType\":" + TaskNumbers._FILEDELETE_REQUEST +
                   ", \"user_id\":\"" + connected_user_id +
                   "\", \"file_name\":\"" + selectFilename + "\"}");
          try {
            c.Receiver();
            model.remove(fileList.getSelectedIndex());
            filehashmap.remove(selectFilename);
          } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
          }
        }
      });

      modifyButton.setBounds(510, 150, 120, 40);
      modifyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String selectFilename = fileList.getSelectedValue();
          if (selectFilename == null) {
            JOptionPane.showMessageDialog(null, "please choose file", "omg", 2);
            return;
          }
          new ModifyFileInfoDialog(m, filehashmap, c, connected_user_id,
                                   selectFilename, model);
        }
      });

      sortByDate.setBounds(510, 190, 120, 40);
      sortByDate.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {}
      });

      sortBySize.setBounds(510, 230, 120, 40);
      sortBySize.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {}
      });

      refreshButton.setBounds(510, 270, 120, 40);
      refreshButton.addActionListener(new ActionListener() {
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
            filehashmap.clear();
            for (JsonElement filedto : filelist) {
              JsonObject j = filedto.getAsJsonObject();
              String csom =
                  j.get("file_comment").getAsString().replace("\f", "\n");
              filehashmap.put(j.get("file_name").getAsString(),
                              new FileDTO(j.get("file_name").getAsString(),
                                          j.get("file_extention").getAsString(),
                                          j.get("file_bytesize").getAsLong(),
                                          j.get("upload_date").getAsString(),
                                          csom));
              model.addElement(j.get("file_name").getAsString());
            }
          } else {
            model.clear();
            filehashmap.clear();
            model.addElement("not exist any file..!");
          }
        }
      });

      fileList.setBounds(20, 30, 200, 300);
      fileList.setBackground(jlistBackgroundColor);
      fileList.setForeground(Color.black);
      fileList.setFixedCellHeight(20);

      add(fileList);
      add(uploadButton);
      add(downloadButton);
      add(deleteButton);
      add(modifyButton);
      add(sortByDate);
      sortByDate.setEnabled(false);
      add(sortBySize);
      sortBySize.setEnabled(false);
      add(refreshButton);
      add(fip);

      requestFocus();
      setFocusable(true);
    }

    class fileInfoPrintPanel extends JPanel {
      private String iconsPath =
          "/Users/nicode./MainSpace/vscodeworkspace/JavaCloudProject/src/Client/imgs/ext_icons/";
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

        this.setSize(250, 300);
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
          extIconLabel.setIcon(new ImageIcon(f.getPath()));
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

    public FileUploadFrame(MainFrame m, FileDTO fdto,
                           final ClientSocketIOObject c, final File f,
                           DefaultListModel<String> model) {
      super(m, "File Upload Window", true);
      this.model = model;
      this.c = c;
      this.f = f;
      this.fdto = fdto;
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
                filehashmap.containsKey(filenametf.getText())) {
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
                  filehashmap.clear();
                  for (JsonElement filedto : filelist) {
                    JsonObject j = filedto.getAsJsonObject();
                    String comm =
                        j.get("file_comment").getAsString().replace("\f", "\n");
                    filehashmap.put(
                        j.get("file_name").getAsString(),
                        new FileDTO(j.get("file_name").getAsString(),
                                    j.get("file_extention").getAsString(),
                                    j.get("file_bytesize").getAsLong(),
                                    j.get("upload_date").getAsString(), comm));
                    model.addElement(j.get("file_name").getAsString());
                  }
                } else {
                  model.clear();
                  filehashmap.clear();
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

class ModifyFileInfoDialog extends JDialog {

  private HashMap<String, FileDTO> filehashmap;
  private ClientSocketIOObject c;
  private String cui;
  private String selected_filename;
  private DefaultListModel<String> model;
  public ModifyFileInfoDialog(MainFrame m, HashMap<String, FileDTO> fileHashMap,
                              final ClientSocketIOObject c, final String cui,
                              final String selected_Filename,
                              final DefaultListModel<String> model) {
    super(m, "File info modify Window", true);
    this.c = c;
    this.model = model;
    this.cui = cui;
    this.selected_filename = selected_Filename;
    this.filehashmap = fileHashMap;
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

    public nameAndCommentInputPanel() {
      filenametf = new JTextField(20);
      filenametf.setDocument(new JTextFieldLimit(30));
      submitbtn = new JButton("submit");
      commentta = new JTextArea("", 7, 20);

      submitbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (filenametf.getText().equals("") ||
              commentta.getText().equals("") ||
              filehashmap.containsKey(filenametf.getText())) {
            JOptionPane.showMessageDialog(
                null, "please insert name and comment", "omg", 2);
          } else {
            String com = commentta.getText().replaceAll("\\R", "\f");
            c.sender("{\"requestType\":" +
                     TaskNumbers._FILEINFO_MODIFICATION_REQUEST +
                     ", \"user_id\":\"" + cui + "\", \"file_name\":\"" +
                     selected_filename + "\", \"new_filename\":\"" +
                     filenametf.getText() + "\", \"new_fileComment\":\"" + com +
                     "\"}");
            try {
              c.Receiver();
              c.sender("{\"requestType\":" + TaskNumbers._FILELIST_REQUEST +
                       ",\"user_id\":\"" + cui + "\""
                       + "}");
              JsonObject jo =
                  JsonParser.parseString(c.Receiver()).getAsJsonObject();
              int responseNum = jo.get("responseType").getAsInt();

              if (responseNum == TaskNumbers._REQUEST_SUCCESSFULLY_PROCESSED) {
                JsonArray filelist = jo.get("filelist").getAsJsonArray();
                model.clear();
                filehashmap.clear();
                for (JsonElement filedto : filelist) {
                  JsonObject j = filedto.getAsJsonObject();
                  String comm =
                      j.get("file_comment").getAsString().replace("\f", "\n");
                  filehashmap.put(
                      j.get("file_name").getAsString(),
                      new FileDTO(j.get("file_name").getAsString(),
                                  j.get("file_extention").getAsString(),
                                  j.get("file_bytesize").getAsLong(),
                                  j.get("upload_date").getAsString(), comm));
                  model.addElement(j.get("file_name").getAsString());
                }
              } else {
                model.clear();
                filehashmap.clear();
                model.addElement("not exist any file..!");
              }
              dispose();
            } catch (ClassNotFoundException | IOException e1) {
              e1.printStackTrace();
            }
          }
        }
      });

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

      this.add(filenametf);
      this.add(new JScrollPane(commentta));
      this.add(submitbtn);
      this.setVisible(true);
    }
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
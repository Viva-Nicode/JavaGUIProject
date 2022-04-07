package GuiComponents;

import Client.ClientSocketIOObject;
import Server.File.FileDTO;
import Server.File.FileDTOList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/* class FileChooser {

  private JFileChooser fileComponent = new JFileChooser();

  public FileChooser(DefaultListModel<String> model) {

    int ret = fileComponent.showOpenDialog(null);
    if (ret == JFileChooser.APPROVE_OPTION) {
      model.addElement(fileComponent.getSelectedFile().toString());
    }
  }
} */

interface setable {
  public final int FRAMEWIDTH = 1024;
  public final int FRAMEHEIGHT = 768;
  public final Color jlistBackgroundColor = new Color(0x8977ad);
}

public class MainFrame extends JFrame implements setable {
  private final String connected_user_id;
  private ClientSocketIOObject c;

  public MainFrame(final String id, final ClientSocketIOObject c,
                   final String filelistResponse) {
    this.connected_user_id = id;
    this.c = c;
    this.setTitle("Basic Cloud");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new MainPanel(filelistResponse));
    this.setResizable(false);
    this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
    setVisible(true);
  }

  private class MainPanel extends JPanel {

    JList<String> fileList;
    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem logout;
    private JButton uploadButton;
    private FileDTOList fileDTOlist;
    JFileChooser fileComponent;

    Image background =
        new ImageIcon(MainPanel.class.getResource("./imgs/bgimg.jpg"))
            .getImage();

    public void paintComponent(Graphics g) {
      g.drawImage(background, 0, 0, null);
    }

    public MainPanel(final String filelistResponse) {

      this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
      this.setLayout(null);

      menubar = new JMenuBar();
      menu = new JMenu("MENU");
      menubar.add(menu);
      logout = new JMenuItem("logout");
      logout.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          c.sender("desconnect");
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

      String[] fileMetadataArray = filelistResponse.split(" ");

      uploadButton.setBounds(240, 50, 240, 40);
      uploadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileComponent = new JFileChooser();
          int ret = fileComponent.showOpenDialog(null);
          if (ret == JFileChooser.APPROVE_OPTION) {
            model.addElement(fileComponent.getSelectedFile().toString());
          }
        }
      });

      if (Integer.parseInt(fileMetadataArray[0]) == -1) {
        model.addElement("not exist any file..!");
      } else {
        fileDTOlist = new FileDTOList();
        for (int idx = 1; idx < fileMetadataArray.length; idx = idx + 6) {
          fileDTOlist.add(new FileDTO(
              Integer.parseInt(fileMetadataArray[idx]),
              fileMetadataArray[idx + 1], fileMetadataArray[idx + 2],
              Integer.parseInt(fileMetadataArray[idx + 3]),
              fileMetadataArray[idx + 4], fileMetadataArray[idx + 5]));
          model.addElement(fileMetadataArray[idx + 1]);
        }
      }
      /*responseNumber (int file_id) name extention (int size) date comment*/

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

  class DetailedInformationLabel extends JLabel {}
}

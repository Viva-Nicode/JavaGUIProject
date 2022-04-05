package src.GuiComponents;

import java.awt.Color;
import java.awt.FlowLayout;
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

import src.Client.ClientSocket;

class FileChooserTest extends JFrame implements ActionListener {
  private JFileChooser fileComponent = new JFileChooser();
  private JButton btnOpen = new JButton("열기");
  private JButton btnSave = new JButton("저장");
  private JLabel labelOpen = new JLabel(" ");
  private JLabel labelSave = new JLabel(" ");

  public FileChooserTest() {
    this.init();
    this.start();
    this.setSize(500, 300);
    this.setVisible(true);
  }

  public void init() {
    getContentPane().setLayout(new FlowLayout());
    add(btnOpen);
    add(btnSave);
    add(labelOpen);
    add(labelSave);
  }
  public void start() {
    btnOpen.addActionListener(this);
    btnSave.addActionListener(this);
    /* fileComponent.setFileFilter(new FileNameExtensionFilter(
        "xlsx", "xlsx", "xls")); */
    fileComponent.setMultiSelectionEnabled(false); // 다중 선택 불가 설정
  }
  public void actionPerformed(ActionEvent arg0) {
    if (arg0.getSource() == btnOpen) {
      if (fileComponent.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        labelOpen.setText("열기 파일 경로 : " +
                          fileComponent.getSelectedFile().toString());
      }
    } else if (arg0.getSource() == btnSave) {
      if (fileComponent.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        labelSave.setText("저장 파일 경로 : " +
                          fileComponent.getSelectedFile().toString());
      }
    }
  }
}

interface setable {
  public final int FRAMEWIDTH = 1024;
  public final int FRAMEHEIGHT = 768;
  public final Color jlistBackgroundColor = new Color(0x8977ad);
}

public class MainFrame extends JFrame implements setable {
  private final String id;
  private ClientSocket c;

  public MainFrame(final String id, final ClientSocket c) {
    this.id = id;
	this.c = c;
    this.setTitle("Basic Cloud");
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(getOwner());
    this.setContentPane(new MainPanel());
    this.setResizable(false);
    this.setSize(FRAMEWIDTH, FRAMEHEIGHT);
    setVisible(true);
  }
  private class MainPanel extends JPanel {

    JList fileList;
    private DefaultListModel model;
    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem logout;

    Image background =
        new ImageIcon(MainPanel.class.getResource("./imgs/bgimg.jpg"))
            .getImage();

    public void paintComponent(Graphics g) {
      g.drawImage(background, 0, 0, null);
    }

    public MainPanel() {

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
			new InitialScreen();
		}
      });
      menu.add(logout);
      setJMenuBar(menubar);

      model = new DefaultListModel();
      fileList = new JList(model);

      model.addElement("element 1");
      model.addElement("element 2");
      model.addElement("element 3");

      fileList.setBounds(20, 30, 200, 400);
      fileList.setBackground(jlistBackgroundColor);
      fileList.setForeground(Color.WHITE);
      fileList.setFixedCellHeight(20);

      add(fileList);

      requestFocus();
      setFocusable(true);
    }
  }

  class DetailedInformationLabel extends JLabel {}
  public static void main(String[] args) throws Exception {
    new MainFrame("id", new ClientSocket("fe", 23));
  }
}


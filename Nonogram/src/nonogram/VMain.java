package nonogram;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Lukasz Kowalczyk
 */
public class VMain{
    
    JFrame mainFrame;
    //JPanel mainPanel;
    JMenuBar menuBar;
    JMenu mStart,mHelp;
    JMenuItem iNewGame,iUpload,iRandomize,iRules,iControl;
   BufferedImage logo_image;
    
    class MyCanvas extends JComponent 
    {
        public void paint(Graphics g) 
        {
            g.drawImage(logo_image, 34, 15, this);
        }
    }
    
    
    public VMain()
    {
        mainFrame = new JFrame("Nonogram");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700,150);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
 /*       mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0,0,700,150);*/
        try{
            logo_image = ImageIO.read(new File("pics/logo.gif"));
        }
        catch(IOException e)
        {
            System.err.println("NO LOGO!");
            e.printStackTrace();
        }
        menuBar = new JMenuBar();
        /*mainFrame.add(mainPanel);*/
        mainFrame.setJMenuBar(menuBar);
        fill_menuBar(); 
        mainFrame.getContentPane().add(new MyCanvas());
        mainFrame.setVisible(true);
    }
    
    void fill_menuBar()
    {
        mStart =new JMenu("Start");
        menuBar.add(mStart);
        mHelp = new JMenu("Pomoc");
        menuBar.add(mHelp);
        iNewGame = new JMenuItem("Nowa gra");
        iUpload = new JMenuItem("Wczytaj obraz...");
        iRandomize = new JMenuItem("Generuj losowe");
        iRules = new JMenuItem("Zasady gry");
        iControl = new JMenuItem("Sterowanie");
        mStart.add(iNewGame);
        mStart.add(iUpload);
        mStart.add(iRandomize);
        mHelp.add(iRules);
        mHelp.add(iControl);
        
    }
}

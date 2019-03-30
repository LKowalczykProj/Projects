package nonogram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Lukasz Kowalczyk
 */
public class Nonogram {

    static VMain mainWindow;
    static RuleWindow Help;
    static ControlWindow Help2;
    static VDifficulty diffWindow;
    static VSelect levelSelect;
    static String difficulty,level;
    //static ArrayList<VBoard> BoardList;
    static ArrayList<Picture> picsBuffer;
    //static ButtonControler controler;
    //static int BoardMarker;
    
    static class ButtonControler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent ae) {
            Object src = ae.getSource();
            if(src==mainWindow.iNewGame)
            {
                diffWindow.setVisible(true); 
            }
            if(src==mainWindow.iRules)
            {
                Help.setVisible(true);
            }
            if(src==mainWindow.iControl)
            {
                Help2.setVisible(true);
            }
            if(src==mainWindow.iRandomize)
            {
                Picture pic = new Picture("",true);
                picsBuffer.add(pic);
                mainWindow.mainFrame.setVisible(false);
            }
            if(src==mainWindow.iUpload)
            {
                ImageLoader test = new ImageLoader();
                if(!test.errorFlag)
                {
                    Picture pic = new Picture(test.pathFile,false);
                    picsBuffer.add(pic);
                    mainWindow.mainFrame.setVisible(false);
                }
                
            }
            if(src==diffWindow.bEasy)
            {
                difficulty="easy";
                diffWindow.setVisible(false);
                levelSelect.reset_buttons(1);
                levelSelect.setVisible(true);
            }
            if(src==diffWindow.bMedium)
            {
                difficulty="medium";
                diffWindow.setVisible(false);
                levelSelect.reset_buttons(11);
                levelSelect.setVisible(true);
            }
            if(src==diffWindow.bHard)
            {
                difficulty="hard";
                diffWindow.setVisible(false);
                levelSelect.reset_buttons(21);
                levelSelect.setVisible(true);
            }
            if(src==levelSelect.bConfirm)
            {
                level=levelSelect.choice;
                levelSelect.setVisible(false);
                Picture pic = new Picture("pics/" + level + ".gif",false);
                picsBuffer.add(pic);
                mainWindow.mainFrame.setVisible(false);
            }
        }
        
    }
    
    public static void main(String[] args) {
        ButtonControler controler = new ButtonControler();
        picsBuffer = new ArrayList<>();
        //BoardList = new ArrayList<>();
        mainWindow = new VMain();
        mainWindow.mainFrame.setVisible(true);
        mainWindow.iNewGame.addActionListener(controler);
        mainWindow.iRules.addActionListener(controler);
        mainWindow.iControl.addActionListener(controler);
        mainWindow.iRandomize.addActionListener(controler);
        mainWindow.iUpload.addActionListener(controler);
        diffWindow = new VDifficulty();
        diffWindow.setVisible(false);
        diffWindow.bEasy.addActionListener(controler);
        diffWindow.bMedium.addActionListener(controler);
        diffWindow.bHard.addActionListener(controler);
        //VBoard board = new VBoard(path,num);
        levelSelect =new VSelect(1);
        levelSelect.setVisible(false);
        levelSelect.bConfirm.addActionListener(controler);
        Help = new RuleWindow();
        Help.setVisible(false);
        Help2 = new ControlWindow();
        Help2.setVisible(false);
        //MouseBoard test = new MouseBoard();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonogram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Kowalczyk
 */
public class ImageLoader extends JComponent{
    
    public String pathFile;
    public Boolean errorFlag; 
    
    public ImageLoader()
    {
        JFileChooser fc = new JFileChooser();
        File plik=null;
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION);
        {
            plik = fc.getSelectedFile();
            pathFile = plik.getAbsolutePath();
            if(pathFile.regionMatches(pathFile.length()-4, ".gif",0 ,4 ))
                errorFlag=false;
            else
            {
                errorFlag=true;
                JOptionPane.showMessageDialog(null,"Zły format pliku! Powinien byc .gif","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

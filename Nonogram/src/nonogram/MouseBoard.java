/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonogram;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Kowalczyk
 */
public class MouseBoard implements MouseListener{
    
    JFrame main;
    //JPanel panel;
    int mouseX,mouseY;
    int size,width,height;
    int tab[][];
    
    class MyCanvas extends JComponent 
    {
        public void paint(Graphics g) 
        {
            for(int i=0;i<width;i++)
            {
                for(int j=0;j<height;j++)
                {
                    g.setColor(Color.black);
                    g.drawRect(0+i*size, 0+j*size, size, size);
                    if(tab[i][j]==0)
                        g.setColor(Color.white);
                    g.fillRect(1+i*size, 1+j*size, size-1, size-1);
                }
            }  
                //g.setColor(Color.black);
                //g.drawRect(0+(mouseX/size)*size, 0+(mouseY/size)*size, size, size);
                //g.setColor(Color.white);
                //g.fillRect(1+(mouseX/size)*size, 1+(mouseY/size)*size, size-2, size-2);
        }
    }
    
    public MouseBoard()
    {
        size=20;
        width=25;
        height=25;
        tab = new int[25][25];
        for(int i=0;i<25;i++)
        {
            for(int j=0;j<25;j++)
            {
                tab[i][j]=0;
            }
        }
        main=new JFrame("test");
        //panel = new JPanel();
        //panel.setBounds(0,0,517,540);
        main.addMouseListener(this);
        main.setSize(517,540);
        main.getContentPane().add(new MyCanvas());
        main.setVisible(true);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        //mouseX=MouseInfo.getPointerInfo().getLocation().x;
        //mouseY=MouseInfo.getPointerInfo().getLocation().y;
        mouseX = me.getX()-8;
        mouseY = me.getY()-31;
        if(tab[mouseX/size][mouseY/size]==0)
            tab[mouseX/size][mouseY/size]=1;
        else
            tab[mouseX/size][mouseY/size]=0;
        main.getContentPane().add(new MyCanvas());
        main.setVisible(true);
        System.out.println(mouseX + " " + mouseY);
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}

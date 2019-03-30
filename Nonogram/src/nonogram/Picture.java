/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonogram;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author student
 */



public class Picture implements MouseListener, ActionListener{
    
    private BufferedImage Image= null;
    private WritableRaster Raster = null;
    private int Tab[][];
    private int PictureTab[][];
    public String upper_tab[][],left_tab[][];
    //upper_tab = new String[maxVertical][Width];
    //left_tab = new String [Height][maxHorizontal];
    private int Width,Height;
    private int maxHorizontal,maxVertical;
    private int mouseX,mouseY,pressedX,pressedY,mode,box_size; //0 - white , 1 - black, 2 - x, 3 - dot
    private JFrame fMain;
    private JButton bContX,bContWhite,bContDot,bContBlack;
    public JButton bCHECK;
    public Boolean solved;
    public JPanel pConfirm;
    
    class MyCanvas extends JComponent 
    {
        public void paint(Graphics g) 
        {
            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++)
                {
                    if(PictureTab[j][i]==0)
                    {
                        g.setColor(Color.black);
                        g.drawRect((maxHorizontal+i)*box_size, (maxVertical+j)*box_size, box_size, box_size);
                        g.setColor(Color.white);
                        g.fillRect((maxHorizontal+i)*box_size+1, (maxVertical+j)*box_size+1, box_size-1, box_size-1);
                    }
                    if(PictureTab[j][i]==1)
                    {
                        g.setColor(Color.black);
                        g.drawRect((maxHorizontal+i)*box_size, (maxVertical+j)*box_size, box_size, box_size);
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect((maxHorizontal+i)*box_size+1, (maxVertical+j)*box_size+1, box_size-1, box_size-1);
                    }
                    if(PictureTab[j][i]==2)
                    {
                        g.setColor(Color.black);
                        g.drawRect((maxHorizontal+i)*box_size, (maxVertical+j)*box_size, box_size, box_size);
                        g.setColor(Color.white);
                        g.fillRect((maxHorizontal+i)*box_size+1, (maxVertical+j)*box_size+1, box_size-1, box_size-1);
                        g.setColor(Color.black);
                        g.drawLine((maxHorizontal+i)*box_size, (maxVertical+j)*box_size, (maxHorizontal+i+1)*box_size, (maxVertical+j+1)*box_size);
                        g.drawLine((maxHorizontal+i)*box_size, (maxVertical+j+1)*box_size, (maxHorizontal+i+1)*box_size+1, (maxVertical+j)*box_size);
                        
                    }
                    if(PictureTab[j][i]==3)
                    {
                        g.setColor(Color.black);
                        g.drawRect((maxHorizontal+i)*box_size, (maxVertical+j)*box_size, box_size, box_size);
                        g.setColor(Color.white);
                        g.fillRect((maxHorizontal+i)*box_size+1, (maxVertical+j)*box_size+1, box_size-1, box_size-1);
                        g.setColor(Color.black);
                        g.fillOval((maxHorizontal+i)*box_size+box_size/4, (maxVertical+j)*box_size+box_size/4, box_size/2, box_size/2);
                    }
                    
                }
            }  
                //g.setColor(Color.black);
                //g.drawRect(0+(mouseX/size)*size, 0+(mouseY/size)*size, size, size);
                //g.setColor(Color.white);
                //g.fillRect(1+(mouseX/size)*size, 1+(mouseY/size)*size, size-2, size-2);
        }
    }
    
    
    public Picture(String path,Boolean rand_select)
    {
        box_size=25;
        solved=false;
        if(rand_select)
        {
            Height=15;
            Width=15;
            Tab= new int[Height][Width];
            PictureTab = new int[Height][Width];
            for(int i=0;i<Height;i++)
            {
                for(int j=0;j<Width;j++)
                {
                    Random rand = new Random();
                    int temp = rand.nextInt();
                    if(temp<0)
                        temp*=(-1);
                    temp=temp%10;
                    if(temp<4)
                        Tab[i][j]=0;
                    else
                        Tab[i][j]=1;
                    PictureTab[i][j]=0;
                }
            }
        }
        else
        {
            File ImageFile = new File(path);
            try{
                Image=ImageIO.read(ImageFile);
            }
            catch(IOException e)
            {
                System.err.println(path);
                e.printStackTrace();
            }
            Raster=Image.getRaster();
            Width=Raster.getWidth();
            Height=Raster.getHeight();
            Tab= new int[Height][Width];
            PictureTab = new int[Height][Width];
            convert();
        }
        measure();
        create_tabs();
        displayGUI();
    }
    
    private void convert()
    {
        int temp[]= new int[3];
        for(int i=0;i<Height;i++)
        {
            for(int j=0;j<Width;j++)
            {
                Raster.getPixel(j, i, temp);
                if(temp[0]<128 && temp[1]<128 && temp[2]<128)
                    Tab[i][j]=1;
                else
                    Tab[i][j]=0;
                PictureTab[i][j]=0;
            }
        }
    }
    
    private void measure()
    {
        maxHorizontal=0;
        maxVertical=0;
        for(int i=0;i<Height;i++)
        {
            int curRow=0;
            boolean empty=true;
            for(int j=0;j<Width;j++)
            {
                if(empty)
                {
                    if(Tab[i][j]==1)
                    {
                        curRow++;
                        empty=false;
                    }
                }
                else
                {
                    if(Tab[i][j]==0)
                    {
                        empty=true;
                    }
                }
            }
            if(curRow>maxHorizontal)
                maxHorizontal=curRow;
        }
        
        for(int i=0;i<Width;i++)
        {
            int curCol=0;
            boolean empty=true;
            for(int j=0;j<Height;j++)
            {
                if(empty)
                {
                    if(Tab[j][i]==1)
                    {
                        curCol++;
                        empty=false;
                    }
                }
                else
                {
                    if(Tab[j][i]==0)
                    {
                        empty=true;
                    }
                }
            }
            if(curCol>maxVertical)
                maxVertical=curCol;
        }
    }
    private void create_tabs()
    {
        int HorP=maxHorizontal-1;
        int VerP=maxVertical-1;
        int counter=0;
        upper_tab = new String[maxVertical][Width];
        left_tab = new String [Height][maxHorizontal];
        
        //upper_tab
        
        //filling table with blanks
        for(int i=0;i<maxVertical;i++)
        {
            for(int j=0;j<Width;j++)
                upper_tab[i][j]=".";
        }
        
        for(int i=0;i<Width;i++)
        {
            for(int j=Height-1;j>=0;j--)
            {
                if(Tab[j][i]==0)
                {
                    if(counter!=0)
                    {
                        upper_tab[VerP][i]=Integer.toString(counter);
                        VerP--;
                        counter=0;
                    }
                }
                else
                {
                    counter++;
                }
            }
            if(counter!=0)
            {
                upper_tab[VerP][i]=Integer.toString(counter);
                VerP--;
                counter=0;
            }
            VerP=maxVertical-1;
        }
        
        //left_tab
        
        //filling table with blanks
        for(int i=0;i<Height;i++)
        {
            for(int j=0;j<maxHorizontal;j++)
                left_tab[i][j]=".";
        }
        
        for(int i=0;i<Height;i++)
        {
            for(int j=Width-1;j>=0;j--)
            {
                if(Tab[i][j]==0)
                {
                    if(counter!=0)
                    {
                        left_tab[i][HorP]=Integer.toString(counter);
                        HorP--;
                        counter=0;
                    }
                }
                else
                {
                    counter++;
                }
            }
            if(counter!=0)
            {
                left_tab[i][HorP]=Integer.toString(counter);
                HorP--;
                counter=0;
            }
            HorP=maxHorizontal-1;
        }
        
    }
    
    public void display()
    {
        for(int i=0;i<Height;i++)
        {
            for(int j=0;j<Width;j++)
            {
                if(Tab[i][j]==0)
                    System.out.print(" ");
                else
                    System.out.print("X");
            }
            System.out.println();
        }
        System.out.println(maxHorizontal + " " + maxVertical);
        
        //upper_tab
        
        for(int i=0;i<maxVertical;i++)
        {
            for(int j=0;j<Width;j++)
            {
                System.out.print(upper_tab[i][j] + " ");
            }
            System.out.println();
        }
        
        System.out.println("********************");
        
        //left_tab;
        
        for(int i=0;i<Height;i++)
        {
            for(int j=0;j<maxHorizontal;j++)
            {
                System.out.print(left_tab[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println(maxHorizontal + " " + maxVertical);
    }
    
    public void displayGUI()
    {
        mode=1;
        fMain = new JFrame();
        //fMain.setLayout(null);
        fMain.setSize((maxHorizontal+Width)*box_size+7,(maxVertical+Height)*box_size+70);
        fMain.setResizable(false);
        JPanel uPanel = new JPanel();
        uPanel.setLayout(null);
        uPanel.setBounds(maxHorizontal*box_size,0,Width*box_size+50,maxVertical*box_size);
        uPanel.setBackground(Color.LIGHT_GRAY);
        for(int i=0;i<maxVertical;i++)
        {
            for(int j=0;j<Width;j++)
            {
                JLabel p=new JLabel(upper_tab[i][j]);
                p.setBounds(j*box_size,i*box_size,box_size,box_size);
                p.setHorizontalAlignment(SwingConstants.CENTER);
                p.setVerticalAlignment(SwingConstants.CENTER);
                uPanel.add(p);
            }
        }
        JPanel lPanel = new JPanel();
        lPanel.setLayout(null);
        lPanel.setBounds(0,maxVertical*box_size,maxHorizontal*box_size,Height*box_size+50);
        lPanel.setBackground(Color.LIGHT_GRAY);
        for(int i=0;i<Height;i++)
        {
            for(int j=0;j<maxHorizontal;j++)
            {
                JLabel p=new JLabel(left_tab[i][j]);
                p.setBounds(j*box_size,i*box_size,box_size,box_size);
                p.setHorizontalAlignment(SwingConstants.CENTER);
                p.setVerticalAlignment(SwingConstants.CENTER);
                lPanel.add(p);
            }
        }
        
        JPanel pControl = new JPanel();
        pControl.setBounds(0,0, maxHorizontal*box_size, maxVertical*box_size);
        pControl.setLayout(null);
        ImageIcon iBlackBox = new ImageIcon("icons/black.gif");
        ImageIcon iWhiteBox = new ImageIcon("icons/white.gif");
        ImageIcon iXBox = new ImageIcon("icons/X.gif");
        ImageIcon iDotBox = new ImageIcon("icons/dot.gif");
        bContBlack = new JButton();
        bContBlack.setIcon(iBlackBox);
        bContBlack.setBounds(0, 0, maxHorizontal*box_size/2, maxVertical*box_size/2);
        bContBlack.addActionListener(this);
        bContWhite = new JButton();
        bContWhite.setIcon(iWhiteBox);
        bContWhite.setBounds(maxHorizontal*box_size/2,0,maxHorizontal*box_size/2,maxVertical*box_size/2);
        bContWhite.addActionListener(this);
        bContX = new JButton();
        bContX.setIcon(iXBox);
        bContX.setBounds(0,maxVertical*box_size/2 , maxHorizontal*box_size/2, maxVertical*box_size/2);
        bContX.addActionListener(this);
        bContDot = new JButton();
        bContDot.setIcon(iDotBox);
        bContDot.setBounds(maxHorizontal*box_size/2,maxVertical*box_size/2,maxHorizontal*box_size/2,maxVertical*box_size/2);
        bContDot.addActionListener(this);
        pControl.add(bContBlack);
        pControl.add(bContWhite);
        pControl.add(bContX);
        pControl.add(bContDot);
        pConfirm = new JPanel();
        pConfirm.setBounds(0,(maxVertical+Height)*box_size+1,(maxVertical+Width+3)*box_size,50);
        bCHECK= new JButton("Sprawdz");
        bCHECK.setBounds((Width+maxHorizontal)*box_size/2-20,(Height+maxVertical)*box_size+10, 57, 30);
        bCHECK.addActionListener(this);
        pConfirm.add(bCHECK);
        pConfirm.setBackground(Color.LIGHT_GRAY);
        fMain.add(pControl);
        fMain.add(uPanel);
        fMain.add(lPanel);
        fMain.add(pConfirm);
        fMain.addMouseListener(this);
        fMain.getContentPane().add(new MyCanvas());
        fMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fMain.setVisible(true);
    }
    
    public Boolean checkSolved()
    {
        for(int i=0;i<Height;i++)
        {
            for(int j=0;j<Width;j++)
            {
                if(Tab[i][j]!=PictureTab[i][j] && Tab[i][j]!=PictureTab[i][j]-2)
                    return false;
            }
        }
        return true;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        mouseX = e.getX()-3-maxHorizontal*box_size;
        mouseY = e.getY()-26-maxVertical*box_size;
        PictureTab[mouseY/box_size][mouseX/box_size]=mode;
        fMain.getContentPane().add(new MyCanvas());
        fMain.setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressedX=(e.getX()-3-maxHorizontal*box_size)/box_size;
        pressedY=(e.getY()-26-maxVertical*box_size)/box_size;
        if(pressedX<0)
            pressedX=0;
        if(pressedY<0)
            pressedY=0;
        if(pressedX>Width)
            pressedX=Width;
        if(pressedY>Height-1)
            pressedY=Height-1;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int relX,relY;
        relX=(e.getX()-3-maxHorizontal*box_size)/box_size;
        relY=(e.getY()-26-maxVertical*box_size)/box_size;
        if(relX<0)
            relX=0;
        if(relY<0)
            relY=0;
        if(relX>Width)
            relX=Width;
        if(relY>Height-1)
            relY=Height-1;
        if(relX==pressedX)
        {
            if(relY==pressedY)
                PictureTab[pressedY][pressedX]=mode;
            else
            {
                if(relY<pressedY)
                {
                    for(int i=relY;i<=pressedY;i++)
                    {
                        PictureTab[i][pressedX]=mode;
                    }
                }
                else
                {
                    for(int i=pressedY;i<=relY;i++)
                    {
                        PictureTab[i][pressedX]=mode;
                    }
                }
            }
            fMain.getContentPane().add(new MyCanvas());
            fMain.setVisible(true);
        }
        else
        {
            if(relY==pressedY)
            {
                if(relX<pressedX)
                {
                    for(int i=relX;i<=pressedX;i++)
                    {
                        PictureTab[pressedY][i]=mode;
                    }
                }
                else
                {
                    for(int i=pressedX;i<=relX;i++)
                    {
                        PictureTab[pressedY][i]=mode;
                    }
                }
                fMain.getContentPane().add(new MyCanvas());
                fMain.setVisible(true);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src==bContBlack)
            mode=1;
        if(src==bContWhite)
            mode=0;
        if(src==bContX)
            mode=2;
        if(src==bContDot)
            mode=3;
        if(src==bCHECK)
        {
            solved=checkSolved();
            if(solved)
            {
                JOptionPane.showMessageDialog(fMain,"GRATULACJE!!!","Display Message",JOptionPane.PLAIN_MESSAGE);
                System.exit(0);
            }
            
        }
    }
    
}

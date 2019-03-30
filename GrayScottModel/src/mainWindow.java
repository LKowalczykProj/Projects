import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class mainWindow implements ActionListener, MouseListener, ChangeListener {



    private JPanel mainPanel,controlPanel;
    private MyCanvas Canv = new MyCanvas();
    private JButton bAnim;
    private Timer timer;
    private BufferedImage GSModel;
    private JSlider sFeed,sKill;
    private JLabel lFeed,lKill;
    private JTextField tFeed,tKill;
    private double feedRatio,killRatio,diffA,diffB;
    private double[][][] base,next;

    public mainWindow()
    {
        base= new double[1000][500][2];
        next= new double[1000][500][2];
        for(int i=0;i<1000;i++)
        {
            for(int j=0;j<500;j++)
            {
                base[i][j][0]=1;
                base[i][j][1]=0;
                next[i][j][0]=1;
                next[i][j][1]=0;
            }
        }

        for(int i=0;i<100;i++)
        {
            for(int j=0;j<100;j++)
            {
                base[i+300][j+150][1]=1;
            }
        }

        feedRatio=0.055;
        killRatio=0.062;
        diffA=1;
        diffB=0.5;
        GSModel = new BufferedImage(1000,500,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<500;i++)
        {
            for(int j=0;j<1000;j++)
            {
                Color c=new Color(255,255,255);
                GSModel.setRGB(j,i,c.getRGB());
            }
        }

        JFrame mainFrame = new JFrame("Gray-Scott Model");
        mainFrame.setTitle("Gray-Scott Model");
        mainFrame.setSize(1000,700);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0,0,1000,500);
        Canv.setBounds(0,0,1000,500);
        Canv.addMouseListener(this);
        mainPanel.add(Canv);

        controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBounds(0,500,1000,200);

        bAnim = new JButton("Animate");
        bAnim.setBounds(80,70,100,40);
        bAnim.addActionListener(this);

        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel("0") );
        labelTable.put( new Integer( 25 ), new JLabel("0.25") );
        labelTable.put( new Integer( 50), new JLabel("0.5") );
        labelTable.put( new Integer( 75), new JLabel("0.75") );
        labelTable.put( new Integer( 100), new JLabel("1") );

        sFeed = new JSlider(JSlider.HORIZONTAL,0,100,0);
        sFeed.setBounds(270,40,600,50);
        sFeed.setMajorTickSpacing(25);
        sFeed.setPaintTicks(true);
        sFeed.setLabelTable( labelTable );
        sFeed.setPaintLabels(true);
        sFeed.addChangeListener(this);

        sKill = new JSlider(JSlider.HORIZONTAL,0,100,0);
        sKill.setBounds(270,100,600,50);
        sKill.setMajorTickSpacing(25);
        sKill.setPaintTicks(true);
        sKill.setLabelTable( labelTable );
        sKill.setPaintLabels(true);
        sKill.addChangeListener(this);

        lFeed = new JLabel("Feed");
        lFeed.setBounds(220,30,60,50);
        lKill = new JLabel("Kill");
        lKill.setBounds(220,90,60,50);

        tFeed = new JTextField("0");
        tFeed.setBounds(890,40,70,30);
        tFeed.setEditable(false);
        tKill = new JTextField("0");
        tKill.setBounds(890,100,70,30);
        tKill.setEditable(false);

        controlPanel.add(bAnim);
        controlPanel.add(sFeed);
        controlPanel.add(sKill);
        controlPanel.add(lFeed);
        controlPanel.add(lKill);
        controlPanel.add(tFeed);
        controlPanel.add(tKill);

        mainFrame.add(mainPanel);
        mainFrame.add(controlPanel);

        timer=new Timer();

        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src=e.getSource();
        if(src==bAnim)
        {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    update(GSModel);
                    Canv.repaint();
                    System.out.println("*Beep*");
                }
            },0,1000);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(e.getX() + " "+ e.getY());
        base[e.getX()][e.getY()][1]=1;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider src = (JSlider)e.getSource();
        if(src==sFeed)
        {
            if(!src.getValueIsAdjusting())
            {
                feedRatio=(double)src.getValue()/100;
                tFeed.setText(Double.toString(feedRatio));
            }
        }
        if(src==sKill)
        {
            if(!src.getValueIsAdjusting())
            {
                killRatio=(double)src.getValue()/100;
                tKill.setText(Double.toString(killRatio));
            }
        }

    }

    private class MyCanvas extends Canvas
    {
        @Override
        public void paint(Graphics g)
        {
            g.drawImage(GSModel,0,0,this);
        }
    }

    private void update(BufferedImage img)
    {
        for(int i=0;i<img.getHeight();i++)
        {
            for(int j=0;j<img.getWidth();j++)
            {
                double A=base[j][i][0],B=base[j][i][1];
                next[j][i][0]=A+(diffA*laplace(j,i,0))-A*B*B+feedRatio*(1-A);
                next[j][i][1]=B+(diffB*laplace(j,i,1))+A*B*B-(killRatio+feedRatio)*B;
                System.out.println("p "+next[j][i][0]+" "+ next[j][i][1]);
                int k=(int)(next[j][i][0]-next[j][i][1])*255;
                if(k<0)
                    k=0;
                if(k>255)
                    k=255;
                Color c = new Color(k,k,k);
                img.setRGB(j,i,c.getRGB());
            }
        }
        double[][][] temp;
        temp=base;
        base=next;
        next=temp;
    }

    private double laplace(int x,int y,int t)
    {
        double sum=0;
        sum+=base[x][y][t]*-1;
        if(x>0)
            sum+=base[x-1][y][t]*0.2;
        if(y>0)
            sum+=base[x][y-1][t]*0.2;
        if(x<999)
            sum+=base[x+1][y][t]*0.2;
        if(y<499)
            sum+=base[x][y+1][t]*0.2;
        if(x>0 && y>0)
            sum+=base[x-1][y-1][t]*0.05;
        if(x>0 && y<499)
            sum+=base[x-1][y+1][t]*0.05;
        if(x<999 && y>0)
            sum+=base[x+1][y-1][t]*0.05;
        if(x<999 && y<499)
            sum+=base[x+1][y+1][t]*0.05;

        return sum;
    }

}

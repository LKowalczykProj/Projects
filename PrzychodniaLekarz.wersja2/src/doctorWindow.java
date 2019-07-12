import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class doctorWindow extends JFrame{

    public JPanel visitPanel;
    public int current;
    public JButton bConfirm;
    public JLabel lDate;
    private DateFormat format;
    ArrayList<JTextField> visitArray = new ArrayList<JTextField>();
    //JButton okButton;
    public doctorWindow(DateFormat f)
    {

        format = f;
        setTitle("Przychodnia");
        current=0;
        setSize(650,700);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        visitPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout grd = new GridBagLayout();
        visitPanel.setLayout(grd);
        for(int i=0;i<20;i++)
        {
            int hour=8+i/2;
            String min;
            if(i%2==0)
                min=":00";
            else
                min=":30";
            JTextField iVisit = new JTextField("  Godz: "+Integer.toString(hour)+min);
            iVisit.setFont(new Font("Verdana",Font.BOLD,30));
            iVisit.setEnabled(false);
            iVisit.setDisabledTextColor(Color.BLACK);
            iVisit.setName(Integer.toString(i));
            iVisit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    super.mouseClicked(mouseEvent);
                    visitArray.get(current).setBackground(Color.WHITE);
                    current=Integer.parseInt(iVisit.getName());
                    visitArray.get(current).setBackground(Color.LIGHT_GRAY);
                }
            });
            visitArray.add(iVisit);
            gbc.gridx=0;
            gbc.gridy=i;
            gbc.ipadx=192;
            gbc.ipady=20;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            visitPanel.add(iVisit,gbc);
        }
        JScrollPane scroll =new JScrollPane(visitPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(15,0));
        scroll.setBounds(180,80,400,500);
        add(scroll);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setBounds(100,590,550,150);
        bConfirm = new JButton("OK");
        bConfirm.setBounds(200,25,150,40);
        bConfirm.setFont(new Font("Verdana",Font.BOLD,20));
        buttonPanel.add(bConfirm);
        add(buttonPanel);

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("img/reklama.gif"));
        } catch (IOException e) {
            System.out.println("Loading Error");
        }
        JPanel adPanel = new JPanel();
        adPanel.setLayout(null);
        adPanel.setBounds(20,130,img.getWidth()+10,img.getHeight()+10);
        adPanel.setBackground(Color.DARK_GRAY);
        JLabel adIcon = new JLabel(new ImageIcon(img));
        adIcon.setBounds(5,5,img.getWidth(),img.getHeight());
        adPanel.add(adIcon);
        add(adPanel);

        JPanel datePanel = new JPanel();
        datePanel.setLayout(null);
        datePanel.setBounds(0,0,650,100);
        //lDate przechowywuje wybraną przez nas date
        lDate = new JLabel(format.format(Calendar.getInstance().getTime()));
        lDate.setBounds(200,30,250,40);
        lDate.setFont(new Font("Verdana",Font.BOLD,30));
        lDate.setHorizontalAlignment(JLabel.CENTER);
        datePanel.add(lDate);

        JButton btnNewButton = new JButton("Ustaw date");
        btnNewButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                final JFrame f = new JFrame();
                lDate.setText(new DatePicker(f).setPickedDate());

            }
        });
        btnNewButton.setBounds(440,30,100,30);
        datePanel.add(btnNewButton);

        add(datePanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

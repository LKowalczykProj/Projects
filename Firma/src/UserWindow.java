import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserWindow {

    private JFrame mainframe;
    private User cUser;
    private JLabel lName;
    private JButton bLogout;

    public UserWindow()
    {
        int w=400,h=250;
        mainframe = new JFrame("Panel pracownika");
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainframe.setLocationRelativeTo(null);
        mainframe.setLayout(null);
        mainframe.setSize(w,h);

        JPanel mainpanel = new JPanel();
        mainpanel.setLayout(null);
        mainpanel.setBounds(0,0,w,h);
        mainframe.add(mainpanel);

        lName = new JLabel("Hello");
        lName.setFont(new Font("verdana",Font.BOLD,20));
        lName.setBounds(50,20,300,40);
        lName.setHorizontalAlignment(SwingConstants.CENTER);
        mainpanel.add(lName);

        JButton bCheck = new JButton("Sprawdz wplaty");
        bCheck.setFont(new Font("Verdana",Font.BOLD,20));
        bCheck.setBounds(50,80,300,50);
        bCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cUser.printTotal();
            }
        });
        mainpanel.add(bCheck);

        bLogout = new JButton("Wyloguj");
        bLogout.setFont(new Font("Verdana",Font.BOLD,20));
        bLogout.setBounds(50,150,300,50);
        mainpanel.add(bLogout);

        mainframe.setVisible(false);
    }

    public void setcUser(User cUser) {
        this.cUser = cUser;
        lName.setText("Witaj, "+cUser.getName()+"!");
    }

    public JFrame getFrame()
    {
        return mainframe;
    }

    public JButton getbLogout() {
        return bLogout;
    }
}

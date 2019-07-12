import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class UserWindow {

    private int id;
    private Connection myConn;
    private JFrame mainframe;
    private JButton bLogout,bCourse,bGrades;
    private JLabel lName;
    public UserWindow(Connection myConn)
    {
        this.id=-1;
        this.myConn = myConn;
        int w=300,h=400;
        mainframe = new JFrame("Panel Usera");
        mainframe.setSize(w,h);
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainframe.setResizable(false);
        mainframe.setLocationRelativeTo(null);
        mainframe.setLayout(null);

        JPanel mainpanel = new JPanel();
        mainpanel.setBounds(0,0,w,h);
        mainpanel.setLayout(null);

        lName = new JLabel("User");
        lName.setBounds(50,50,200,50);
        lName.setFont(new Font("Verdana",Font.BOLD,25));
        lName.setHorizontalAlignment(SwingConstants.CENTER);
        mainpanel.add(lName);

        bCourse = new JButton("Kursy");
        bCourse.setBounds(50,150,200,50);
        mainpanel.add(bCourse);

        bGrades = new JButton("Oceny");
        bGrades.setBounds(50,220,200,50);
        mainpanel.add(bGrades);

        bLogout = new JButton("Wyloguj");
        bLogout.setBounds(50,290,200,50);
        mainpanel.add(bLogout);

        mainframe.add(mainpanel);

        mainframe.setVisible(false);
    }

    public JFrame getFrame() { return mainframe; }

    public JButton getbLogout() { return bLogout; }

    public void setUser(int id,String Name)
    {
        this.id=id;
        lName.setText(Name);
    }

    public Connection getMyConn() {
        return myConn;
    }

    public JButton getbCourse() {
        return bCourse;
    }

    public JButton getbGrades() {
        return bGrades;
    }

    public int getId() {
        return id;
    }
}

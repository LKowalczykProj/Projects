import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterWindow {

    private JFrame mainframe;
    private JTextField tName,tSurname,tUname;
    private JPasswordField tPass,tPass2,tToken;
    public JButton bRegister,bBack;
    private boolean premium;
    public RegisterWindow()
    {
        mainframe = new JFrame("Rejstracja");
        mainframe.setLocationRelativeTo(null);
        mainframe.setLayout(null);
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        int w=400,h=400;
        mainframe.setSize(w,h);
        premium=false;

        JPanel mainpanel = new JPanel();
        mainpanel.setBounds(0,0,w,h);
        mainpanel.setLayout(null);

        JLabel lName = new JLabel("Imie:");
        lName.setBounds(50,80,100,30);
        mainpanel.add(lName);

        JLabel lSurname = new JLabel("Nazwisko:");
        lSurname.setBounds(50,120,100,30);
        mainpanel.add(lSurname);

        JLabel lUname = new JLabel("Login:");
        lUname.setBounds(50,160,100,30);
        mainpanel.add(lUname);

        JLabel lPass = new JLabel("Hasło:");
        lPass.setBounds(50,200,100,30);
        mainpanel.add(lPass);

        JLabel lPass2 = new JLabel("Potwierdzenie:");
        lPass2.setBounds(50,240,100,30);
        mainpanel.add(lPass2);

        JLabel lToken = new JLabel("Token:");
        lToken.setBounds(50,280,100,30);
        mainpanel.add(lToken);

        tName = new JTextField();
        tName.setBounds(150,80,200,30);
        mainpanel.add(tName);

        tSurname = new JTextField();
        tSurname.setBounds(150,120,200,30);
        mainpanel.add(tSurname);

        tUname = new JTextField();
        tUname.setBounds(150,160,200,30);
        mainpanel.add(tUname);

        tPass = new JPasswordField();
        tPass.setBounds(150,200,200,30);
        mainpanel.add(tPass);

        tPass2 = new JPasswordField();
        tPass2.setBounds(150,240,200,30);
        mainpanel.add(tPass2);

        tToken = new JPasswordField();
        tToken.setBounds(150,280,200,30);
        tToken.setEnabled(false);
        mainpanel.add(tToken);

        bRegister = new JButton("Zarejestruj");
        bRegister.setBounds(90,330,100,30);
        mainpanel.add(bRegister);

        bBack = new JButton("Cofnij");
        bBack.setBounds(210,330,100,30);
        mainpanel.add(bBack);

        ButtonGroup UserType = new ButtonGroup();

        JRadioButton rbStudent = new JRadioButton("Student",true);
        rbStudent.setBounds(90,30,100,30);
        rbStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tToken.setText("");
                tToken.setEnabled(false);
                premium=true;
            }
        });
        UserType.add(rbStudent);
        mainpanel.add(rbStudent);

        JRadioButton rbLecturer = new JRadioButton("Prowadzący",false);
        rbLecturer.setBounds(210,30,100,30);
        rbLecturer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tToken.setEnabled(true);
                premium=true;
            }
        });
        UserType.add(rbLecturer);
        mainpanel.add(rbLecturer);

        mainframe.add(mainpanel);

        mainframe.setVisible(false);
    }

    public JFrame getFrame()
    {
        return mainframe;
    }

    public String getUserame()
    {
        return tUname.getText();
    }

    public String getPass()
    {
        return new String(tPass.getPassword());
    }

    public boolean isPremium()
    {
        return premium;
    }

    public boolean verifyToken()
    {
        if(isPremium())
            return new String(tToken.getPassword()).equals("P2PP2W");
        return true;
    }

    public boolean verifyPassword()
    {
        return new String(tPass.getPassword()).equals(new String(tPass2.getPassword()));
    }

    public String getFullname()
    {
        return tName.getText()+" "+tSurname.getText();
    }

    public boolean emptyFields()
    {
        if(tUname.getText().isEmpty())
            return true;
        if(tName.getText().isEmpty())
            return true;
        if(tSurname.getText().isEmpty())
            return true;
        if(new String(tPass.getPassword()).isEmpty())
            return true;
        return false;
    }

    public void reset()
    {
        tUname.setText("");
        tSurname.setText("");
        tToken.setText("");
        tPass.setText("");
        tPass2.setText("");
        tName.setText("");
    }
}

import javax.swing.*;

public class LogWindow {

    private JFrame mainframe;
    private JButton bConfirm;
    private JTextField tUname;
    private JPasswordField tPass;

    public LogWindow()
    {
        mainframe = new JFrame("Logowanie");
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainframe.setLayout(null);
        mainframe.setResizable(false);
        mainframe.setLocationRelativeTo(null);
        int w=400,h=200;
        mainframe.setSize(w,h);

        JPanel mainpanel = new JPanel();
        mainpanel.setBounds(0,0,w,h);
        mainpanel.setLayout(null);

        bConfirm = new JButton("Zaloguj");
        bConfirm.setBounds(150,120,100,30);

        JLabel lUname = new JLabel("Login:");
        lUname.setBounds(70,30,60,30);

        JLabel lPass = new JLabel("Hasło:");
        lPass.setBounds(70,70,60,30);

        tUname = new JTextField();
        tUname.setBounds(140,30,190,30);

        tPass = new JPasswordField();
        tPass.setBounds(140,70,190,30);

        mainpanel.add(bConfirm);
        mainpanel.add(lUname);
        mainpanel.add(lPass);
        mainpanel.add(tUname);
        mainpanel.add(tPass);

        mainframe.add(mainpanel);

        mainframe.setVisible(true);
    }
    public JFrame getFrame()
    {
        return mainframe;
    }

    public String getUsername()
    {
        return tUname.getText();
    }

    public String getPass()
    {
        return new String(tPass.getPassword());
    }

    public void reset()
    {
        tUname.setText("");
        tPass.setText("");
    }

    public JButton getbConfirm() {
        return bConfirm;
    }
}

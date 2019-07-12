import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Controller {

    private Connection myConn;
    private LogWindow LW;
    private RegisterWindow RW;
    private UserWindow SW,PW;

    private class btnController implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) {
            Object src = ae.getSource();
            if(src==LW.bConfirm)
            {
                try {
                    String query = "select * from Users where Username=? and Password=?";
                    PreparedStatement mySt = myConn.prepareStatement(query);
                    mySt.setString(1,LW.getUsername());
                    mySt.setString(2,LW.getPass());
                    ResultSet rs=mySt.executeQuery();
                    if(rs.next())
                    {
                        if(rs.getString("Type").equals("Student")) {
                            SW.setUser(rs.getInt("id"),rs.getString("Fullname"));
                            SW.getFrame().setVisible(true);
                        }
                        else {
                            PW.setUser(rs.getInt("id"),rs.getString("Fullname"));
                            PW.getFrame().setVisible(true);
                        }
                        LW.getFrame().setVisible(false);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Zły login lub hasło", "InfoBox: " + "Błąd", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(src==LW.bRegister)
            {
                LW.getFrame().setVisible(false);
                RW.getFrame().setVisible(true);
            }
            if(src==RW.bRegister)
            {
                if(!RW.emptyFields() && RW.verifyPassword() && RW.verifyToken()) {

                    String query = "select * from Users where Username=?";
                    String query2 = "insert into Users "
                            + "(Username, Fullname, Password, Type)"
                            + "VALUES (?,?,?,?)";
                    try {
                        PreparedStatement ps = myConn.prepareStatement(query);
                        PreparedStatement ps2 = myConn.prepareStatement(query2);
                        ps.setString(1,RW.getUserame());
                        ResultSet rs = ps.executeQuery();
                        if(rs.next())
                        {
                            JOptionPane.showMessageDialog(null, "Podany login jest już zajęty", "InfoBox: " + "Bład", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else
                        {
                            String type;
                            if(RW.isPremium())
                                type="Lecturer";
                            else
                                type="Student";
                            ps2.setString(1,RW.getUserame());
                            ps2.setString(2,RW.getFullname());
                            ps2.setString(3,RW.getPass());
                            ps2.setString(4,type);
                            ps2.executeUpdate();
                            RW.reset();
                            RW.getFrame().setVisible(false);
                            LW.getFrame().setVisible(true);
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Niepoprawne dane", "InfoBox: " + "Błąd", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            if(src==RW.bBack)
            {
                RW.reset();
                LW.reset();
                RW.getFrame().setVisible(false);
                LW.getFrame().setVisible(true);
            }
            if(src==PW.getbLogout())
            {
                LW.reset();
                PW.getFrame().setVisible(false);
                LW.getFrame().setVisible(true);

            }
            if(src==SW.getbLogout())
            {
                LW.reset();
                SW.getFrame().setVisible(false);
                LW.getFrame().setVisible(true);
            }
        }
    }

    public Controller()
    {
        try {
            myConn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/UMCS?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "*888rootSQL");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        btnController controller = new btnController();
        LW = new LogWindow();
        RW = new RegisterWindow();
        PW = new LecturerWindow(myConn);
        SW = new StudentWindow(myConn);
        connectButtons(controller);
    }

    public void connectButtons(btnController b)
    {
        LW.bRegister.addActionListener(b);
        LW.bConfirm.addActionListener(b);
        RW.bRegister.addActionListener(b);
        RW.bBack.addActionListener(b);
        PW.getbLogout().addActionListener(b);
        SW.getbLogout().addActionListener(b);
    }
}

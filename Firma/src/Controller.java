import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Controller {

    private LogWindow LW;
    private UserWindow UW;
    private Connection myConn;
    private Company myCompany;
    private Accountant myAccountant;

    private class btnController implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object src = actionEvent.getSource();
            if(src==LW.getbConfirm())
            {
                User u = myCompany.findUser(LW.getUsername(),LW.getPass());
                if(u!=null) {
                    LW.getFrame().setVisible(false);
                    UW.setcUser(u);
                    UW.getFrame().setVisible(true);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Niepoprawne dane", "InfoBox: " + "Błąd", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            if(src==UW.getbLogout())
            {
                UW.getFrame().setVisible(false);
                LW.reset();
                LW.getFrame().setVisible(true);
            }
        }
    }

    public Controller()
    {
        btnController b = new btnController();
        myAccountant = new Accountant();
        myCompany = new Company();
        LW = new LogWindow();
        UW = new UserWindow();
        try {
            myConn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "*888rootSQL");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        loadUsers();
        BankAccount companyAccount = new BankAccount();
        myAccountant.addBankAccount(companyAccount);
        myAccountant.addCompany(myCompany);
        myCompany.addAccountant(myAccountant);
        connectButtons(b);
    }

    private void loadUsers()
    {
        try
        {
            String query = "Select * from Users";
            Statement st = myConn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()) {
                String type = rs.getString("Type");
                User u;
                if (type.equals("Programmer")) {
                    u = new Programmer(rs.getString("Name"), rs.getString("Username"), rs.getNString("Pass"));
                    myCompany.addUser(u);
                }
                if (type.equals("Analyst")) {
                    u = new Analyst(rs.getString("Name"), rs.getString("Username"), rs.getNString("Pass"));
                    myCompany.addUser(u);
                }
                if (type.equals("CEO")) {
                    u = new CEO(rs.getString("Name"), rs.getString("Username"), rs.getNString("Pass"));
                    myCompany.addUser(u);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void connectButtons(btnController b)
    {
        LW.getbConfirm().addActionListener(b);
        UW.getbLogout().addActionListener(b);
    }
}

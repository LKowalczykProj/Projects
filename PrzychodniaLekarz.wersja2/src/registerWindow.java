import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

public class registerWindow extends JFrame implements ActionListener {

    public JPanel mainPanel;
    private JTextField tPesel, tFName, tLName,tlAdress;
    private JPasswordField tPassword1, tPassword2;
    private JComboBox cUserType;
    public JButton bSubmit;

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    public registerWindow() {
        setTitle("Rejestracja");
        setSize(500,540);
        setLayout(null);
        setResizable(false);

        conn = databaseConn.db_connection();

        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,500,540);
        mainPanel.setLayout(null);

        JLabel lFName = new JLabel("Imię:");
        lFName.setBounds(30,15,440,30);
        tFName = new JTextField();
        tFName.setBounds(30,45,440,30);

        JLabel lLName = new JLabel("Nazwisko:");
        lLName.setBounds(30,75,440,30);
        tLName = new JTextField();
        tLName.setBounds(30,105,440,30);

        JLabel lPesel = new JLabel("PESEL:");
        lPesel.setBounds(30,135,440,30);
        tPesel = new JTextField();
        tPesel.setBounds(30,165,440,30);

        JLabel lAdress = new JLabel("Adres:");
        lAdress.setBounds(30,195,440,30);
        tlAdress = new JTextField();
        tlAdress.setBounds(30,215,440,30);

        JLabel lPassword1 = new JLabel("Hasło:");
        lPassword1.setBounds(30,255,440,30);
        tPassword1 = new JPasswordField();
        tPassword1.setBounds(30,285,440,30);

        JLabel lPassword2 = new JLabel("Powtórz hasło:");
        lPassword2.setBounds(30,315,440,30);
        tPassword2 = new JPasswordField();
        tPassword2.setBounds(30,345,440,30);

        JLabel lUserType = new JLabel("Typ użytkownika:");
        lUserType.setBounds(30,375,440,30);
        String[] userTypes = { "Pacjent", "Doktor","Aptekarz" };
        cUserType = new JComboBox(userTypes);
        cUserType.setEditable(false);
        cUserType.setSelectedIndex(0);
        cUserType.setBounds(30,405,440,30);
        cUserType.addActionListener(this);

        bSubmit = new JButton("Zarejestruj się");
        bSubmit.setBounds(30, 450, 440, 30);
        bSubmit.addActionListener(this);

        mainPanel.add(lFName);
        mainPanel.add(tFName);
        mainPanel.add(lLName);
        mainPanel.add(tLName);
        mainPanel.add(lPesel);
        mainPanel.add(tPesel);
        mainPanel.add(lAdress);
        mainPanel.add(tlAdress);
        mainPanel.add(lPassword1);
        mainPanel.add(tPassword1);
        mainPanel.add(lPassword2);
        mainPanel.add(tPassword2);
        mainPanel.add(lUserType);
        mainPanel.add(cUserType);
        mainPanel.add(bSubmit);
        add(mainPanel);

        setVisible(true);
    }

    public void actionPerformed (ActionEvent e) {
        if(e.getSource() == bSubmit) {

            if(tPesel.getText().trim().isEmpty() || tFName.getText().trim().isEmpty() || tLName.getText().trim().isEmpty() || tPassword1.getPassword().length == 0 ||
                    tPassword2.getPassword().length == 0) {
                JOptionPane.showMessageDialog(null,"Wypełnij wszystkie pola");
                return;
            }
            if (!Arrays.equals(tPassword1.getPassword(), tPassword2.getPassword())) {
                    JOptionPane.showMessageDialog(null,"Hasła muszą być takie same");
                    return;
                }
                if(tPesel.getText().length()!=11)
                {
                    JOptionPane.showMessageDialog(null,"Pesel musi mieć 11 znaków");
                    return;
                }
            //poprawnie wprowadzone dane do formularza

            String query = "select * from users where PESEL=?";
            try {
                pst = conn.prepareStatement(query);
                pst.setString(1, tPesel.getText());
                rs = pst.executeQuery();

                if(rs.next()) {
                    JOptionPane.showMessageDialog(null,"taki użytkownik już istnieje");
                    return;
                }
                //poprawne dane i brak duplikatu w bazie

                query = "INSERT INTO `users`(`id`, `pesel`, `first_name`, `last_name`, `password`, `user_type_id`, `adress`) VALUES (NULL,?,?,?,?,?,?)";
                String usertype = String.valueOf(cUserType.getSelectedItem());
                int utype = 0;

                switch (usertype) {
                    case "Pacjent":
                        utype = 2;
                        break;
                    case "Doktor":
                        utype = 1;
                        break;
                    case "Aptekarz":
                        utype = 0;
                        break;
                }
                try {
                    pst = conn.prepareStatement(query);
                    pst.setString(1, tPesel.getText());
                    pst.setString(2, tFName.getText());
                    pst.setString(3, tLName.getText());
                    pst.setString(4, new String(tPassword1.getPassword()));
                    pst.setInt(5, utype);
                    pst.setString(6,tlAdress.getText());

                    pst.executeUpdate();
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
                JOptionPane.showMessageDialog(null,"Dodano");
                dispose(); // Zamyka okno rejestracji
            }
            catch (Exception exc) {
                exc.printStackTrace();
            }
            // Teraz nowy użytkownik musi się normalnie zalogować w oknie logowania.
        }
    }
}

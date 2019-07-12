import entities.UsersEntity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class loginWindow extends JFrame implements ActionListener {

    public JPanel mainPanel;
    private JTextField tPesel;
    private JPasswordField tPassword;
    public JButton bSubmit, bRegistration;
    //dla wyszukania zalogowanego doktora
    private UserDAO userDAO = new UserDAO();
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;


    public loginWindow() {
        setTitle("Logowanie");
        setSize(500,285);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        conn = databaseConn.db_connection();

        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,500,255);
        mainPanel.setLayout(null);

        JLabel lPesel = new JLabel("PESEL:");
        lPesel.setBounds(30,15,440,30);
        tPesel = new JTextField();
        tPesel.setBounds(30,45,440,30);

        JLabel lPassword = new JLabel("Hasło:");
        lPassword.setBounds(30,75,440,30);
        tPassword = new JPasswordField();
        tPassword.setBounds(30,105,440,30);

        bSubmit = new JButton("Zaloguj się");
        bSubmit.setBounds(30, 150, 440, 30);
        bSubmit.addActionListener(this);

        bRegistration = new JButton("Zarejestruj się");
        bRegistration.setBounds(30, 195, 440, 30);
        bRegistration.addActionListener(this);

        mainPanel.add(lPesel);
        mainPanel.add(tPesel);
        mainPanel.add(lPassword);
        mainPanel.add(tPassword);
        mainPanel.add(bSubmit);
        mainPanel.add(bRegistration);
        add(mainPanel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed (ActionEvent e) {
        if(e.getSource() == bSubmit) {

            String query = "select * from users where PESEL=? and password=?";
            try {
                pst = conn.prepareStatement(query);
                pst.setString(1, tPesel.getText());
                pst.setString(2, new String(tPassword.getPassword()));
                rs = pst.executeQuery();

                if(rs.next()) {
                    int user_type_id = rs.getInt("user_type_id");
                    int user_id = rs.getInt("id");
                    switch (user_type_id) {
                        case 2:
                            System.out.println("logowanie pacjenta"); //chyba najprościej; w tym miejscu będzie ładowany odpowiedni moduł
                            patientWindow PW = new patientWindow(user_id);
                            dispose();
                            break;
                        case 1:
                            System.out.println("logowanie doktora: ");

                            UsersEntity zalogowanyDoktor = userDAO.findDoctorByPesel(tPesel.getText());
                            doctorController DC = new doctorController(zalogowanyDoktor);
                            dispose();
                            break;
                        case 0:
                            System.out.println("logowanie aptekarza");
                            SearchView mainView = new SearchView();
                            mainView.setVisible(true);
                            dispose();
                            break;
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null,"błędne dane logowania");
                }
            }
            catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        else if(e.getSource() == bRegistration) {
            registerWindow RW = new registerWindow();
        }
    }
}

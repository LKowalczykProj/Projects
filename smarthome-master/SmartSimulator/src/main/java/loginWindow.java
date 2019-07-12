import javax.swing.*;
import java.awt.*;

public class loginWindow {
    private JFrame loginFrame;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private controller c;


    public loginWindow() {
        loginFrame = new JFrame("SmartSimulator");
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);
        loginFrame.setLayout(null);
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginFrame.setSize(300,100);

        Container content = loginFrame.getContentPane();
        GroupLayout layout = new GroupLayout(content);
        content.setLayout(layout);

        JLabel loginLabel = new JLabel("Login:");
        loginField = new JTextField();
        JLabel passwordLabel = new JLabel("Hasło:");
        passwordField = new JPasswordField();
        loginButton = new JButton("Zaloguj");

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(loginLabel,50, 50, 50)
                    .addComponent(loginField))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(passwordLabel, 50, 50, 50)
                    .addComponent(passwordField))
                .addComponent(loginButton)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(loginLabel).addComponent(loginField))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(passwordLabel).addComponent(passwordField))
            .addComponent(loginButton)
        );

        loginButton.addActionListener(e -> login());

        loginFrame.setVisible(true);
    }

    private void login() {
        SmartHomeApiClient apiClient = new SmartHomeApiClient("http://73.ip-51-38-131.eu:8000/");
        if(apiClient.login(loginField.getText(), new String(passwordField.getPassword()))) {
            c = new controller(apiClient);
            loginFrame.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Niepoprawne dane użytkownika", "Błąd", JOptionPane.WARNING_MESSAGE);
        }
    }
}

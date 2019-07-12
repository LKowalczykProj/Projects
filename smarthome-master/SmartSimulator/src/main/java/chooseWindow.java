import com.google.common.reflect.TypeToken;

import javax.swing.*;
import java.lang.reflect.Type;
import java.util.List;

public class chooseWindow {

    private JFrame mainframe;
    private JButton bBuilder,bSimulator;
    private JLabel lHouse;
    private int houseId;

    private SmartHomeApiClient apiClient;

    public void setApiClient(SmartHomeApiClient apiClient) {
        this.apiClient = apiClient;
        Type userListType = new TypeToken<List<SmartHomeApiClient.User>>() {}.getType();
        List<SmartHomeApiClient.SmartHomeObject> users = apiClient.getList("User", userListType);
        SmartHomeApiClient.User user = (SmartHomeApiClient.User)users.get(0);
        lHouse.setText("User: " + user.username);

        Type houseListType = new TypeToken<List<SmartHomeApiClient.House>>() {}.getType();
        List<SmartHomeApiClient.SmartHomeObject> houses = apiClient.getList("House", houseListType);
        for(SmartHomeApiClient.SmartHomeObject o : houses) {
            SmartHomeApiClient.House house = (SmartHomeApiClient.House) o;
            if(house.owner == user.id)
                houseId = house.id;
        }
    }

    public chooseWindow()
    {
        mainframe = new JFrame("SmartSimulator");
        mainframe.setLocationRelativeTo(null);
        mainframe.setResizable(false);
        mainframe.setLayout(null);
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainframe.setSize(300,250);

        JPanel mainpanel = new JPanel();
        mainpanel.setBounds(0,0,300,250);
        mainpanel.setLayout(null);

        lHouse = new JLabel();
        lHouse.setBounds(50,30,200,30);

        mainpanel.add(lHouse);

        bBuilder = new JButton("Builder");
        bBuilder.setBounds(50,80,200,50);
        mainpanel.add(bBuilder);

        bSimulator = new JButton("Symulator");
        bSimulator.setBounds(50,150,200,50);
        mainpanel.add(bSimulator);

        mainframe.add(mainpanel);

        mainframe.setVisible(true);
    }

    public JButton getbBuilder() {
        return bBuilder;
    }

    public JButton getbSimulator() {
        return bSimulator;
    }

    public JFrame getMainframe() {
        return mainframe;
    }

    public int getHouseId() {
        return houseId;
    }
}

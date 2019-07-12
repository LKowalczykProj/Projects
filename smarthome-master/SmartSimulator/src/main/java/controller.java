import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class controller {

    private builderWindow BW;
    private chooseWindow CW;
    private simulatorWindow SW;
    private int house;

    private SmartHomeApiClient apiClient;

    public class btnController implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent ae) {

            Object src = ae.getSource();
            if(src==CW.getbBuilder())
            {
                house = CW.getHouseId();
                BW.loadMap(house);
                CW.getMainframe().setVisible(false);
                BW.getMainframe().setVisible(true);
            }
            if(src==CW.getbSimulator())
            {
                house = CW.getHouseId();
                SW.loadMap(house);
                CW.getMainframe().setVisible(false);
                SW.getMainframe().setVisible(true);

            }
            if(src==BW.getbSave())
            {
                BW.save();
                BW.getMainframe().setVisible(false);
                CW.getMainframe().setVisible(true);
            }
        }
    }

    public controller(SmartHomeApiClient client)
    {
        apiClient = client;
        CW = new chooseWindow();
        CW.setApiClient(apiClient);
        BW = new builderWindow();
        BW.setApiClient(apiClient);
        SW = new simulatorWindow();
        SW.setApiClient(apiClient);
        btnController btc = new btnController();
        connectButtons(btc);
    }

    private void connectButtons(btnController b)
    {
        BW.getbSave().addActionListener(b);
        CW.getbBuilder().addActionListener(b);
        CW.getbSimulator().addActionListener(b);
    }

}

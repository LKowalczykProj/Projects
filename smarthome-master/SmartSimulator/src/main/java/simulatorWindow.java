import com.google.common.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class simulatorWindow {

    private electricObject findElectricByName(ArrayList<electricObject> arr, String name) {
        for(electricObject o : arr) {
            if(o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }
    private roomObject findRoomByName(ArrayList<roomObject> arr, String name) {
        for(roomObject o : arr) {
            if(o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }
    private doorObject findDoorByName(ArrayList<doorObject> arr, String name) {
        for(doorObject o : arr) {
            if(o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    void updateParameter(String devName, String parameter, String value) {
        MqttMessage m = new MqttMessage();
        m.setPayload(value.getBytes());
        try {
            mqttClient.publish("dev/" + devName + "/value/" + parameter, m);
        } catch(MqttException e) {
            e.printStackTrace();
        }
    }

    private class SimulatorMqttCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {
            System.err.println("MQTT connection lost");
        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            System.out.println("MQTT " + s + new String(mqttMessage.getPayload()));
            String[] tok = s.split("/");
            // handle write command
            if(tok[0].equals("dev") && tok[2].equals("write") && tok[1].length() > 2) {
                switch(tok[1].substring(0, 2)) {
                    case "ro":
                        roomObject room = findRoomByName(roomList, tok[1]);
                        if(room != null && tok[3].equals("temperature")) {
                            room.setTemp(Double.parseDouble(new String(mqttMessage.getPayload())));
                            mainpanel.repaint();
                        }
                        break;
                    case "la":
                        electricObject lamp = findElectricByName(lampList, tok[1]);
                        if(lamp != null && tok[3].equals("power")) {
                            lamp.setState(mqttMessage.getPayload()[0] == '1');
                            mainpanel.repaint();
                        }
                        break;
                    case "rt":
                        electricObject rtv = findElectricByName(rtvList, tok[1]);
                        if(rtv != null && tok[3].equals("power")) {
                            rtv.setState(mqttMessage.getPayload()[0] == '1');
                            mainpanel.repaint();
                        }
                        break;
                    case "do":
                        doorObject door = findDoorByName(doorList, tok[1]);
                        if(door != null && tok[3].equals("locked")) {
                            door.setState(mqttMessage.getPayload()[0] == '1');
                            mainpanel.repaint();
                        }
                        break;
                }
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    }

    private ArrayList<roomObject> roomList;
    private ArrayList<electricObject> lampList;
    private ArrayList<electricObject> rtvList;
    private ArrayList<doorObject> doorList;
    private ArrayList<Dude> dudeList;
    private JFrame mainframe;
    private JPanel mainpanel;
    private int globalX, globalY, w, h, pressX, pressY,mode;
    private Dude selectedDude;
    private SmartHomeApiClient apiClient;
    private MqttClient mqttClient;

    public void setApiClient(SmartHomeApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public simulatorWindow() {
        mode=0;
        selectedDude = null;
        roomList = new ArrayList<>();
        lampList = new ArrayList<>();
        rtvList = new ArrayList<>();
        doorList = new ArrayList<>();
        dudeList = new ArrayList<>();
        globalX = 0;
        globalY = 0;
        pressX = 0;
        pressY = 0;
        mainframe = new JFrame("Simulator");
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        w = screenSize.width;
        h = screenSize.height;
        int h2=(int)Math.round(h*0.1);
        mainframe.setSize(w, h);
        mainframe.setResizable(false);
        mainframe.setLayout(null);
        mainframe.setFocusable(true);
        mainframe.requestFocusInWindow();
        mainframe.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int originX=selectedDude.getX(),originY=selectedDude.getY();
                boolean collision=false;
                boolean repaint=false;
                if(ke.getKeyCode()==KeyEvent.VK_UP)
                {
                    repaint=true;
                    int y=(selectedDude.getY()-10)/150;
                    if(selectedDude.getDirection().equals("horizontal"))
                        selectedDude.moveY(-10);
                    selectedDude.setDirection("horizontal");
                    if(y>(selectedDude.getY()-10)/150)
                        collision=checkCollision(true,false); //Collision with lower wall
                }
                if(ke.getKeyCode()==KeyEvent.VK_DOWN)
                {
                    repaint=true;
                    int y=(selectedDude.getY()+10)/150;
                    if(selectedDude.getDirection().equals("horizontal"))
                        selectedDude.moveY(10);
                    selectedDude.setDirection("horizontal");
                    if(y<(selectedDude.getY()+10)/150)
                        collision=checkCollision(false,false); //Collision with upper wall
                }
                if(ke.getKeyCode()==KeyEvent.VK_LEFT)
                {
                    repaint=true;
                    int x=(selectedDude.getX()-10)/150;
                    if(selectedDude.getDirection().equals("vertical"))
                        selectedDude.moveX(-10);
                    selectedDude.setDirection("vertical");
                    if(x>(selectedDude.getX()-10)/150)
                        collision=checkCollision(true,true); //Collision with right wall
                }
                if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
                {
                    repaint=true;
                    int x=(selectedDude.getX()+10)/150;
                        if(selectedDude.getDirection().equals("vertical"))
                        selectedDude.moveX(10);
                    selectedDude.setDirection("vertical");
                    if(x<(selectedDude.getX()+10)/150)
                        collision=checkCollision(false,true); //Collision with left wall
                }

                if(collision)
                {
                    selectedDude.setX(originX);
                    selectedDude.setY(originY);
                }
                System.out.println(selectedDude.getInside());
                if(repaint) {
                    checkInside();
                    mainpanel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        JPanel sidepanel = new JPanel();
        sidepanel.setBounds(0,h-h2,w,h2);
        sidepanel.setLayout(null);
        mainframe.add(sidepanel);

        ButtonGroup modes = new ButtonGroup();

        JRadioButton bTemp = new JRadioButton("Temp view",false);
        bTemp.setFont(new Font("Verdana",Font.PLAIN,15));
        bTemp.setBounds(460,10,200,30);
        bTemp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=1;
                mainpanel.repaint();
            }
        });
        modes.add(bTemp);
        sidepanel.add(bTemp);

        JRadioButton bLight = new JRadioButton("Light view",false);
        bLight.setFont(new Font("Verdana",Font.PLAIN,15));
        bLight.setBounds(240,10,200,30);
        bLight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=2;
                mainpanel.repaint();
            }
        });
        modes.add(bLight);
        sidepanel.add(bLight);

        JRadioButton bPlan = new JRadioButton("Plan view",true);
        bPlan.setBounds(20,10,200,30);
        bPlan.setFont(new Font("Verdana",Font.PLAIN,15));
        bPlan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=0;
                mainpanel.repaint();
            }
        });
        modes.add(bPlan);
        sidepanel.add(bPlan);

        JRadioButton bHumid = new JRadioButton("Humidity view",false);
        bHumid.setBounds(680,10,200,30);
        bHumid.setFont(new Font("Verdana",Font.PLAIN,15));
        bHumid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=3;
                mainpanel.repaint();
            }
        });
        modes.add(bHumid);
        sidepanel.add(bHumid);

        JButton bDude = new JButton("Spawn guy");
        bDude.setBounds(900,5,100,30);
        bDude.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Dude d = new Dude(75,75);
                selectedDude=d;
                dudeList.add(d);
                mainpanel.repaint();
            }
        });
        sidepanel.add(bDude);

        mainpanel = new myCanvas();
        mainpanel.setBounds(0, 0, w, h-h2);
        mainpanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int x = mouseEvent.getX() - globalX, y = mouseEvent.getY() - globalY;
                //System.out.println(x+" "+y);
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    pressX=mouseEvent.getX();
                    pressY=mouseEvent.getY();
                    if(mode==2) {
                        for (doorObject d : doorList) {
                            if(d.getVertical()) {
                                if (x >= d.getX()-10 && x <= d.getX()+9 && y >= d.getY()-20 && y <= d.getY()+19) {
                                    d.setState(!d.getState());
                                    updateParameter(d.getName(), "locked", d.getState() ? "1" : "0");
                                }
                            }
                            else
                            {
                                if (x >= d.getX()-20 && x <= d.getX()+19 && y >= d.getY()-10 && y <= d.getY()+9) {
                                    d.setState(!d.getState());
                                    updateParameter(d.getName(), "locked", d.getState() ? "1" : "0");
                                }
                            }
                        }
                        for (electricObject l : lampList) {
                            if (x >= l.getX()-10 && x <= l.getX() + 9 && y >= l.getY()-10 && y <= l.getY() + 9) {
                                l.setState(!l.getState());
                                updateParameter(l.getName(), "power", l.getState() ? "1" : "0");
                            }
                        }
                        for (Dude d: dudeList)
                        {
                            if (x >= d.getX()-10 && x <= d.getX() + 9 && y >= d.getY() && y-10 <= d.getY() + 9) {
                                selectedDude=d;
                            }
                        }
                        for (electricObject l : rtvList) {
                            if (x >= l.getX()-10 && x <= l.getX() + 9 && y >= l.getY()-10 && y <= l.getY() + 9) {
                                l.setState(!l.getState());
                                updateParameter(l.getName(), "power", l.getState() ? "1" : "0");
                            }
                        }
                    }
                    if(mode==1)
                    {
                        for(roomObject r:roomList)
                        {
                            if(x>=r.getX() && x<=r.getX()+149 && y>=r.getY() && y<=r.getY()+149)
                            {
                                r.setTemp(r.getTemp()+1);
                                updateParameter(r.getName(), "temperature", String.valueOf(r.getTemp()));
                            }
                        }
                        for (Dude d: dudeList)
                        {
                            if (x >= d.getX()-10 && x <= d.getX() + 9 && y >= d.getY()-10 && y <= d.getY() + 9) {
                                selectedDude=d;
                            }
                        }
                    }
                    if(mode==3)
                    {
                        for(roomObject r:roomList)
                        {
                            if(x>=r.getX() && x<=r.getX()+149 && y>=r.getY() && y<=r.getY()+149)
                            {
                                r.setHumid(r.getHumid()+1);
                                updateParameter(r.getName(), "humidity", String.valueOf(r.getHumid()));
                            }
                        }
                        for (Dude d: dudeList)
                        {
                            if (x >= d.getX()-10 && x <= d.getX() + 9 && y >= d.getY()-10 && y <= d.getY() + 9) {
                                selectedDude=d;
                            }
                        }
                    }
                }
                if(mouseEvent.getButton()==MouseEvent.BUTTON3)
                {
                    if(mode==1)
                    {
                        for(roomObject r:roomList)
                        {
                            if(x>=r.getX() && x<=r.getX()+149 && y>=r.getY() && y<=r.getY()+149)
                            {
                                r.setTemp(r.getTemp()-1);
                                updateParameter(r.getName(), "temperature", String.valueOf(r.getTemp()));
                            }
                        }
                    }
                    if(mode==3)
                    {
                        for(roomObject r:roomList)
                        {
                            if(x>=r.getX() && x<=r.getX()+149 && y>=r.getY() && y<=r.getY()+149)
                            {
                                r.setHumid(r.getHumid()-1);
                                updateParameter(r.getName(), "humidity", String.valueOf(r.getHumid()));
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                pressY = 0;
                pressX = 0;
                mainpanel.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        mainpanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {

                globalX += mouseEvent.getX() - pressX;
                pressX = mouseEvent.getX();
                globalY += mouseEvent.getY() - pressY;
                pressY = mouseEvent.getY();
                if (globalX > 0)
                    globalX = 0;
                if (globalY > 0)
                    globalY = 0;
                mainpanel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });


        mainframe.add(mainpanel);
        mainframe.setVisible(false);

        // MQTT setup
        try {
            mqttClient = new MqttClient("tcp://51.38.131.73:1883", MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("main_controller");
            options.setPassword("Cae9wei7mo".toCharArray());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            mqttClient.setCallback(new SimulatorMqttCallback());
            mqttClient.connect(options);
            mqttClient.subscribe("dev/+/write/+");
        } catch (MqttException e) {
            JOptionPane.showMessageDialog(mainframe, "Nie można połączyć się z serwerem MQTT",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    class myCanvas extends JPanel {

        @Override
        public void paint(Graphics g) {
            //Draw Grid
            if(mode==0) //Plan Mode
                g.setColor(Color.white);
            else
                g.setColor(new Color(50,50,50));
            g.fillRect(0, 0, getWidth(), getHeight());
            if(mode==0) {
                int startX = globalX % 150, startY = globalY % 150;
                g.setColor(Color.LIGHT_GRAY);
                while (startX < w) {
                    g.drawLine(startX, 0, startX, h);
                    startX += 150;
                }
                while (startY < h) {
                    g.drawLine(0, startY, w, startY);
                    startY += 150;
                }
                //Draw Rooms
                for (roomObject r : roomList) {
                    if(r.getPeople())
                        g.setColor(Color.GREEN);
                    else
                        g.setColor(Color.WHITE);
                    g.fillRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                }
                //Draw Lamps
                for (electricObject l : lampList) {
                    g.setColor(Color.WHITE);
                    g.fillOval(l.getX() + globalX - 10, l.getY() + globalY - 10, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawOval(l.getX() + globalX - 10, l.getY() + globalY - 10, 20, 20);
                }
                //Draw RTVs
                for (electricObject r : rtvList) {
                    g.setColor(Color.WHITE);
                    g.fillRect(r.getX() + globalX - 10, r.getY() + globalY - 10, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX - 10, r.getY() + globalY - 10, 20, 20);
                }
                //Draw Doors
                for (doorObject d : doorList) {
                    g.setColor(Color.WHITE);
                    if (d.getVertical()) {
                        g.fillRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                        g.setColor(Color.BLACK);
                        g.drawRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                    } else {
                        g.fillRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                        g.setColor(Color.BLACK);
                        g.drawRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                    }
                }
                g.setColor(Color.BLACK);
                g.setFont(new Font("Monospaced",Font.BOLD,20));

                for(roomObject r: roomList)
                    g.drawString(r.getName(),r.getX()+globalX+10,r.getY()+globalY+25);

                g.setFont(new Font("Monospaced",Font.BOLD,15));

                for(electricObject l: lampList)
                    g.drawString(l.getName(),l.getX()-10+globalX,l.getY()+globalY-20);

                for(electricObject r: rtvList)
                    g.drawString(r.getName(),r.getX()-10+globalX,r.getY()+globalY-20);

                for(doorObject d:doorList)
                    g.drawString(d.getName(),d.getX()-20+globalX,d.getY()+globalY-20);
            }
            if(mode==1) //Temp Mode
            {
                //Draw Rooms
                Color cold = new Color(10,30,255);
                double ct = -20;
                //double ht=30;
                Color hot = new Color(255,30,15);
                for (roomObject r : roomList) {
                    double diff=(r.getTemp()-ct)/50;
                    int R=(int)Math.round(10+245*diff);
                    int G=30;
                    int B=(int)Math.round(255-240*diff);
                    g.setColor(new Color(R,G,B));
                    g.fillRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                }
                //Draw RTVs
                for (electricObject r : rtvList) {
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX - 10, r.getY() + globalY - 10, 20, 20);
                }
                //Draw Dudes
                for (Dude d: dudeList)
                {
                    //new Color(200,120,70)
                    g.setColor(new Color(240,200,150));
                    if(d.getDirection().equals("vertical"))
                    {
                        g.fillOval(d.getX()+globalX-5,d.getY()+globalY+5,10,10);
                        g.fillOval(d.getX()+globalX-5,d.getY()+globalY-15,10,10);
                        g.setColor(Color.BLACK);
                        g.drawOval(d.getX()+globalX-5,d.getY()+globalY+5,10,10);
                        g.drawOval(d.getX()+globalX-5,d.getY()+globalY-15,10,10);
                    }
                    else
                    {
                        g.fillOval(d.getX()+globalX+5,d.getY()+globalY-5,10,10);
                        g.fillOval(d.getX()+globalX-15,d.getY()+globalY-5,10,10);
                        g.setColor(Color.BLACK);
                        g.drawOval(d.getX()+globalX+5,d.getY()+globalY-5,10,10);
                        g.drawOval(d.getX()+globalX-15,d.getY()+globalY-5,10,10);
                    }
                    g.setColor(new Color(240,200,150));
                    g.fillOval(d.getX()+globalX-10,d.getY()+globalY-10,20,20);
                    g.setColor(Color.BLACK);
                    g.drawOval(d.getX()+globalX-10,d.getY()+globalY-10,20,20);
                }
                //Draw Lamps
                for (electricObject l : lampList) {
                    g.setColor(Color.BLACK);
                    g.drawOval(l.getX() + globalX - 10, l.getY() + globalY - 10, 20, 20);
                }
                //Draw Doors
                for (doorObject d : doorList) {
                    g.setColor(new Color(180, 120, 50));
                    if(d.getState())
                    {
                        if (d.getVertical()) {
                            g.fillRect(d.getX() + globalX - 25, d.getY() + globalY - 15, 30, 10);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 25, d.getY() + globalY - 15, 30, 10);
                        } else {
                            g.fillRect(d.getX() + globalX +5, d.getY() + globalY - 25, 10, 30);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX +5, d.getY() + globalY - 25, 10, 30);
                        }
                    }
                    else {
                        if (d.getVertical()) {
                            g.fillRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                        } else {
                            g.fillRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                        }
                    }
                }
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced",Font.BOLD,30));

                for(roomObject r: roomList)
                    g.drawString(Double.toString(r.getTemp())+"C",r.getX()+globalX+10,r.getY()+globalY+25);

                g.setFont(new Font("Verdana",Font.PLAIN,20));
                int h2=(int)(h*0.03);
                int w2=(int)(w*0.05);
                for(int i=0;i<=50;i++)
                {
                    if(i%10==0) {
                        g.setColor(Color.WHITE);
                        g.drawString(String.valueOf(i -20), w - w2 - 50, h2 + 15 * (i+1));
                    }
                    g.setColor(new Color(Math.round(10+245*i/50),30,Math.round(255-240*i/50)));
                    g.fillRect(w-w2-15,h2+15*i,15,15);
                }
            }
            if(mode==2) //Light mode
            {
                Color dark = new Color(50,50,50);
                Color bright = new Color(240,240,220);
                Color on = new Color(50,200,0);
                Color off = new Color(150,0,0);
                //Draw Rooms
                for (roomObject r : roomList) {
                    double dim = 0;
                    for(electricObject l:lampList)
                    {
                        if(l.getRoomId()==r.getId() && l.getState())
                            dim+=0.3*l.getDim();
                    }
                    if(dim>1)
                        dim=1;
                    g.setColor(new Color((int)Math.round(50+190*dim),(int)Math.round(50+190*dim),(int)Math.round(50+130*dim)));
                    g.fillRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                }
                //Draw RTVs
                for (electricObject r : rtvList) {
                    if(r.getState())
                        g.setColor(on);
                    else
                        g.setColor(off);
                    g.fillRect(r.getX() + globalX - 10, r.getY() + globalY - 10, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX - 10, r.getY() + globalY - 10, 20, 20);
                }
                //Draw Dudes
                for (Dude d: dudeList)
                {
                    //new Color(200,120,70)
                    g.setColor(new Color(240,200,150));
                    if(d.getDirection().equals("vertical"))
                    {
                        g.fillOval(d.getX()+globalX-5,d.getY()+globalY+5,10,10);
                        g.fillOval(d.getX()+globalX-5,d.getY()+globalY-15,10,10);
                        g.setColor(Color.BLACK);
                        g.drawOval(d.getX()+globalX-5,d.getY()+globalY+5,10,10);
                        g.drawOval(d.getX()+globalX-5,d.getY()+globalY-15,10,10);
                    }
                    else
                    {
                        g.fillOval(d.getX()+globalX+5,d.getY()+globalY-5,10,10);
                        g.fillOval(d.getX()+globalX-15,d.getY()+globalY-5,10,10);
                        g.setColor(Color.BLACK);
                        g.drawOval(d.getX()+globalX+5,d.getY()+globalY-5,10,10);
                        g.drawOval(d.getX()+globalX-15,d.getY()+globalY-5,10,10);
                    }
                    g.setColor(new Color(240,200,150));
                    g.fillOval(d.getX()+globalX-10,d.getY()+globalY-10,20,20);
                    g.setColor(Color.BLACK);
                    g.drawOval(d.getX()+globalX-10,d.getY()+globalY-10,20,20);
                }
                //Draw Lamps
                for (electricObject l : lampList) {
                    if(l.getState())
                        g.setColor(bright);
                    else
                        g.setColor(dark);
                    g.fillOval(l.getX() + globalX - 10, l.getY() + globalY - 10, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawOval(l.getX() + globalX - 10, l.getY() + globalY - 10, 20, 20);
                }
                //Draw Doors
                for (doorObject d : doorList) {
                    g.setColor(new Color(160, 100, 30));
                    if(d.getState())
                    {
                        if (d.getVertical()) {
                            g.fillRect(d.getX() + globalX - 25, d.getY() + globalY - 15, 30, 10);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 25, d.getY() + globalY - 15, 30, 10);
                        } else {
                            g.fillRect(d.getX() + globalX +5, d.getY() + globalY - 25, 10, 30);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX +5, d.getY() + globalY - 25, 10, 30);
                        }
                    }
                    else {
                        if (d.getVertical()) {
                            g.fillRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                        } else {
                            g.fillRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                        }
                    }
                }
            }
            if(mode==3) //Humidity Mode
            {
                //Draw Rooms
                Color dry = new Color(0,255,0);
                double ct = 0;
                //double ht=70;
                Color wet = new Color(0,100,0);
                for (roomObject r : roomList) {
                    double diff=(r.getHumid()-ct)/100;
                    int R=0;
                    int G=(int)Math.round(255-155*diff);
                    int B=0;
                    g.setColor(new Color(R,G,B));
                    g.fillRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX, r.getY() + globalY, 150, 150);
                }
                //Draw RTVs
                for (electricObject r : rtvList) {
                    g.setColor(Color.BLACK);
                    g.drawRect(r.getX() + globalX - 10, r.getY() + globalY - 10, 20, 20);
                }
                //Draw Dudes
                for (Dude d: dudeList)
                {
                    //new Color(200,120,70)
                    g.setColor(new Color(240,200,150));
                    if(d.getDirection().equals("vertical"))
                    {
                        g.fillOval(d.getX()+globalX-5,d.getY()+globalY+5,10,10);
                        g.fillOval(d.getX()+globalX-5,d.getY()+globalY-15,10,10);
                        g.setColor(Color.BLACK);
                        g.drawOval(d.getX()+globalX-5,d.getY()+globalY+5,10,10);
                        g.drawOval(d.getX()+globalX-5,d.getY()+globalY-15,10,10);
                    }
                    else
                    {
                        g.fillOval(d.getX()+globalX+5,d.getY()+globalY-5,10,10);
                        g.fillOval(d.getX()+globalX-15,d.getY()+globalY-5,10,10);
                        g.setColor(Color.BLACK);
                        g.drawOval(d.getX()+globalX+5,d.getY()+globalY-5,10,10);
                        g.drawOval(d.getX()+globalX-15,d.getY()+globalY-5,10,10);
                    }
                    g.setColor(new Color(240,200,150));
                    g.fillOval(d.getX()+globalX-10,d.getY()+globalY-10,20,20);
                    g.setColor(Color.BLACK);
                    g.drawOval(d.getX()+globalX-10,d.getY()+globalY-10,20,20);
                }
                //Draw Lamps
                for (electricObject l : lampList) {
                    g.setColor(Color.BLACK);
                    g.drawOval(l.getX() + globalX - 10, l.getY() + globalY - 10, 20, 20);
                }
                //Draw Doors
                for (doorObject d : doorList) {
                    g.setColor(new Color(180, 120, 50));
                    if(d.getState())
                    {
                        if (d.getVertical()) {
                            g.fillRect(d.getX() + globalX - 25, d.getY() + globalY - 15, 30, 10);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 25, d.getY() + globalY - 15, 30, 10);
                        } else {
                            g.fillRect(d.getX() + globalX +5, d.getY() + globalY - 25, 10, 30);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX +5, d.getY() + globalY - 25, 10, 30);
                        }
                    }
                    else {
                        if (d.getVertical()) {
                            g.fillRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                        } else {
                            g.fillRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                            g.setColor(Color.BLACK);
                            g.drawRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                        }
                    }
                }
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced",Font.BOLD,25));

                for(roomObject r: roomList)
                    g.drawString(Double.toString(r.getHumid())+"%",r.getX()+globalX+10,r.getY()+globalY+25);

                g.setFont(new Font("Verdana",Font.PLAIN,20));
                int h2=(int)(h*0.03);
                int w2=(int)(w*0.05);
                for(int i=0;i<=50;i++)
                {
                    if(i%10==0) {
                        g.setColor(Color.WHITE);
                        g.drawString(String.valueOf(2*i), w - w2 - 60, h2 + 15 * (i+1));
                    }
                    g.setColor(new Color(0,Math.round(255-155*i/50),0));
                    g.fillRect(w-w2-15,h2+15*i,15,15);
                }
            }
            mainframe.requestFocusInWindow();
        }
    }

    public JFrame getMainframe() {
        return mainframe;
    }

    public void loadMap(int house)
    {
        try {
            Type mapListType = new TypeToken<List<SmartHomeApiClient.MapEntry>>() {}.getType();
            List<SmartHomeApiClient.SmartHomeObject> entries = apiClient.getList("Map", mapListType);

            for(SmartHomeApiClient.SmartHomeObject o : entries)
            {
                SmartHomeApiClient.MapEntry entry = (SmartHomeApiClient.MapEntry)o;
                if(entry.house != house)
                    continue;

                char type = entry.type.charAt(0);
                if(type=='r') {
                    roomObject r = new roomObject(entry.posX, entry.posY, entry.name);
                    r.setId(roomList.size());
                    roomList.add(r);
                }
                if(type=='l') {
                    electricObject l = new electricObject(entry.posX, entry.posY, entry.name);
                    for (roomObject r: roomList) {
                        if (r.getX()+150 >= l.getX() && r.getX() <= l.getX() && r.getY()+150 >= l.getY() && r.getY() <= l.getY() + 19) {
                            l.setRoomId(r.getId());
                        }
                    }
                    l.setId(lampList.size());
                    lampList.add(l);
                }
                if(type=='x') {
                    electricObject r = new electricObject(entry.posX, entry.posY, entry.name);
                    r.setId(rtvList.size());
                    rtvList.add(r);
                }
                if(type=='h' || type=='v') {
                    doorObject d = new doorObject(entry.posX, entry.posY, entry.name);
                    d.setVertical(type=='v');
                    d.setId(doorList.size());
                    connect(d);
                    doorList.add(d);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mainpanel.repaint();
    }

    private boolean checkCollision(boolean neg,boolean xColl)
    {
        int doorOffX=0,doorOffY=0,dudeOff,roomOffX=0,roomOffY=0,X=0,Y=0;
        if(neg)
        {
            if(xColl)
            {
                X=20;
                doorOffX=1;
                roomOffX=1;
            }
            else
            {
                Y=20;
                doorOffY=1;
                roomOffY=1;
            }
            dudeOff=-10;
        }
        else
        {
            if(xColl) {
                roomOffX = -1;
                X=-20;
            }
            else {
                Y=20;
                roomOffY = -1;
            }
            dudeOff=10;
        }
        System.out.println("Collision");
        for (roomObject r : roomList) {
            if(((selectedDude.getX()+dudeOff)/150+roomOffX==r.getX()/150 && (selectedDude.getY()+dudeOff)/150+roomOffY==r.getY()/150) ||
                    ((selectedDude.getX()+dudeOff)/150==r.getX()/150 && (selectedDude.getY()+dudeOff)/150==r.getY()/150))
            {
                for(doorObject d:doorList)
                {
                    if((selectedDude.getX()+dudeOff)/150+doorOffX==d.getX()/150 && (selectedDude.getY()+dudeOff)/150+doorOffY==d.getY()/150 && d.getVertical()==xColl && d.getState())
                    {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void checkInside()
    {
        for(Dude d:dudeList)
            d.setInside(-1);
        for(int i=0;i<roomList.size();i++)
        {
            roomObject r = roomList.get(i);
            boolean people = false;
            for(Dude d:dudeList)
            {
                if(d.getX()+10>=r.getX() && d.getX()-9<=r.getX()+149 && d.getY()+10>=r.getY() && d.getY()-9<=r.getY()+149)
                {
                    people = true;
                    d.setInside(i);
                }
            }
            r.setPeople(people);
            if(r.isPeopleChanged())
                updateParameter(r.getName(), "detected", r.getPeople() ? "1" : "0");
        }
    }

    private void connect(doorObject d)
    {
        for(roomObject r: roomList)
        {
            if(d.getVertical())
            {
                if(r.getX()==d.getX() && r.getY()==d.getY()-75)
                {
                    d.setD2(r.getName());
                }
                if(r.getX()+150==d.getX() && r.getY()==d.getY()-75)
                {
                    d.setD1(r.getName());
                }
            }
            else
            {
                if(r.getX()==d.getX()-75 && r.getY()==d.getY())
                {
                    d.setD2(r.getName());
                }
                if(r.getX()==d.getX()-75 && r.getY()+150==d.getY())
                {
                    d.setD1(r.getName());
                }
            }
        }
    }
}

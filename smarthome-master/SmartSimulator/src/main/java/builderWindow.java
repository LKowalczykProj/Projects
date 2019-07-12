import com.google.common.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
    Mode:
    0 - Move (default)
    1 - Add Room
    2 - Add Lamp
    3 - Add RTV
    4 - Add Door
 */


public class builderWindow {

    private ArrayList<roomObject> roomList;
    private ArrayList<electricObject> lampList;
    private ArrayList<electricObject> rtvList;
    private ArrayList<doorObject> doorList;
    private JFrame mainframe;
    private JPanel mainpanel;
    private int mode,globalX,globalY,w,h,pressX,pressY;
    private int rtvC,roomC,lampC,doorC;
    private electricObject draggedObject;
    private boolean dragEnabled;
    private JButton bSave;
    private int houseId;
    private SmartHomeApiClient apiClient;

    public void setApiClient(SmartHomeApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public builderWindow()
    {
        rtvC=0;
        lampC=0;
        doorC=0;
        roomC=0;
        draggedObject = null;
        dragEnabled=true;
        roomList = new ArrayList<>();
        lampList = new ArrayList<>();
        rtvList = new ArrayList<>();
        doorList = new ArrayList<>();
        mode = 0;
        globalX=0;
        globalY=0;
        pressX=0;
        pressY=0;
        mainframe = new JFrame("Builder");
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        w = screenSize.width;
        h = screenSize.height;
        int w2=(int)Math.round(w*0.1);
        mainframe.setSize(w,h);
        mainframe.setResizable(false);
        mainframe.setLayout(null);
        JPanel sidepanel = new JPanel();
        sidepanel.setBounds(0,0,w2,h);
        sidepanel.setLayout(null);

        JButton bRoom = new JButton("Room");
        bRoom.setBounds(20,20,w2-40,100);
        bRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=1;
            }
        });
        sidepanel.add(bRoom);

        JButton bDoor = new JButton("Door");
        bDoor.setBounds(20,140,w2-40,100);
        bDoor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=4;
            }
        });
        sidepanel.add(bDoor);

        JButton bLamp = new JButton("Lamp");
        bLamp.setBounds(20,260,w2-40,100);
        bLamp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=2;
            }
        });
        sidepanel.add(bLamp);

        JButton bRTV = new JButton("RTV");
        bRTV.setBounds(20,380,w2-40,100);
        bRTV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=3;
            }
        });
        sidepanel.add(bRTV);

        JButton bMove = new JButton("Move");
        bMove.setBounds(20,500,w2-40,100);
        bMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mode=0;
            }
        });
        sidepanel.add(bMove);

        bSave = new JButton("Save");
        bSave.setBounds(20,620,w2-40,100);
        sidepanel.add(bSave);

        mainpanel = new myCanvas();
        mainpanel.setBounds(w2,0,w-w2,h);
        mainpanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int x=mouseEvent.getX()-globalX,y=mouseEvent.getY()-globalY;
                //System.out.println(x+" "+y);
                if(mouseEvent.getButton()==MouseEvent.BUTTON1) {
                    if (mode==0)
                    {
                        int bg=0;
                        for(electricObject l:lampList)
                        {
                            if(x>=l.getX()-15 && x<=l.getX()+14 && y>=l.getY()-15 && y<=l.getY()+14)
                            {
                                draggedObject = l;
                            }
                        }
                        if(draggedObject==null)
                        {
                            for(electricObject r:rtvList)
                            {
                                if(x>=r.getX()-15 && x<=r.getX()+14 && y>=r.getY()-15 && y<=r.getY()+14)
                                {
                                    draggedObject = r;
                                }
                            }
                        }
                        if(draggedObject==null)
                        {
                            for(roomObject r: roomList)
                            {
                                if(x>=r.getX() && x<=r.getX()+149 && y>=r.getY() && y<=r.getY()+149)
                                {
                                    dragEnabled=false;
                                }
                            }
                        }
                        if(dragEnabled)
                        {
                            pressX=x+globalX;
                            pressY=y+globalY;
                        }
                    }
                    if (mode == 1) {
                        roomObject r = new roomObject(x-x%150, y-y%150,"room"+roomC);
                        if(checkDupes(r.getX(),r.getY(),true)) {
                            roomList.add(r);
                            roomC+=1;
                        }
                    }
                    if (mode == 2)
                    {
                        electricObject l = new electricObject(x,y,"lamp"+lampC);
                        if(x%150<10)
                            l.move(10-x%150, 0);
                        if(x%150>140)
                            l.move(140-x%150, 0);
                        if(y%150<10)
                            l.move(0,10-y%150);
                        if(y%150>140)
                            l.move(0,140-y%150);
                        lampList.add(l);
                        lampC+=1;
                    }
                    if (mode == 3)
                    {
                        electricObject r = new electricObject(x,y,"rtv"+rtvList.size());
                        if(x%150<10)
                            r.move(10-x%150,0);
                        if(x%150>140)
                            r.move(140-x%150,0);
                        if(y%150<10)
                            r.move(0,10-y%150);
                        if(y%150>140)
                            r.move(0,140-y%150);
                        rtvList.add(r);
                        rtvC+=1;
                    }
                    if (mode == 4)
                    {
                        boolean edge=false;
                        doorObject d = new doorObject(x,y,"door"+doorList.size());
                        if(x%150>130)
                        {
                            d.moveTo(x-x%150+150,y-y%150+75);
                            edge=true;
                            connect(d);
                            if(!d.getD1().equals(d.getD2()) && checkDupes(d.getX(),d.getY(),false))
                            {
                                doorList.add(d);
                                doorC+=1;
                            }
                        }
                        if(x%150<20 && !edge)
                        {
                            d.moveTo(x-x%150,y-y%150+75);
                            edge=true;
                            connect(d);
                            if(!d.getD1().equals(d.getD2()) && checkDupes(d.getX(),d.getY(),false)) {
                                doorList.add(d);
                                doorC+=1;
                            }
                        }
                        if(y%150>130 && !edge)
                        {
                            d.moveTo(x-x%150+75,y-y%150+150);
                            d.setVertical(false);
                            edge=true;
                            connect(d);
                            if(!d.getD1().equals(d.getD2()) && checkDupes(d.getX(),d.getY(),false)) {
                                doorList.add(d);
                                doorC+=1;
                            }
                        }
                        if(y%150<20 && !edge)
                        {
                            d.moveTo(x-x%150+75,y-y%150);
                            d.setVertical(false);
                            edge=true;
                            connect(d);
                            if(!d.getD1().equals(d.getD2()) && checkDupes(d.getX(),d.getY(),false))
                            {
                                doorList.add(d);
                                doorC+=1;
                            }
                        }
                    }
                }
                if(mouseEvent.getButton()==MouseEvent.BUTTON3)
                {
                    int delId=-1;
                    if(mode==1) {
                        for (int i=0;i<roomList.size();i++) {
                            roomObject r=roomList.get(i);
                            if (x >= r.getX() && x <= r.getX() + 150 && y >= r.getY() && y <= r.getY() + 150)
                                delId=i;
                        }
                        if(delId>=0) {
                            int k=doorList.size(),i=0;
                            while(i<k)
                            {
                                doorObject d = doorList.get(i);
                                if(d.getD1().equals(roomList.get(delId).getName()))
                                {
                                    if(d.getD2().equals("Null"))
                                    {
                                        doorList.remove(i);
                                        i--;
                                        k--;
                                    }
                                    else
                                    {
                                        d.setD1("Null");
                                    }
                                }
                                if(d.getD2().equals(roomList.get(delId).getName()))
                                {
                                    if(d.getD1().equals("Null"))
                                    {
                                        doorList.remove(i);
                                        i--;
                                        k--;
                                    }
                                    else
                                    {
                                        d.setD2("Null");
                                    }
                                }
                                i++;
                            }
                            roomList.remove(delId);
                        }
                    }
                    if(mode==2) {
                        for (int i=0;i<lampList.size();i++) {
                            electricObject r = lampList.get(i);
                            if (x >= r.getX()-10 && x <= r.getX() + 9 && y >= r.getY()-10 && y <= r.getY()+9)
                                delId=i;
                        }
                        if(delId>=0)
                            lampList.remove(delId);
                    }
                    if(mode==3) {
                        for (int i=0;i<rtvList.size();i++) {
                            electricObject r = rtvList.get(i);
                            if (x >= r.getX()-10 && x <= r.getX() + 9 && y >= r.getY()-10 && y <= r.getY() + 9)
                                delId=i;
                        }
                        if(delId>=0)
                            rtvList.remove(delId);
                    }
                    if(mode==4) {
                        for (int i=0; i<doorList.size();i++) {
                            doorObject r = doorList.get(i);
                            if (x >= r.getX()-20 && x <= r.getX() + 19 && y >= r.getY()-20 && y <= r.getY() + 19)
                                delId=i;
                        }
                        if(delId>=0)
                            doorList.remove(delId);
                    }

                }
                mainpanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if(draggedObject!=null)
                {
                    if(draggedObject.getX()%150<15)
                        draggedObject.move(15-draggedObject.getX()%150,0);
                    if(draggedObject.getX()%150>135)
                        draggedObject.move(135-draggedObject.getX()%150,0);
                    if(draggedObject.getY()%150<15)
                        draggedObject.move(0,15-draggedObject.getY()%150);
                    if(draggedObject.getY()%150>135)
                        draggedObject.move(0,135-draggedObject.getY()%150);
                    mainpanel.repaint();
                }
                draggedObject = null;
                dragEnabled=true;
                pressY=0;
                pressX=0;
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
                if(dragEnabled && mode==0)
                {
                    if(draggedObject==null)
                    {
                        globalX+=mouseEvent.getX()-pressX;
                        pressX=mouseEvent.getX();
                        globalY+=mouseEvent.getY()-pressY;
                        pressY=mouseEvent.getY();
                        if(globalX>0)
                            globalX=0;
                        if(globalY>0)
                            globalY=0;
                    }
                    else
                    {
                        draggedObject.move(mouseEvent.getX()-pressX,mouseEvent.getY()-pressY);
                        pressX=mouseEvent.getX();
                        pressY=mouseEvent.getY();
                    }

                    mainpanel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });


        mainframe.add(mainpanel);
        mainframe.add(sidepanel);

        //HIDE
        mainframe.setVisible(false);
    }

    class myCanvas extends JPanel {

        @Override
        public void paint(Graphics g)
        {
            //Draw Grid
            g.setColor(Color.white);
            g.fillRect(0,0,getWidth(),getHeight());
            int startX=globalX%150,startY=globalY%150;
            g.setColor(Color.LIGHT_GRAY);
            while (startX<w)
            {
                g.drawLine(startX,0,startX,h);
                startX+=150;
            }
            while (startY<h)
            {
                g.drawLine(0,startY,w,startY);
                startY+=150;
            }
            //Draw Rooms
            for(roomObject r: roomList) {
                g.setColor(Color.GRAY);
                g.fillRect(r.getX()+globalX, r.getY()+globalY, 150, 150);
                g.setColor(Color.BLACK);
                g.drawRect(r.getX()+globalX, r.getY()+globalY, 150, 150);
            }
            //Draw Lamps
            for(electricObject l:lampList)
            {
                g.setColor(Color.WHITE);
                g.fillOval(l.getX()+globalX-10,l.getY()+globalY-10,20,20);
                g.setColor(Color.BLACK);
                g.drawOval(l.getX()+globalX-10,l.getY()+globalY-10,20,20);
            }
            //Draw RTVs
            for(electricObject r:rtvList)
            {
                g.setColor(Color.green);
                g.fillRect(r.getX()+globalX-10,r.getY()+globalY-10,20,20);
                g.setColor(Color.BLACK);
                g.drawRect(r.getX()+globalX-10,r.getY()+globalY-10,20,20);
            }
            //Draw Doors
            for(doorObject d:doorList)
            {
                g.setColor(new Color(180, 120, 50));
                if(d.getVertical()) {
                    g.fillRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                    g.setColor(Color.BLACK);
                    g.drawRect(d.getX() + globalX - 5, d.getY() + globalY - 15, 10, 30);
                }
                else
                {
                    g.fillRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                    g.setColor(Color.BLACK);
                    g.drawRect(d.getX() + globalX - 15, d.getY() + globalY - 5, 30, 10);
                }
                //System.out.println("N: "+d.getHouseId()+" d1: "+d.getD1()+" d2: "+d.getD2());
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced",Font.BOLD,20));

            for(roomObject r: roomList)
                g.drawString(r.getName(),r.getX()+globalX+10,r.getY()+globalY+25);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced",Font.BOLD,15));

            for(electricObject l: lampList)
                g.drawString(l.getName(),l.getX()-10+globalX,l.getY()+globalY-20);

            for(electricObject r: rtvList)
                g.drawString(r.getName(),r.getX()-10+globalX,r.getY()+globalY-20);

            for(doorObject d:doorList)
                g.drawString(d.getName(),d.getX()-20+globalX,d.getY()+globalY-20);
        }
    }

    private boolean checkDupes(int x, int y,boolean room)
    {
        if(room) {
            for (roomObject r : roomList) {
                if (r.getX() == x && r.getY() == y)
                    return false;
            }
        }
        else
        {
            for(doorObject d: doorList)
            {
                if(d.getX()==x && d.getY()==y)
                    return false;
            }
        }
        return true;
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

    public JFrame getMainframe() {
        return mainframe;
    }

    public JButton getbSave() {
        return bSave;
    }

    private void createObjects() {
        Type roomListType = new TypeToken<List<SmartHomeApiClient.Room>>() {}.getType();
        List<SmartHomeApiClient.SmartHomeObject> rooms = apiClient.getList("Room", roomListType);

    }

    SmartHomeApiClient.Room findRoomByName(List<SmartHomeApiClient.SmartHomeObject> list, String dev) {
        for(SmartHomeApiClient.SmartHomeObject o : list) {
            SmartHomeApiClient.Room r = (SmartHomeApiClient.Room)o;
            if(r.device.equals(dev)) {
                return r;
            }
        }
        return null;
    }

    SmartHomeApiClient.Device findDeviceByName(List<SmartHomeApiClient.SmartHomeObject> list, String dev) {
        for(SmartHomeApiClient.SmartHomeObject o : list) {
            SmartHomeApiClient.Device d = (SmartHomeApiClient.Device)o;
            if(d.device.equals(dev)) {
                return d;
            }
        }
        return null;
    }

    public void save()
    {
        Comparator<roomObject> roomComp = new Comparator<roomObject>() {
            @Override
            public int compare(roomObject t1, roomObject t2) {
                if(t1.getY()<t2.getY())
                    return -1;
                else
                {
                    if(t1.getY()==t2.getY())
                    {
                        if(t1.getX()<t2.getX())
                            return -1;
                        else
                            return 1;
                    }
                    else
                        return 1;
                }

            }
        };

        roomList.sort(roomComp);

        Type mapListType = new TypeToken<List<SmartHomeApiClient.MapEntry>>() {}.getType();
        Type roomListType = new TypeToken<List<SmartHomeApiClient.Room>>() {}.getType();
        Type lampListType = new TypeToken<List<SmartHomeApiClient.Lamp>>() {}.getType();
        Type rtvListType = new TypeToken<List<SmartHomeApiClient.RTV>>() {}.getType();
        Type doorListType = new TypeToken<List<SmartHomeApiClient.Door>>() {}.getType();
        // remove existing map entries from database
        List<SmartHomeApiClient.SmartHomeObject> oldEntries = apiClient.getList("Map", mapListType);
        if(oldEntries != null)
            for(SmartHomeApiClient.SmartHomeObject o : oldEntries) {
                apiClient.deleteObject("Map", o.id);
            }

        List<SmartHomeApiClient.SmartHomeObject> rooms = apiClient.getList("Room", roomListType);
        List<SmartHomeApiClient.SmartHomeObject> lamps = apiClient.getList("Lamp", lampListType);
        List<SmartHomeApiClient.SmartHomeObject> doors = apiClient.getList("Door", doorListType);
        List<SmartHomeApiClient.SmartHomeObject> rtvs = apiClient.getList("RTV", rtvListType);


        for (roomObject r : roomList) {
            SmartHomeApiClient.MapEntry mapEntry = new SmartHomeApiClient.MapEntry();
            mapEntry.house = houseId;
            mapEntry.name = r.getName();
            mapEntry.type = "r";
            mapEntry.posX = r.getX();
            mapEntry.posY = r.getY();
            apiClient.postObject("Map", mapEntry);
            if(findRoomByName(rooms, r.getName()) == null) {
                SmartHomeApiClient.Room newRoom = new SmartHomeApiClient.Room();
                newRoom.house = houseId;
                newRoom.name = r.getName();
                newRoom.device = r.getName();
                newRoom.favourite = false;
                newRoom.people = false;
                newRoom.humidity = 50.0f;
                newRoom.temperature = 20.0f;
                apiClient.postObject("Room", newRoom);
            }
        }
        // reload list to get new room IDs
        rooms = apiClient.getList("Room", roomListType);

        for (electricObject r : lampList) {
            SmartHomeApiClient.MapEntry mapEntry = new SmartHomeApiClient.MapEntry();
            mapEntry.house = houseId;
            mapEntry.name = r.getName();
            mapEntry.type = "l";
            mapEntry.posX = r.getX();
            mapEntry.posY = r.getY();
            apiClient.postObject("Map", mapEntry);
            if(findDeviceByName(lamps, r.getName()) == null) {
                SmartHomeApiClient.Lamp newLamp = new SmartHomeApiClient.Lamp();
                newLamp.room = null;
                for (roomObject room: roomList) {
                    if (room.getX()+150 >= r.getX() && room.getX() <= r.getX() && room.getY()+150 >= r.getY() && room.getY() <= r.getY() + 19) {
                        SmartHomeApiClient.SmartHomeObject dev = findRoomByName(rooms, room.getName());
                        if(dev != null) {
                            newLamp.room = dev.id;
                        }
                    }
                }
                newLamp.name = r.getName();
                newLamp.device = r.getName();
                newLamp.dimmable = false;
                newLamp.favourite = false;
                newLamp.state = false;
                newLamp.intensity = 0;
                apiClient.postObject("Lamp", newLamp);
            }
        }
        for (electricObject r : rtvList) {
            SmartHomeApiClient.MapEntry mapEntry = new SmartHomeApiClient.MapEntry();
            mapEntry.house = houseId;
            mapEntry.name = r.getName();
            mapEntry.type = "x";
            mapEntry.posX = r.getX();
            mapEntry.posY = r.getY();
            apiClient.postObject("Map", mapEntry);
            if(findDeviceByName(rtvs, r.getName()) == null) {
                SmartHomeApiClient.RTV newRtv = new SmartHomeApiClient.RTV();
                newRtv.room = null;
                for (roomObject room: roomList) {
                    if (room.getX()+150 >= r.getX() && room.getX() <= r.getX() && room.getY()+150 >= r.getY() && room.getY() <= r.getY() + 19) {
                        SmartHomeApiClient.SmartHomeObject dev = findRoomByName(rooms, room.getName());
                        if(dev != null)
                            newRtv.room = dev.id;
                    }
                }
                newRtv.name = r.getName();
                newRtv.device = r.getName();
                newRtv.volume = 0;
                newRtv.favourite = false;
                newRtv.state = false;
                apiClient.postObject("RTV", newRtv);
            }

        }
        for (doorObject r : doorList) {
            SmartHomeApiClient.MapEntry mapEntry = new SmartHomeApiClient.MapEntry();
            mapEntry.house = houseId;
            mapEntry.name = r.getName();
            mapEntry.type = r.getVertical() ? "v" : "h";
            mapEntry.posX = r.getX();
            mapEntry.posY = r.getY();
            apiClient.postObject("Map", mapEntry);
            if(findDeviceByName(doors, r.getName()) == null) {
                SmartHomeApiClient.Door newDoor = new SmartHomeApiClient.Door();
                newDoor.device = r.getName();
                if(r.getD1().equals("Null")) {
                    newDoor.room1 = null;
                } else {
                    SmartHomeApiClient.Room room1 = findRoomByName(rooms, r.getD1());
                    newDoor.room1 = room1 != null ? room1.id : null;
                }
                if(r.getD2().equals("Null")) {
                    newDoor.room2 = null;
                } else {
                    SmartHomeApiClient.Room room2 = findRoomByName(rooms, r.getD2());
                    newDoor.room2 = room2 != null ? room2.id : null;
                }
                newDoor.state = false;
                newDoor.favourite = false;
                apiClient.postObject("Door", newDoor);
            }
        }
    }

    public void loadMap(int house)
    {
        houseId = house;
        try {
            Type mapListType = new TypeToken<List<SmartHomeApiClient.MapEntry>>() {}.getType();

            List<SmartHomeApiClient.SmartHomeObject> entries = apiClient.getList("Map", mapListType);
            int maxr=0,maxl=0,maxe=0,maxd=0;
            for(SmartHomeApiClient.SmartHomeObject o : entries)
            {
                SmartHomeApiClient.MapEntry entry = (SmartHomeApiClient.MapEntry)o;
                if(entry.house != houseId)
                    continue;

                char type = entry.type.charAt(0);
                if(type=='r') {
                    int id = Integer.valueOf(entry.name.substring(4));
                    if(maxr<id)
                        maxr=id;
                    roomObject r = new roomObject(entry.posX, entry.posY, entry.name);
                    r.setId(roomList.size());
                    roomList.add(r);
                }
                if(type=='l') {
                    int id = Integer.valueOf(entry.name.substring(4));
                    if(maxl<id)
                        maxl=id;
                    electricObject l = new electricObject(entry.posX, entry.posY, entry.name);
                    for (roomObject r: roomList) {
                        if (r.getX()+150 >= l.getX() && r.getX() <= l.getX() && r.getY()+150 >= l.getY() && r.getY() <= l.getY() + 19) {
                            l.setRoomId(r.getId());
                        }
                    }
                    l.setId(lampList.size());
                    lampList.add(l);
                }
                if(type=='x') { ;
                    int id = Integer.valueOf(entry.name.substring(3));
                    if(maxe<id)
                        maxe=id;
                    electricObject x = new electricObject(entry.posX, entry.posY, entry.name);
                    x.setId(rtvList.size());
                    rtvList.add(x);
                }
                if(type=='h' || type=='v') {
                    int id = Integer.valueOf(entry.name.substring(4));
                    if(maxd<id)
                        maxd=id;
                    doorObject d = new doorObject(entry.posX, entry.posY, entry.name);
                    d.setVertical(type=='v');
                    d.setId(doorList.size());
                    connect(d);
                    doorList.add(d);
                }
            }
            roomC=maxr+1;
            lampC=maxl+1;
            rtvC=maxe+1;
            doorC=maxd+1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mainpanel.repaint();
    }
}

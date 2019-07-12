import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class patientWindow extends JFrame {

    private JPanel mainPanel;
    private JTextField tPesel, tFName, tLName;
    private JList tAppointments, tHistory;
    ArrayList<Integer> index_list;
    private int userID;
    JComboBox cbDoctor;

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    public patientWindow(int userID) {
        setTitle("Panel pacjenta");
        setSize(500, 520);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        conn = databaseConn.db_connection();
        this.userID = userID;

        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 500, 520);
        mainPanel.setLayout(null);

        JLabel lFName = new JLabel("Imię:");
        lFName.setBounds(30, 30, 70, 30);
        tFName = new JTextField();
        tFName.setEditable(false);
        tFName.setBounds(100, 30, 370, 30);
        tFName.setBorder(BorderFactory.createCompoundBorder(tFName.getBorder(), BorderFactory.createEmptyBorder(0, 6, 0, 0))); // Padding

        JLabel lLName = new JLabel("Nazwisko:");
        lLName.setBounds(30, 75, 70, 30);
        tLName = new JTextField();
        tLName.setEditable(false);
        tLName.setBounds(100, 75, 370, 30);
        tLName.setBorder(BorderFactory.createCompoundBorder(tLName.getBorder(), BorderFactory.createEmptyBorder(0, 6, 0, 0)));

        JLabel lPesel = new JLabel("PESEL:");
        lPesel.setBounds(30, 120, 70, 30);
        tPesel = new JTextField();
        tPesel.setEditable(false);
        tPesel.setBounds(100, 120, 370, 30);
        tPesel.setBorder(BorderFactory.createCompoundBorder(tPesel.getBorder(), BorderFactory.createEmptyBorder(0, 6, 0, 0)));

        loadPatientData();

        JLabel lAppointments = new JLabel("Umówione wizyty:");
        lAppointments.setBounds(30, 165, 440, 30);

        tAppointments = new JList();
        loadAppointments();

        tAppointments.setVisibleRowCount(3);
        JScrollPane tAScroller = new JScrollPane(tAppointments, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tAScroller.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
        tAScroller.setBounds(30, 195, 440, 60);
        add(tAScroller);

        JLabel lHistory = new JLabel("Historia chorób:");
        lHistory.setBounds(30, 270, 440, 30);

        tHistory = new JList();
        tHistory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList theList = (JList) e.getSource();
                if (e.getClickCount() == 1) {
                    int index = theList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Object o = theList.getModel().getElementAt(index);
                        //System.out.println("Double-clicked on: " + o.toString() + " " + index_list.get(index));
                        recordWindow RW=new recordWindow(index_list.get(index));
                    }
                }
            }
        });
        loadHistory();

        tHistory.setVisibleRowCount(6);
        JScrollPane tHScroller = new JScrollPane(tHistory, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tHScroller.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
        tHScroller.setBounds(30, 300, 440, 120);
        add(tHScroller);

        JTextField tDate = new JTextField();
        tDate.setToolTipText("YYYY-MM-DD");

//        DateFormat format1 = new SimpleDateFormat("YYYY-MM-DD");
//        JFormattedTextField tDate = new JFormattedTextField(format1);

        tDate.setBounds(30,440,120,30);
        JComboBox cbTime = new JComboBox();
        cbTime.setBounds(160,440,80,30);
        for(int i=0;i<20;i++)
        {
            int hour=8+i/2;
            String min;
            if(i%2==0)
                min=":00";
            else
                min=":30";
            String text = Integer.toString(hour)+min;
            cbTime.addItem(text);
        }



        cbDoctor = new JComboBox();
        cbDoctor.setBounds(250,440,120,30);

        String query = "select * from users where user_type_id=1";
        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            while(rs.next()) {
                cbDoctor.addItem("Dr "+(rs.getString("first_name"))+" "+(rs.getString("last_name"))+" "+(rs.getInt("id")));
            }

        }
        catch (Exception exc) {
            exc.printStackTrace();
        }

        JButton bVisit = new JButton("Zapisz");
        bVisit.setBounds(380,440,90,30);
        bVisit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String Date = new String(tDate.getText()+" "+cbTime.getSelectedItem()+":00");
                String Doctor = (String)cbDoctor.getSelectedItem();
                System.out.println(Date+" "+Doctor);

                int d = Integer.parseInt(Doctor.substring(Doctor.lastIndexOf(" ")+1));
                String query = "select * from visits where doctor_id=? and visit_date=?";
                String query2 = "INSERT INTO `visits`(`id`, `doctor_id`, `patient_id`, `visit_date`) VALUES (NULL,?,?,?)";
                try {
                    pst = conn.prepareStatement(query);
                    pst.setInt(1, d);
                    pst.setString(2, Date);
                    rs = pst.executeQuery();

                    if(rs.next()) {
                        JOptionPane.showMessageDialog(null, "Wizyta jest juz zajeta!", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        try {
                            pst = conn.prepareStatement(query2);
                            pst.setInt(1, d);
                            pst.setInt(2, userID);
                            pst.setString(3, Date);
                            int i = pst.executeUpdate();
                            if(i>0) {
                                String query3 = "select * from records where doctor_id=? and patient_id=?";
                                String query4 = "INSERT INTO `records`(`id`, `record`,`doctor_id`, `patient_id`,`modified_date`,`image_id`) VALUES (NULL,?,?,?,?,?)";
                                pst=conn.prepareStatement(query3);
                                pst.setInt(1,d);
                                pst.setInt(2,userID);
                                rs=pst.executeQuery();
                                if(rs.next())
                                {
                                    System.out.println("");
                                }
                                else
                                {
                                    pst=conn.prepareStatement(query4);
                                    pst.setString(1," ");
                                    pst.setInt(2,d);
                                    pst.setInt(3,userID);
                                    pst.setString(4,Date);
                                    pst.setInt(5,1);
                                    int ok=pst.executeUpdate();
                                    if(ok>0)
                                        System.out.println("ok");
                                }
                                JOptionPane.showMessageDialog(null, "Dodano wizytę", "InfoBox: " + "OK", JOptionPane.INFORMATION_MESSAGE);
                                loadAppointments();
                            }
                            else {
                                System.out.println("stuck somewhere");
                            }
                        }
                        catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });

        mainPanel.add(lFName);
        mainPanel.add(tFName);
        mainPanel.add(lLName);
        mainPanel.add(tLName);
        mainPanel.add(lPesel);
        mainPanel.add(tPesel);
        mainPanel.add(lAppointments);
        mainPanel.add(lHistory);
        mainPanel.add(tDate);
        mainPanel.add(cbTime);
        mainPanel.add(cbDoctor);
        mainPanel.add(bVisit);
        add(mainPanel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadPatientData() {
        String query = "select * from users where id=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, userID);

            rs = pst.executeQuery();

            if(rs.next()) {
                tFName.setText(rs.getString("first_name"));
                tLName.setText(rs.getString("last_name"));
                tPesel.setText(rs.getString("pesel"));
            }
            else {

            }
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void loadAppointments() {
        DefaultListModel dlm = new DefaultListModel();

        String query = "select visit_date, first_name, last_name, doctor_id from visits inner join users on visits.doctor_id = users.id where patient_id=? and date(visit_date) > NOW()";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, userID);

            rs = pst.executeQuery();
            String data1;

            while(rs.next()) {
//                data1 = new StringBuilder().append(rs.getString("visit_date")).append(" dr. ").append(rs.getString("first_name"))
//                        .append(" ").append(rs.getString("last_name")).toString();
                data1 = new StringBuilder().append(rs.getString("visit_date")).append(" dr. ").append(rs.getString("first_name"))
                        .append(" ").append(rs.getString("last_name")).toString();
                dlm.addElement(data1);
            }
            tAppointments.setModel(dlm);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void loadHistory() {
        DefaultListModel dlm2 = new DefaultListModel();
        index_list= new ArrayList<Integer>();

        String query = "select records.id, modified_date, last_name from records inner join users on records.doctor_id = users.id where patient_id=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, userID);

            rs = pst.executeQuery();
            String data1;

            while(rs.next()) {
                data1 = new StringBuilder().append(rs.getString("modified_date")).append(" dr. ").append(rs.getString("last_name")).toString();
                index_list.add(Integer.parseInt(rs.getString("records.id")));
                dlm2.addElement(data1);
            }
            tHistory.setModel(dlm2);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
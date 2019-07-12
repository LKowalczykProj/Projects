import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class LecturerWindow extends UserWindow{
    private int counter;
    public LecturerWindow(Connection myConn)
    {
        super(myConn);
        getFrame().setTitle("Panel profesorski");

        getbCourse().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createCourse();
            }
        });

        getbGrades().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addGrades();
            }
        });
    }

    private void addCourseInstance(String name,JPanel p, GridBagConstraints gbc)
    {
        JPanel pi = new JPanel();
        pi.setLayout(null);
        JLabel l = new JLabel(name);
        l.setBounds(20, 10, 200, 30);
        JButton b = new JButton("Pliki");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = jfc.showOpenDialog(null);

                if(returnValue==JFileChooser.APPROVE_OPTION)
                {
                    File selectedFile = jfc.getSelectedFile();
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(selectedFile);
                        String query = "insert into Materials "
                                + "(File, Course_id) "
                                + "select ?, id "
                                + "from Courses "
                                + "where Name = ?";
                        PreparedStatement ps = getMyConn().prepareStatement(query);
                        ps.setBinaryStream(1,fis,(int) selectedFile.length());
                        ps.setString(2,name);
                        ps.executeUpdate();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        b.setBounds(280, 10, 80, 30);
        pi.add(b);
        pi.add(l);
        gbc.gridx = 0;
        gbc.gridy = counter;
        gbc.ipadx = 380;
        gbc.ipady = 50;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(pi, gbc);
        counter+=1;
    }

    private void createCourse()
    {
        JFrame courseframe = new JFrame("Kursy");
        courseframe.setLocationRelativeTo(null);
        courseframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        courseframe.setResizable(false);
        courseframe.setLayout(null);
        courseframe.setSize(500,600);

        JLabel lCour = new JLabel("Twoje Kursy");
        lCour.setBounds(50,30,400,50);
        lCour.setFont(new Font("Verdana",Font.BOLD,30));
        lCour.setHorizontalAlignment(SwingConstants.CENTER);
        courseframe.add(lCour);

        JPanel p = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        p.setLayout(gbl);
        counter=0;
        try {
            String query = "Select Name from Courses where Creator_id=?";
            PreparedStatement ps1 = getMyConn().prepareStatement(query);
            ps1.setInt(1,getId());
            ResultSet rs = ps1.executeQuery();
            while(rs.next()) {
                addCourseInstance(rs.getString("Name"),p,gbc);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        JScrollPane scroll = new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(15,0));
        scroll.setBounds(50,100,400,400);

        JTextField tAdd = new JTextField();
        tAdd.setBounds(150,520,300,30);
        courseframe.add(tAdd);

        JButton bAdd = new JButton("Dodaj");
        bAdd.setBounds(50,520,80,30);
        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!tAdd.getText().equals(""))
                {
                    String query = "insert into Courses "
                            +"(Name, Creator_id) "
                            +"Values(?,?)";
                    try {
                        PreparedStatement ps = getMyConn().prepareStatement(query);
                        ps.setString(1,tAdd.getText());
                        ps.setInt(2,getId());
                        ps.executeUpdate();
                        addCourseInstance(tAdd.getText(),p,gbc);
                        tAdd.setText("");
                        courseframe.setVisible(false);
                        courseframe.setVisible(true);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        courseframe.add(bAdd);

        courseframe.add(scroll);
        courseframe.setVisible(true);
    }

    private void addGrades()
    {
        JFrame gradeframe  = new JFrame("Oceny");
        gradeframe.setLayout(null);
        gradeframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gradeframe.setResizable(false);
        gradeframe.setLocationRelativeTo(null);
        gradeframe.setSize(700,100);

        JComboBox courseBox = new JComboBox();
        courseBox.setBounds(270,20,230,30);
        gradeframe.add(courseBox);

        JComboBox studentBox  = new JComboBox();
        studentBox.setBounds(20,20,230,30);

        ArrayList<Integer> SID = new ArrayList<>();

        try
        {
            String query = "select U.id, U.Fullname "
                    + "from Users U, Registry R, Courses C "
                    + "where U.id = R.Student_id and R.Course_id = C.id and C.Creator_id = ? "
                    + "group by U.Fullname";
            PreparedStatement ps = getMyConn().prepareStatement(query);
            ps.setInt(1,getId());
            ResultSet rs = ps.executeQuery();
            int i=0;
            while(rs.next())
            {
                studentBox.addItem(rs.getString("Fullname"));
                SID.add(rs.getInt("id"));
                if(i==0)
                {
                    String query2 = "select C.Name "
                            + "from Courses C, Users U, Registry R "
                            + "where C.id = R.Course_id and R.Student_id = U.id and U.Fullname = ?";
                    PreparedStatement ps2 = getMyConn().prepareStatement(query2);
                    ps2.setString(1,rs.getString("Fullname"));
                    ResultSet rs2 = ps2.executeQuery();
                    while(rs2.next())
                    {
                        courseBox.addItem(rs2.getString("Name"));
                    }
                }
                i+=1;
            }
            if(i!=0)
                studentBox.setSelectedIndex(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        studentBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try
                {
                    String query = "select C.Name "
                            + "from Courses C, Users U, Registry R "
                            + "where C.id = R.Course_id and R.Student_id = U.id and U.Fullname = ?";
                    PreparedStatement ps = getMyConn().prepareStatement(query);
                    ps.setString(1,(String) studentBox.getSelectedItem());
                    ResultSet rs = ps.executeQuery();
                    courseBox.removeAllItems();
                    while(rs.next())
                    {
                        courseBox.addItem(rs.getString("Name"));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        gradeframe.add(studentBox);

        JTextField tGrade = new JTextField("");
        tGrade.setBounds(520,20,40,30);
        gradeframe.add(tGrade);

        JButton bSubmit = new JButton("Wstaw");
        bSubmit.setBounds(580,20,100,30);
        bSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String grade = tGrade.getText();
                if(!grade.equals(""))
                {
                    try
                    {
                        Double d = Double.parseDouble(grade);
                        String query = "insert into Grades "
                                + "(Course_name, Value, Student_id) "
                                + "Values (?,?,?)";
                        PreparedStatement ps = getMyConn().prepareStatement(query);
                        ps.setString(1,(String) courseBox.getSelectedItem());
                        ps.setDouble(2,d);
                        ps.setInt(3,SID.get(studentBox.getSelectedIndex()));
                        ps.executeUpdate();
                        tGrade.setText("");
                        JOptionPane.showMessageDialog(null, "Ocena dodana", "InfoBox: " + "Sukces", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (Exception e)
                    {
                        if(e.getClass()==NumberFormatException.class)
                            JOptionPane.showMessageDialog(null, "Ocena powninna być liczbą", "Error: " + "Błąd", JOptionPane.ERROR_MESSAGE);
                        else
                            e.printStackTrace();
                    }
                }
            }
        });
        gradeframe.add(bSubmit);

        gradeframe.setVisible(true);
    }
}

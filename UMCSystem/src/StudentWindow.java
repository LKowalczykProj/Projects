import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.*;

public class StudentWindow extends UserWindow{

    private int counter;
    public StudentWindow(Connection myConn)
    {
        super(myConn);
        getFrame().setTitle("Panel Studenta");

        getbCourse().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                enroll();
            }
        });

        getbGrades().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayGrades();
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
                try
                {
                    String query = "select M.File "
                            +"from Materials M, Courses C "
                            +"where M.Course_id = C.id and C.Name = ?";
                    PreparedStatement ps = getMyConn().prepareStatement(query);
                    ps.setString(1,name);
                    ResultSet rs = ps.executeQuery();
                    int i=0;
                    String path="";
                    int returnValue = 1;
                    while(rs.next())
                    {
                        if(i==0)
                        {
                            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            returnValue = jfc.showOpenDialog(null);
                            if(returnValue==JFileChooser.APPROVE_OPTION)
                            {
                                path=jfc.getSelectedFile().getAbsolutePath();
                            }
                        }
                        if(returnValue==JFileChooser.APPROVE_OPTION)
                        {
                            Blob b = rs.getBlob("File");
                            InputStream in = b.getBinaryStream();
                            BufferedImage im = ImageIO.read(in);
                            File f = new File(path+"/Material"+Integer.toString(i+1)+".png");
                            ImageIO.write(im,"png",f);
                        }
                        i+=1;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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

    private void enroll()
    {
        JFrame courseframe = new JFrame("Kursy");
        courseframe.setLocationRelativeTo(null);
        courseframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        courseframe.setResizable(false);
        courseframe.setLayout(null);
        courseframe.setSize(500,600);

        JPanel p = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        p.setLayout(gbl);
        counter=0;
        try {
            String query = "Select C.Name "
                    + "from Courses C,Registry R, Users U "
                    + "where U.id = ? and U.id = R.Student_id and R.Course_id = C.id";
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

        JLabel lCour = new JLabel("Twoje Kursy");
        lCour.setBounds(50,30,400,50);
        lCour.setFont(new Font("Verdana",Font.BOLD,30));
        lCour.setHorizontalAlignment(SwingConstants.CENTER);
        courseframe.add(lCour);

        JComboBox courseBox = new JComboBox();
        courseBox.setBounds(50,520,300,30);
        courseframe.add(courseBox);

        try{
            String query = "Select Name from Courses "
                    + "where Name not in (Select C.Name "
                    + "from Courses C,Registry R, Users U "
                    + "where U.id = ? and U.id = R.Student_id and R.Course_id = C.id "
                    + "group by C.Name)";
            PreparedStatement pr = getMyConn().prepareStatement(query);
            pr.setInt(1,getId());

            ResultSet rs = pr.executeQuery();
            int i=0;
            while(rs.next())
            {
                i+=1;
                courseBox.addItem(rs.getString("Name"));
            }
            if(i==0)
            {
                Statement pr2 = getMyConn().createStatement();
                ResultSet rs2 = pr2.executeQuery("Select Name from Courses");
                while(rs2.next())
                {
                    courseBox.addItem(rs2.getString("Name"));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        JButton bEnroll = new JButton("Zapisz");
        bEnroll.setBounds(370,520,80,30);
        bEnroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try
                {
                    String result = (String) courseBox.getSelectedItem();
                    String query = "insert into Registry "
                            + "(Course_id,Student_id ) "
                            + "select C.id, ? "
                            + "from Courses C "
                            + "where C.Name = ?";
                    System.out.println(result);
                    PreparedStatement ps = getMyConn().prepareStatement(query);
                    ps.setInt(1,getId());
                    ps.setString(2,result);
                    ps.executeUpdate();
                    courseBox.removeItem(result);
                    addCourseInstance(result,p,gbc);
                    courseframe.setVisible(false);
                    courseframe.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        courseframe.add(bEnroll);
        courseframe.add(scroll);
        courseframe.setVisible(true);
    }

    private void displayGrades()
    {
        JFrame gradeframe = new JFrame("Oceny");
        gradeframe.setLocationRelativeTo(null);
        gradeframe.setResizable(false);
        gradeframe.setLayout(null);
        gradeframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gradeframe.setSize(600,160);

        JTextArea tGrades = new JTextArea();
        tGrades.setFont(new Font("Verdana",Font.PLAIN,30));
        tGrades.setMargin(new Insets(5,5,5,5));
        tGrades.setEditable(false);
        tGrades.setBackground(Color.LIGHT_GRAY);
        JScrollPane scGrades = new JScrollPane(tGrades,ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scGrades.setBounds(30, 70,540,50);
        gradeframe.add(scGrades);

        JComboBox courseBox = new JComboBox();
        courseBox.setBounds(200,20,200,30);
        try
        {
            String query = "select C.Name "
                    + "from Courses C, Registry R, Users U "
                    + "where U.id = R.Student_id and R.Course_id = C.id and U.id = ?";
            PreparedStatement ps = getMyConn().prepareStatement(query);
            ps.setInt(1,getId());
            ResultSet rs = ps.executeQuery();
            int i=0;
            while(rs.next())
            {
                courseBox.addItem(rs.getString("Name"));
                if(i==0)
                {
                    String query2 = "select Value "
                            + "from Grades "
                            + "where Student_id = ? and Course_name = ?";
                    PreparedStatement ps2 = getMyConn().prepareStatement(query2);
                    ps2.setInt(1,getId());
                    ps2.setString(2,rs.getString("Name"));
                    ResultSet rs2 = ps2.executeQuery();
                    while(rs2.next())
                    {
                        tGrades.append(rs2.getDouble("Value")+" | ");
                    }
                }
                i+=1;
            }
            if(i!=0)
                courseBox.setSelectedIndex(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        courseBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try
                {
                    tGrades.setText("");
                    String course = (String) courseBox.getSelectedItem();

                    String query = "select Value "
                            + "from Grades "
                            + "where Student_id = ? and Course_name = ?";
                    PreparedStatement ps = getMyConn().prepareStatement(query);
                    ps.setInt(1,getId());
                    ps.setString(2,course);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next())
                    {
                        tGrades.append(rs.getString("Value")+" | ");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        gradeframe.add(courseBox);

        gradeframe.setVisible(true);
    }
}

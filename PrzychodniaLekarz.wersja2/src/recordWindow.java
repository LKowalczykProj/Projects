import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class recordWindow extends JFrame {

    private JPanel mainPanel;
    private JTextArea tRecord;

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    public recordWindow(int visit_id) {
        setTitle("Historia Choroby");
        setSize(500, 390);
        setLayout(null);
        setResizable(false);


        tRecord = new JTextArea();
        tRecord.setEditable(false);
        tRecord.setLineWrap(true);
        tRecord.setFont(new Font("Verdana",Font.ITALIC,20));
        tRecord.setMargin(new Insets(5,5,5,5));
        JScrollPane tRScroller = new JScrollPane(tRecord, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tRScroller.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
        tRScroller.setBounds(30, 45, 440, 300);
        add(tRScroller);

        conn = databaseConn.db_connection();

        loadData(visit_id);

        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 500, 500);
        mainPanel.setLayout(null);

        JLabel lDescription = new JLabel("Opis wizyty:");
        lDescription.setBounds(30, 15, 440, 30);

        mainPanel.add(lDescription);

        add(mainPanel);
        setVisible(true);

    }

    public void loadData(int visit_id)
    {
        String query = "select images.image, records.id, records.record from records inner join images on records.image_id = images.id where records.id=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, visit_id);
            rs = pst.executeQuery();

            if(rs.next()) {
                String text;
                text=rs.getString("record");
                tRecord.append(text);
            }
            else {
                // Error message?
            }
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }

    }
}

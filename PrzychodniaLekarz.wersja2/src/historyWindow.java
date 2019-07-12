import entities.RecordsEntity;

import javax.swing.*;
import java.awt.*;

public class historyWindow extends JFrame {

    public JTextArea tArea;
    public String DefaultContent;
    public  RecordsEntity historia;
    public RecordDAO recordDAO = new RecordDAO();

    public historyWindow(RecordsEntity historiaChoroby)
    {
        historia=historiaChoroby;
        DefaultContent=null;
        setTitle("Historia Choroby");
        setResizable(false);
        setSize(400,600);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        JPanel mainPanel =new JPanel();
        mainPanel.setBackground(Color.BLUE);
        mainPanel.setLayout(null);
        tArea =new JTextArea();
        tArea.setFont(new Font("Verdana",Font.PLAIN,25));
        tArea.setLineWrap(true);
        tArea.setEditable(false);

        tArea.append(historiaChoroby.getRecord());
        DefaultContent = historiaChoroby.getRecord();

        JScrollPane scText = new JScrollPane(tArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scText.setBounds(0,0,this.getWidth(),this.getHeight());
        mainPanel.add(scText);
        add(mainPanel);

        setVisible(false);
    }

    public void setText(String text)
    {
        if(DefaultContent==null)
            DefaultContent = text;
        tArea.append(text);

    }
    public void dodajZmianyKartyDoBazy(String text){
        recordDAO.updateMedicalHistory(text, historia);
    }

    public void resetText()
    {
        tArea.setText(DefaultContent);
    }

    public String getText(){return tArea.getText();}
}

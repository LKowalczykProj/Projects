import entities.DrugsEntity;
import entities.RecordsEntity;
import entities.UsersEntity;
import entities.VisitsEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class KartaPacjenta extends JFrame implements ActionListener {

    public JPanel mainPanel;
    public JButton bHistory,bAdd,bSum,bBack,bPrint,bConfirm,bDelete;
    public historyWindow HW;
    private JTextArea tSymp,tDiag,tPers;
    public JComboBox drugCBox;
    public List<DrugsEntity> drugList; //Kopia leków z bazy leków (nazwa + cena)
    public List<Integer> selectionList; //Wybrane leki
    private JTextField tSum;
    private double total;

    Timestamp czasOrazDataWizyty;
    VisitsEntity wizytaBiezaca;
    RecordsEntity historiaChoroby;

    private UserDAO userDAO;
    private DrugsDAO drugsDAO;
    private RecordDAO recordDAO;
    public KartaPacjenta(VisitsEntity wizytaArg, Timestamp czasorazdatazapisu)
    {
        //Data Transfer Objects dla wyziągania danych z bazy
        userDAO = new UserDAO();
        drugsDAO = new DrugsDAO();
        recordDAO = new RecordDAO();

        //data, bieżąca wizytaBiezaca, biezaca lista lekow, historia choroby pacjenta
        czasOrazDataWizyty = czasorazdatazapisu;
        wizytaBiezaca =wizytaArg;
        drugList = drugsDAO.getAllDrugs();
        UsersEntity pacjentBiezacy = userDAO.findPatientById(wizytaBiezaca.getPatientId());
        historiaChoroby = recordDAO.getMedicalHistory(wizytaBiezaca.getPatientId(), wizytaBiezaca.getDoctorId());

        total=0;
        selectionList = new ArrayList<Integer>();
        HW=new historyWindow(historiaChoroby);
        setTitle("Wizyta");
        Font baseFont = new Font("Verdana",Font.PLAIN,15);
        //setLocationRelativeTo(null);
        setLayout(null);
        setSize(500,680);
        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,500,660);//-40
        mainPanel.setLayout(null);
        JTextField tName = new JTextField(pacjentBiezacy.getFirstName()+" "+pacjentBiezacy.getLastName());
        tName.setEnabled(false);
        tName.setFont(baseFont);
//        JTextField tBdate = new JTextField("Data Urodzenia");
//        tBdate.setEnabled(false);
//        tBdate.setFont(baseFont);
        JTextField tPesel = new JTextField(pacjentBiezacy.getPesel());
        tPesel.setEnabled(false);
        tPesel.setFont(baseFont);
        JTextField tAdress = new JTextField(pacjentBiezacy.getAdress());
        tAdress.setEnabled(false);
        tAdress.setFont(baseFont);
        tName.setBounds(30,30,440,30);
        //tBdate.setBounds(30,70,440,30);
        tPesel.setBounds(30,70,440,30);
        tAdress.setBounds(30,110,440,30);

        JLabel lSymp = new JLabel("Objawy:");
        lSymp.setBounds(30,150,100,30);
        lSymp.setFont(new Font("Verdana",Font.BOLD,15));
        JLabel lDiag = new JLabel("Diagnoza:");
        lDiag.setBounds(30,290,100,30);
        lDiag.setFont(new Font("Verdana",Font.BOLD,15));
        JLabel lPers = new JLabel("Przypisane Leki:");
        lPers.setBounds(30,430,150,30);
        lPers.setFont(new Font("Verdana",Font.BOLD,15));

        bHistory = new JButton("Historia");
        bHistory.setBounds(390,600,80,30);
        bHistory.addActionListener(this);
        bBack = new JButton("Cofnij");
        bBack.setBounds(300,600,80,30);
        bBack.addActionListener(this);
        bAdd = new JButton("Dodaj");
        bAdd.setBounds(210,600,80,30);
        bAdd.addActionListener(this);
        bPrint = new JButton("Drukuj");
        bPrint.setBounds(120,600,80,30);
        bPrint.addActionListener(this);
        bSum = new JButton("Podlicz");
        bSum.setBounds(30,600,80,30);
        bSum.addActionListener(this);

        tSymp = new JTextArea();
        tSymp.setLineWrap(true);
        tSymp.setFont(new Font("Verdana",Font.PLAIN,20));
        tSymp.setMargin(new Insets(5,5,5,5));
        JScrollPane scSymp = new JScrollPane(tSymp,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scSymp.setBounds(30,185,440,100);

        tDiag = new JTextArea();
        tDiag.setLineWrap(true);
        tDiag.setFont(new Font("Verdana",Font.PLAIN,20));
        tDiag.setMargin(new Insets(5,5,5,5));
        JScrollPane scDiag = new JScrollPane(tDiag,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scDiag.setBounds(30,325,440,100);

        tPers = new JTextArea();
        tPers.setLineWrap(true);
        tPers.setFont(new Font("Verdana",Font.ITALIC,20));
        tPers.setMargin(new Insets(5,5,5,5));
        tPers.setEditable(false);
        JScrollPane scPers = new JScrollPane(tPers,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scPers.setBounds(30,465,440,70);

        drugCBox = new JComboBox();
        drugCBox.setBounds(110,550,245,30);
        for(DrugsEntity d : drugList) {
            drugCBox.addItem(d.getDrugName());
        }
        bConfirm = new JButton("+");
        bConfirm.setFont(new Font("Verdana",Font.BOLD,10));
        bConfirm.setBounds(365,550,50,30);
        bConfirm.addActionListener(this);
        bDelete = new JButton("-");
        bDelete.setFont(new Font("Verdana",Font.BOLD,10));
        bDelete.setBounds(420,550,50,30);
        bDelete.addActionListener(this);

        tSum = new JTextField("0.0$");
        tSum.setBounds(30,550,70,30);

        mainPanel.add(tName);
        //mainPanel.add(tBdate);
        mainPanel.add(tPesel);
        mainPanel.add(tAdress);
        mainPanel.add(lSymp);
        mainPanel.add(scSymp);
        mainPanel.add(lDiag);
        mainPanel.add(scDiag);
        mainPanel.add(lPers);
        mainPanel.add(scPers);
        mainPanel.add(bHistory);
        mainPanel.add(bAdd);
        mainPanel.add(bBack);
        mainPanel.add(bPrint);
        mainPanel.add(bSum);
        mainPanel.add(drugCBox);
        mainPanel.add(bConfirm);
        mainPanel.add(bDelete);
        mainPanel.add(tSum);

        add(mainPanel);
        setResizable(false);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                HW.dodajZmianyKartyDoBazy(HW.getText());
                System.out.println("zamykanie\n"+ HW.getText());
                setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
        //setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);



        setVisible(false);
    }

    public VisitsEntity getWizytaBiezaca() {
        return wizytaBiezaca;
    }

    public void setWizytaBiezaca(VisitsEntity wizytaBiezaca) {
        this.wizytaBiezaca = wizytaBiezaca;
    }

    public void actionPerformed (ActionEvent e)
    {
        Object src = e.getSource();
        if(src==bHistory)
        {
            HW.setVisible(true);
        }
        if(src==bAdd)
        {
            String txt;
            txt="\nObjawy: "+tSymp.getText()+"\nDiagnoza: "+tDiag.getText();
            HW.setText(txt);
            //ta funkcja dodaje zmiany w karcie do bazy
            //HW.dodajZmianyKartyDoBazy(txt);
        }
        if(src==bBack)
        {
            HW.resetText();
        }
        if(src==bConfirm)
        {

            int id = drugCBox.getSelectedIndex();
            tPers.append(drugList.get(id).getDrugName()+drugList.get(id).getPrice()+"$\n");
            selectionList.add(id);
        }
        if(src==bSum)
        {
            double s=0;
            for(int i=0;i<selectionList.size();i++)
            {
                s+=drugList.get(selectionList.get(i)).getPrice();
            }
            s=s*100;
            s=Math.round(s);
            s=s/100;
            tSum.setText(Double.toString(s)+"$");
            total=s;
        }
        if(src==bDelete && selectionList.size()>0)
        {
            selectionList.remove(selectionList.size()-1);
            tPers.setText("");
            for(int i=0;i<selectionList.size();i++)
            {
                tPers.append(drugList.get(selectionList.get(i)).getDrugName()
                        +drugList.get(selectionList.get(i)).getPrice()+"\n");
            }
        }
        if(src==bPrint)
        {
            File perscription = new File("Recepta.txt");

            try(PrintWriter out = new PrintWriter(perscription))
            {
                for(int i=0;i<selectionList.size();i++)
                {
                    out.print(drugList.get(selectionList.get(i)).getDrugName());
                }
                out.println("---------------------------------------");
                out.println(Double.toString(total));
                out.close();
            }
            catch(FileNotFoundException ex)
            {
                System.out.println("file not found");
            }
        }
    }
}

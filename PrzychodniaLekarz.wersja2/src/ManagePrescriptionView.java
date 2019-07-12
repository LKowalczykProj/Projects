//package views;

//import database.DatabaseConnection;
//import utils.Drug;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.LinkedList;

public class ManagePrescriptionView extends JFrame {

    private LinkedList<Drug> boughtDrugs;

    private JTable drugsTable;
    private JButton addButton;
    private JButton acceptButton;
    private JLabel summaryLabel;
    private JScrollPane scroll;

    private DefaultTableModel defaultTableModel;

    private Object[][] data = {};
    private Object[] columns = {"Nazwa Leku", "Cena [zł]", "Ilość"};

    private DatabaseConnection databaseConnection;

    private SearchView searchView;

    private double summary;

    public ManagePrescriptionView(SearchView searchView){
        this.searchView = searchView;
        boughtDrugs = new LinkedList<Drug>();
        databaseConnection = DatabaseConnection.getInstance();
        setupView();
        initializeComponents();
        setupListeners();
    }

    private void setupListeners() {
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {

                String drugName= JOptionPane.showInputDialog("Podaj nazwe leku ");
                String quantityStr = JOptionPane.showInputDialog("Podaj ilość");

                try {

                    if (quantityStr == null || drugName == null) return;
                    int quantity = Integer.parseInt(quantityStr);
                    LinkedList<Drug> drugs = databaseConnection.getDrug(drugName);


                    if (drugs.size() != 0 && drugs.get(0).getQuantity() >= quantity) {

                        String[] data = {drugs.get(0).getDrugName(), String.valueOf(drugs.get(0).getPrice()*quantity), quantityStr};

                        defaultTableModel.addRow(data);
                        drugs.get(0).setQuantity(quantity);
                        boughtDrugs.add(drugs.get(0));
                        summary += drugs.get(0).getPrice()*quantity;
                        summaryLabel.setText("SUMA: "+summary+"zł");

                    } else if( drugs.size() != 0 && drugs.get(0).getQuantity() < quantity){

                        JOptionPane.showMessageDialog(getParent(),"Brak. Dostępna ilość: " + drugs.get(0).getQuantity());

                    } else {

                        JOptionPane.showMessageDialog(getParent(),
                                "Brak leku", null, JOptionPane.INFORMATION_MESSAGE);

                    }

                } catch (Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(getParent(),"Podaj poprawne dane");
                }
            }
        });

        acceptButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    databaseConnection.updateQuantity(boughtDrugs);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                searchView.updateAllDrugs();
                setVisible(false);
            }

        });
    }

    private void setupView(){

        this.setLayout(new FlowLayout());
        this.setSize(600, 550);
        this.setTitle("Realizuj recepte");
        this.setLocationRelativeTo(null);
        this.setResizable(false);

    }

    private void initializeComponents(){

        defaultTableModel = new DefaultTableModel(data, columns);
        drugsTable = new JTable();
        drugsTable.setModel(defaultTableModel);
        drugsTable.setPreferredScrollableViewportSize(new Dimension(450, 450));
        add(drugsTable);

        scroll = new JScrollPane(this.drugsTable);
        this.add(scroll);

        addButton = new JButton("Znajdz lek");
        add(addButton);
        addButton.setSize(200,50);

        acceptButton = new JButton("Zatwierdź");
        acceptButton.setSize(200,50);
        add(acceptButton);

        summaryLabel = new JLabel("SUMA: 0.00zł");
        add(summaryLabel);

    }

}

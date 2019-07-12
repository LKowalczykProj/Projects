//package views;

//import database.DatabaseConnection;
//import utils.Drug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import javax.swing.table.DefaultTableModel;

public class SearchView extends JFrame{

    private MenuBar menuBar;

    private Menu prescriptions;
    private Menu orders;

    private MenuItem addDrugFromOrder;
    private MenuItem realizePrescription;

    private JTable table;
    private DefaultTableModel defaultTableModel;

    private JButton searchButton;
    private JTextField searchField;
    private JScrollPane scroll;

    private DatabaseConnection databaseConnection;

    private Object[][] data = {};
    private Object[] columns = {"Nazwa Leku", "Skład", "Cena [zł]", "Ilość dostępnych sztuk", "Grupa leków"};

    public SearchView() {

        databaseConnection = DatabaseConnection.getInstance();

        initializeComponents();
        setupFrame();

        initializeMenuBar();
        initializeTable();

        setupView();

        updateAllDrugs();

        addSearchButtonListener();
        addAutoSearchListener();
        addMenuItemsListener();

    }

    public void updateAllDrugs(){
        this.defaultTableModel.setRowCount(0);
        LinkedList<Drug> drugs = databaseConnection.getAlldrugs();
        updateFunction(drugs);
    }

    private void updateSearch(){
        this.defaultTableModel.setRowCount(0);
        LinkedList<Drug> drugs = databaseConnection.getDrug(searchField.getText());
        drugs.addAll(databaseConnection.getSameGroupDrugs(searchField.getText()));
        updateFunction(drugs);
    }

    private void updateFunction(LinkedList<Drug> drugs){
        for(Drug drug : drugs){
            String [] drugToAdd = {drug.getDrugName(),drug.getComposition(),String.valueOf(drug.getPrice()),String.valueOf(drug.getQuantity()),
                    String.valueOf(drug.getDrugGroup())};
            defaultTableModel.addRow(drugToAdd);
        }
    }
    private void initializeComponents(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new MenuBar();
        prescriptions = new Menu();
        orders = new Menu();
        addDrugFromOrder = new MenuItem();
        realizePrescription = new MenuItem();
        table = new JTable();

        defaultTableModel = new DefaultTableModel(data,columns);
        searchButton = new JButton("Szukaj");
        searchField = new JTextField(20);

        this.searchButton.setSize(100,50);

        scroll = new JScrollPane(this.table);

    }

    private void setupFrame(){

        this.setLayout(new FlowLayout());
        this.setSize(1000, 600);
        this.setTitle("Apteka");
        this.setResizable(false);

    }

    private void initializeMenuBar(){

        this.setMenuBar(this.menuBar);
        this.menuBar.add(this.prescriptions);
        this.menuBar.add(this.orders);

        this.prescriptions.setLabel("Recepty");
        this.orders.setLabel("Zamówienia");

        this.addDrugFromOrder.setLabel("Dodaj lek");
        this.realizePrescription.setLabel("Realizuj");

        this.orders.add(this.addDrugFromOrder);
        this.prescriptions.add(this.realizePrescription);

    }

    private void initializeTable(){

        defaultTableModel = new DefaultTableModel(data, columns);
        table = new JTable();
        table.setModel(defaultTableModel);

        table.setPreferredScrollableViewportSize(new Dimension(950, 450));

    }

    private void setupView(){

        this.add(this.searchButton, BorderLayout.NORTH);
        this.add(this.searchField, BorderLayout.NORTH);

        this.add(this.table, BorderLayout.CENTER);

        scroll = new JScrollPane(this.table);
        this.add(scroll);

        this.setLocationRelativeTo(null);

    }

    private void addSearchButtonListener(){

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                updateSearch();
            }
        });

    }

    private void addAutoSearchListener(){

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if(searchField.getText().equals("")){
                    updateAllDrugs();
                }
                else{
                    updateSearch();
                }

            }

            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }
        });

    }

    private void addMenuItemsListener(){
        final SearchView searchView = this;
        addDrugFromOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AddDrugView addDrugView = new AddDrugView( searchView );
                addDrugView.setVisible(true);
            }
        });
        realizePrescription.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ManagePrescriptionView managePrescriptionView = new ManagePrescriptionView(searchView);
                managePrescriptionView.setVisible(true);
            }
        });

    }

}

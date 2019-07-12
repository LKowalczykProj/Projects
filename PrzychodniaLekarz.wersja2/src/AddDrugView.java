//package views;

//import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AddDrugView extends JFrame {

    private TextField nameField;
    private TextField compositionField;
    private TextField priceField;
    private TextField quantityField;
    private TextField groupField;

    private Button addbutton;

    private Label nameLabel;
    private Label compositionLabel;
    private Label priceLabel;
    private Label quantityLabel;
    private Label groupLabel;

    private DatabaseConnection databaseConnection;

    private SearchView searchView;

    public AddDrugView(SearchView searchView) {

        this.searchView = searchView;
        databaseConnection = DatabaseConnection.getInstance();
        setupView();
        initializeComponents();
        addComponentsToView();
        addButtonAction();
    }

    private void setupView(){

        this.setLayout(new GridLayout(6,1));
        this.setSize(500, 300);
        this.setTitle("Dodaj lek");
        this.setResizable(false);
        this.setLocationRelativeTo(null);

    }

    private void initializeComponents(){

        nameField = new TextField(50);
        compositionField = new TextField(50);
        priceField = new TextField(50);
        quantityField = new TextField(50);
        groupField = new TextField(50);

        addbutton = new Button("Dodaj");

        nameLabel = new Label("Nazwa leku");
        compositionLabel = new Label("Skład");
        priceLabel = new Label("Cena");
        quantityLabel = new Label("Ilość");
        groupLabel = new Label("Grupa");
    }

    private void addComponentsToView(){

        this.add(nameLabel);
        this.add(nameField);

        this.add(compositionLabel);
        this.add(compositionField);

        this.add(priceLabel);
        this.add(priceField);

        this.add(quantityLabel);
        this.add(quantityField);

        this.add(groupLabel);
        this.add(groupField);

        this.add(addbutton,2,10);

    }

    private void addButtonAction(){
        addbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {

                    databaseConnection.addDrug(nameField.getText(),compositionField.getText(),
                            Integer.parseInt(quantityField.getText()), Float.parseFloat(priceField.getText()),
                            Integer.parseInt(groupField.getText()));

                    JOptionPane.showMessageDialog(getParent(),
                            "Dodano lek", null, JOptionPane.INFORMATION_MESSAGE);

                    nameField.setText("");
                    compositionField.setText("");
                    priceField.setText("");
                    quantityField.setText("");
                    groupLabel.setText("");

                    searchView.updateAllDrugs();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(getParent(),
                            "Wprowadź poprawne dane", "Błąd", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }
}

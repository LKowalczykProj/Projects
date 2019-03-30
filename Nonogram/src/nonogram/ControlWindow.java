/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonogram;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Kowalczyk
 */
public class ControlWindow extends JFrame{
    
    public ControlWindow(){
        super("Sterowanie");
        setSize(450,380);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setBounds(0,30,450,380);
        String txt="<html><p></p><bold><p>NOWA GRA:</p></bold><p>Start>>Nowa Gra>>Trudność>>Obrazek</p>";
        txt+="<p>Łatwy 10x10</p><p>Średni 15x15 lub 15x20</p><p>Trudny 20x20</p>";
        txt+="<p></p><p>WCZYTANIE SWOJEGO OBRAZKA:</p><p>Start>>Wczytaj Obraz...</p><p>Ta opcja otwiera okienku wyboru plikow</p>";
        txt+="<p>Obraz powinien być w formacie gif i nie przekraczać 30x30 pikseli</p>";
        txt+="<p></p><p>LOSOWA GRA</p><p>Start>>Generuj losowe...</p><p>Program generuje losowy plansze 15x15</p>";
        txt+="<p></p><p>OBSŁUGA PLANSZY</p><p>Panel w lewym górnym rogu to wybór trybu malowania</p><p>Klikając lub przeciągając myszkę zmieniamy obszar na zgodny z wzorem</p>";
        txt+="<p>Wielokrotne malowanie działa tylko w pionie i poziomie</p></html>";
        JLabel tekst = new JLabel(txt);
        tekst.setBounds(30,60,450,380);
        panel.add(tekst);
        add(panel);
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}

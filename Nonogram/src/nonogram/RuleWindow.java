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
public class RuleWindow extends JFrame{
    
    public RuleWindow()
    {
        super("Zasady");
        setSize(400,250);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setBounds(0,30,400,250);
        String txt="<html><p></p>Nonogramy to logiczna łamigłówka o prostych zasadach,</p> <p>ale wymagająca odpowiedniej strategii.</p>";
        txt+="<p></p><bold><p>ZASADY:</p></bold><p></p><p>Kwadraty na siatce należy zamalować na czarno,</p>";
        txt+="<p>albo wypełnić znakiem X.</p><p>Każdy rząd i kolumna oznaczone są licbzmi,</p>";
        txt+="<p>które reprezenrują długości ciągów czarnych pól.</p><p>Celem gry jest znalezienie i oznaczenie ich na planszy.</p></html>";
        JLabel tekst = new JLabel(txt);
        tekst.setBounds(30,60,340,300);
        panel.add(tekst);
        add(panel);
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}

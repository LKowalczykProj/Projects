/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonogram;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Kowalczyk
 */
public class VDifficulty extends JFrame{
    
    JPanel panel;
    JButton bEasy,bMedium,bHard;
    
    
    public VDifficulty()
    {
        super("Difficulty");
        setSize(200,350);
        setLocationRelativeTo(null);
        setResizable(false);
        panel = new JPanel();
        panel.setLayout(null);
        add(panel);
        bEasy = new JButton("Łatwy");
        bEasy.setBounds(50,50,80,50);
        panel.add(bEasy);
        bMedium = new JButton("Średni");
        bMedium.setBounds(50,130,80,50);
        panel.add(bMedium);
        bHard = new JButton("Trudny");
        bHard.setBounds(50,210,80,50);
        panel.add(bHard);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
            
    
    
}

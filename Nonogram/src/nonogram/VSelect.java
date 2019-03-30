/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonogram;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Kowalczyk
 */
class VSelect extends JFrame implements ActionListener{
    
    private JPanel pMain;
    
    private ArrayList<JRadioButton> RButtonList;
    private ButtonGroup group;
    public String choice;
    public JButton bConfirm;
    
    
    public VSelect(int offset)
    {
        super();
        RButtonList= new ArrayList<>();
        setLayout(null);
        setSize(100,420);
        setLocationRelativeTo(null);
        setResizable(false);
        pMain = new JPanel();
        pMain.setLayout(null);
        pMain.setSize(200,400);
        add(pMain);
        
        choice = new String("1");
        group = new ButtonGroup();
        
        for(int i=0;i<10;i++)
        {
            JRadioButton rb = new JRadioButton(Integer.toString(i+offset));
            rb.setActionCommand(Integer.toString(i+offset));
            rb.addActionListener(this);
            rb.setBounds(40, 20+i*30, 40, 30);
            group.add(rb);
            RButtonList.add(rb);
            pMain.add(rb);
        }
        RButtonList.get(0).setSelected(true);
        
        bConfirm = new JButton("Wybierz");
        bConfirm.setBounds(13, 335, 100, 25);
        pMain.add(bConfirm);
        
        
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    }
    
    public void reset_buttons(int offset)
    {
        for(int i=0;i<RButtonList.size();i++)
        {
            String temp;
            temp=Integer.toString(i+offset);
            RButtonList.get(i).setText(temp);
            RButtonList.get(i).setActionCommand(temp);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        choice=ae.getActionCommand();
    }
    
}

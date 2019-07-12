import javax.swing.*;

public class User {

    private String name, uname, pass;
    private int total,salary;
    public User(String name, String uname, String pass,int salary)
    {
        this.name=name;
        this.uname=uname;
        this.pass=pass;
        total=0;
        this.salary=salary;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public String getUname() {
        return uname;
    }

    public void printTotal()
    {
        int temp = total;
        total=0;
        JOptionPane.showMessageDialog(null, "Od ostatniego sprawdzenia zarobiono "+temp+" PLN", "Zarobki", JOptionPane.INFORMATION_MESSAGE);
    }

    public void earn()
    {
        total+=salary;
    }

    public int getSalary()
    {
        return salary;
    }
}

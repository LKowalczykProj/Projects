import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Company {

    private Accountant A;
    private ArrayList<User> users;

    public Company()
    {
        users = new ArrayList<>();
    }

    public void addAccountant(Accountant A)
    {
        this.A=A;
        activateTimer();
    }

    public void addUser(User u)
    {
        users.add(u);
    }

    public User findUser(String uname,String pass)
    {
        for(User u:users)
        {
            if(u.getUname().equals(uname) && u.getPass().equals(pass))
                return u;
        }
        return null;
    }

    public ArrayList<User> getUsers()
    {
        return users;
    }

    public void payPaycheck()
    {
        for(User u:users)
            u.earn();
    }

    private void activateTimer()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                A.requestPayment();
            }
        },5000,5000);
    }
}

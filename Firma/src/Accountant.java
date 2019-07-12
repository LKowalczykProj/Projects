import java.util.Timer;
import java.util.TimerTask;

public class Accountant {

    private Company C;
    private BankAccount BA;
    public Accountant()
    {

    }

    public void addCompany(Company C)
    {
        this.C=C;
        activateTimer();
    }

    public void addBankAccount(BankAccount BA)
    {
        this.BA=BA;
    }

    public void requestPayment()
    {

            int total=0;
            for(User u: C.getUsers())
            {
                total+=u.getSalary();
            }
            if(total<=BA.getMoney())
            {
                BA.withdraw(total);
                C.payPaycheck();
            }
            System.out.println(BA.getMoney());
    }

    private void activateTimer()
    {

        Timer earnTimer = new Timer();
        earnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BA.deposit(100000000);
            }
        },15000,15000);
    }
}

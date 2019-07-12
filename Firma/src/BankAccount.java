public class BankAccount {

    private Long money;
    public BankAccount()
    {
        money=200000000000L;
    }

    public void withdraw(int x)
    {
        money-=x;
    }

    public void deposit(int x)
    {
        money+=x;
    }

    public Long getMoney() {
        return money;
    }
}

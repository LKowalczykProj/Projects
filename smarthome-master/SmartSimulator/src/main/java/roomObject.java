public class roomObject extends buildObject{

    private double temp,humid;
    private boolean people;
    private boolean peopleChanged;
    public roomObject(int x,int y,String name)
    {
        super(x,y,name);
        temp=20.0;
        humid=50.0;
        people=false;
        peopleChanged = false;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
        if(temp>30)
            this.temp=30;
        if(temp<-20)
            this.temp=-20;
    }

    public double getHumid() {
        return humid;
    }

    public void setHumid(double humid) {
        this.humid = humid;
        if(humid<0)
            this.humid=0;
        if(humid>100)
            this.humid=100;
    }

    public boolean getPeople()
    {
        return people;
    }

    public void setPeople(boolean people) {
        if(people != this.people)
            peopleChanged = true;
        this.people = people;
    }

    public boolean isPeopleChanged() {
        if(peopleChanged) {
            peopleChanged = false;
            return true;
        }
        return false;
    }
}

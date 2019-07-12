public class doorObject extends buildObject {

    private boolean vertical;
    private String d1,d2;
    private boolean state;
    public doorObject(int x,int y, String name)
    {
        super(x,y,name);
        vertical=true;
        d1="Null"; //left-up
        d2="Null"; //right-down
        state=false;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean getVertical()
    {
        return vertical;
    }

    public void setD1(String d1) {
        this.d1 = d1;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }

    public String getD1() {
        return d1;
    }

    public String getD2() {
        return d2;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

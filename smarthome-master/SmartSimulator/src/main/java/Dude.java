public class Dude {

    private int x,y;
    private String direction;
    private int inside;
    public Dude(int x,int y)
    {
        this.x=x;
        this.y=y;
        direction="vertical";
        inside=-1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDirection() {
        return direction;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void moveX(int x)
    {
        this.x+=x;
    }

    public void moveY(int y)
    {
        this.y+=y;
    }

    public int getInside() {
        return inside;
    }

    public void setInside(int inside) {
        this.inside = inside;
    }
}

public class buildObject {

    private int posX,posY,id;
    private String name;
    public buildObject(int x,int y,String name)
    {
        posX=x;
        posY=y;
        this.name=name;
        id=0;
    }

    public int getX()
    {
        return posX;
    }

    public int getY()
    {
        return posY;
    }

    public String getName() {
        return name;
    }

    public void move(int x,int y)
    {
        posX+=x;
        posY+=y;
    }

    public void moveTo(int x,int y)
    {
        posX=x;
        posY=y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

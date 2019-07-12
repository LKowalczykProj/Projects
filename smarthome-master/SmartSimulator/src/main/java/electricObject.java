public class electricObject extends buildObject {

    private boolean state;
    private int roomId;
    private double dim;
    public electricObject(int x, int y, String name)
    {
        super(x,y,name);
        state=false;
        roomId=-1;
        dim=1;
    }

    public void setState(boolean on)
    {
        state=on;
    }

    public boolean getState()
    {
        return state;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public double getDim() {
        return dim;
    }

    public void setDim(double dim) {
        this.dim = dim;
    }
}

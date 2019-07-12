import java.sql.Connection;

public class Course {

    private String name;
    private int id;
    public Course(String name, int id)
    {
        this.name=name;
        this.id=id;
    }

    public int getId()
    {
        return id;
    }

    public String getName() { return name; }

    public void save(Connection myConn)
    {

    }
}

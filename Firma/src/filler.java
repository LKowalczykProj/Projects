import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Random;

public class filler {

    Connection myConn;

    public filler() {
        try {
            myConn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "*888rootSQL");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Marek");
        nameList.add("Piotr");
        nameList.add("Janusz");
        nameList.add("Kamil");
        nameList.add("Mieczysław");
        nameList.add("Gustaw");
        nameList.add("Karolina");
        nameList.add("Łucja");
        nameList.add("Agata");
        nameList.add("Weronika");
        nameList.add("Klaudia");
        nameList.add("Barbara");
        nameList.add("Leokadia");
        nameList.add("Grzegorz");
        nameList.add("Natalia");
        nameList.add("Andrzej");
        nameList.add("Adam");
        nameList.add("Ewa");
        nameList.add("Katarzyna");

        ArrayList<String> surnameList = new ArrayList<>();
        surnameList.add("Nowak");
        surnameList.add("Kowalik");
        surnameList.add("Goździk");
        surnameList.add("Chrząszcz");
        surnameList.add("Wnuk");
        surnameList.add("Szcerbiec");
        surnameList.add("Kowalczyk");
        surnameList.add("Wójcik");
        surnameList.add("Woźniak");
        surnameList.add("Kaczmarek");
        surnameList.add("Krawczyk");
        surnameList.add("Zając");
        surnameList.add("Wróbel");
        surnameList.add("Klocek");

        Random generator  = new Random();
        String query ="insert into Users "+
                "(Username,Name,Pass,Type) "+
                "values (?,?,?,?)";
        for(int i=0;i<1000;i++)
        {
            String type, username, name="",pass;
            int k=generator.nextInt(100);
            type="Analyst";
            if(k<5)
                type="CEO";
            if(k>49)
                type="Programmer";

            k=generator.nextInt(nameList.size());
            name+=nameList.get(k);
            name+=" ";
            k=generator.nextInt(surnameList.size());
            name+=surnameList.get(k);
            k=generator.nextInt(10)+5;
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for(int j=0;j<k;j++)
            {
                char z = (char) (generator.nextInt(26)+97);
                sb1.append(z);
            }
            k=generator.nextInt(10)+5;
            for(int j=0;j<k;j++)
            {
                char z = (char) (generator.nextInt(26)+97);
                sb2.append(z);
            }
            username=sb1.toString();
            pass=sb2.toString();

            try {
                PreparedStatement ps = myConn.prepareStatement(query);
                ps.setString(1,username);
                ps.setString(2,name);
                ps.setString(3,pass);
                ps.setString(4,type);
                ps.executeUpdate();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

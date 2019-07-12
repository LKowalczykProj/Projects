import entities.VisitsEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import java.sql.Timestamp;

public class VisitDAO {



    public VisitsEntity findVisitByDateAndDoktorId(Timestamp dataOrazCzas, int idLekarza){
        Session session = HibernateUtility.getSession();
        Query<VisitsEntity> query =session.createQuery(
                "from VisitsEntity v WHERE v.visitDate = :data AND v.doctorId =:idDoka", VisitsEntity.class)
                .setParameter("data", dataOrazCzas).setParameter("idDoka", idLekarza);
        VisitsEntity visitsEntity=null;
        try {
            visitsEntity = query.getSingleResult();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "Ten lekarz nie ma wizyty na ta godzine!", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println("Time stamp from parameter");
        System.out.println(dataOrazCzas.getTime());
        System.out.println("Time stamp from db");
        //System.out.println(visitsEntity.getVisitDate().getTime());

        session.close();
        return visitsEntity;
    }


}

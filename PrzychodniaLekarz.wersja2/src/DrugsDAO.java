import entities.DrugsEntity;
import org.hibernate.Session;

import java.util.List;

public class DrugsDAO {

    public List<DrugsEntity> getAllDrugs(){
        Session session = HibernateUtility.getSession();
        List<DrugsEntity> listaLekow = session.createQuery("FROM DrugsEntity").list();
        session.close();
        return listaLekow;
    }
}

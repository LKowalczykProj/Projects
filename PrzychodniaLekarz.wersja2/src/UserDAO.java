import entities.UsersEntity;
import org.hibernate.Session;

public class UserDAO {

    public UsersEntity findPatientById(int id){
       Session session = HibernateUtility.getSession();
        UsersEntity user= session.get(UsersEntity.class, id);
        session.close();
        return user;
    }
    public UsersEntity findDoctorByPesel(String peselDoktora){
        Session session = HibernateUtility.getSession();
        UsersEntity doktor = (UsersEntity) session.createQuery("FROM UsersEntity u where u.pesel= :peselSzukanego")
                .setParameter("peselSzukanego", peselDoktora).list().get(0);
        session.close();
        return doktor;
    }
}

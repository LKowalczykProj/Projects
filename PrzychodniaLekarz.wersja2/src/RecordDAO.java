import entities.RecordsEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Calendar;
import java.util.List;

public class RecordDAO {

    public RecordsEntity getMedicalHistory(int idPacjenta, int idLekarza){
        Session session = HibernateUtility.getSession();
        //jesli nie ma takiej historii choroby
        if(session.createQuery("FROM RecordsEntity r WHERE r.patientId =: idDlaSzukania")
                .setParameter("idDlaSzukania", idPacjenta).list().isEmpty()){
            Transaction tx = session.beginTransaction();

            RecordsEntity nowaHistoria =new RecordsEntity();

            nowaHistoria.setRecord(" ");
            nowaHistoria.setModifiedDate( new java.sql.Date(Calendar.getInstance().getTime().getTime()));
            nowaHistoria.setDoctorId(idLekarza);
            nowaHistoria.setPatientId(idPacjenta);
            nowaHistoria.setImageId(1);
            session.saveOrUpdate(nowaHistoria);
            session.flush();
            tx.commit();
            return nowaHistoria;

        }
        List<RecordsEntity> historia = (List<RecordsEntity>) session.createQuery("FROM RecordsEntity r WHERE r.patientId =: iddlaszukania2")
                .setParameter("iddlaszukania2", idPacjenta).list();
        session.close();
        return historia.get(0);
    }
    public void updateMedicalHistory(String textWejściowy, RecordsEntity stareHistoriaChoroby){
        RecordsEntity nowaHistoriaChoroby = stareHistoriaChoroby;
        nowaHistoriaChoroby.setRecord(stareHistoriaChoroby.getRecord()+" \n"+textWejściowy);
        nowaHistoriaChoroby.setModifiedDate(new java.sql.Date(Calendar.getInstance().getTime().getTime())); //dzisiejsza data
        Session session = HibernateUtility.getSession();
        Transaction tx = session.beginTransaction();
        session.update(nowaHistoriaChoroby);
        tx.commit();
        session.close();

    }

}


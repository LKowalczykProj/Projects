/*
    Autor: Łukasz Kowalczyk

    Projekt przychodni - Moduł Lekarz
 */

import entities.UsersEntity;
import entities.VisitsEntity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class doctorController {

    public doctorWindow DW;
    public KartaPacjenta kartaPacjenta;
    public DateFormat dateFormat;
    public int shift;
    boolean clickedOnOknoCzasu=false;
    static UsersEntity biezacyDoktor;
    public class ButtonControler implements ActionListener{
        Calendar dataPrzekonwertowana = new GregorianCalendar();
        public void actionPerformed(ActionEvent e)
        {
            clickedOnOknoCzasu=true;
            Object src=e.getSource();
            if(src== DW.bConfirm)
            {
                String finalDate = Integer.toString(8+ DW.current/2);
                if(DW.current%2==0)
                    finalDate+="-00-";
                else
                    finalDate+="-30-";
                Calendar date = Calendar.getInstance();
                date.add(Calendar.DATE,shift);
                // System.out.println(date);
                finalDate+= DW.lDate.getText();
                //konwertacja daty ze stringa do Calendar
                int year, month, day, hour, minute;
                String tablicaParametrowCzasu []= finalDate.split("-");
                hour = Integer.valueOf(tablicaParametrowCzasu[0]);
                minute = Integer.valueOf(tablicaParametrowCzasu[1]);
                day = Integer.valueOf(tablicaParametrowCzasu[2]);
                month = Integer.valueOf(tablicaParametrowCzasu[3] );
                year = Integer.valueOf(tablicaParametrowCzasu[4]);
                dataPrzekonwertowana.set(year,month-1,day,hour,minute, 0);  //-1 bo miesiący od zera
                dataPrzekonwertowana.set(Calendar.SECOND, 0);
                dataPrzekonwertowana.set(Calendar.MILLISECOND,0);

                 //wyciąganie wizyty z bazy danych
                Timestamp czasOrazDataWizyty = new Timestamp(dataPrzekonwertowana.getTimeInMillis());
                VisitDAO visitDA = new VisitDAO();
                VisitsEntity wizyta = visitDA.findVisitByDateAndDoktorId(czasOrazDataWizyty, biezacyDoktor.getId());
                //przekazanie wizyty do karty jesli taka wizytaBiezaca jest w bazie
                if(wizyta!=null) {
                    kartaPacjenta = new KartaPacjenta(wizyta, czasOrazDataWizyty);
                    kartaPacjenta.setVisible(true);
                }

            }
        }
    }

    public doctorController(UsersEntity zalogowanyDoktor)
    {
        this.biezacyDoktor = zalogowanyDoktor;
        shift=0;
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ButtonControler BC = new ButtonControler();
        DW = new doctorWindow(dateFormat);


        DW.bConfirm.addActionListener(BC);
    }
}

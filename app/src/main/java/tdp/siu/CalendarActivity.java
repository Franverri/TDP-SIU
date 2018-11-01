package tdp.siu;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    WeekView mWeekView;
    List<WeekViewEvent> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_calendar);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
             @Override
             public String interpretDate(Calendar date) {
                 SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                 String weekday = weekdayNameFormat.format(date.getTime());
                 return weekday.toUpperCase();
             }

             @Override
             public String interpretTime(int hour) {
                 return String.valueOf(hour);
             }
         });

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                return getEvents(newYear, newMonth);
            }
        });

        //acomodarInicioCalendario();

        Calendar inicio = Calendar.getInstance();
        inicio.set(Calendar.DAY_OF_MONTH, 29);
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.MONTH, 9);
        inicio.set(Calendar.YEAR, 2018);
        mWeekView.goToDate(inicio);
    }

    private List<WeekViewEvent> getEvents(int year, int month) {

        Log.d("PRUEBAA", "YEAR: " + year);
        Log.d("PRUEBAA", "MONTH: " + month);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        Calendar timeCierre = Calendar.getInstance();
        timeCierre.set(Calendar.DAY_OF_MONTH, 30);
        timeCierre.set(Calendar.HOUR_OF_DAY, 0);
        timeCierre.set(Calendar.MINUTE, 0);
        timeCierre.set(Calendar.MONTH, 10);
        timeCierre.set(Calendar.YEAR, 2018);

        if(today.get(Calendar.MONTH)+1 != month){
            return new ArrayList<>();
        }

        int id = 0;

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 31);
        startTime.set(Calendar.HOUR_OF_DAY, 1);
        startTime.set(Calendar.MINUTE, 1);
        startTime.set(Calendar.MONTH, 9);
        startTime.set(Calendar.YEAR, 2018);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 31);
        endTime.set(Calendar.HOUR_OF_DAY, 4);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.MONTH, 9);
        endTime.set(Calendar.YEAR, 2018);
        WeekViewEvent event = new WeekViewEvent(id, "Materia 1", startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorAccent));
        events.add(event);

        agregarEvento(2, 10, 2018, 1, 4);

        return events;
    }

    private void agregarEvento(int dia, int mes, int año, int horaInicio, int horaCierre) {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, dia);
        startTime.set(Calendar.HOUR_OF_DAY, horaInicio);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, mes);
        startTime.set(Calendar.YEAR, año);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, dia);
        endTime.set(Calendar.HOUR_OF_DAY, horaCierre);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.MONTH, mes);
        endTime.set(Calendar.YEAR, año);
        WeekViewEvent event = new WeekViewEvent(0, "Materia 1", startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorAccent));
        events.add(event);
    }


    private boolean estaEnCursada(Date time, Date timeCierre) {
        boolean estaEnCursada;
        if(time.before(timeCierre)){
            estaEnCursada = true;
        } else {
            estaEnCursada = false;
        }
        return estaEnCursada;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }
}

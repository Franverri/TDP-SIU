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

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                return getEvents(newYear, newMonth);
            }
        });


    }

    private void agregarEventos() {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 31);
        startTime.set(Calendar.HOUR_OF_DAY, 1);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 9);
        startTime.set(Calendar.YEAR, 2018);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 31);
        endTime.set(Calendar.HOUR_OF_DAY, 4);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.MONTH, 9);
        endTime.set(Calendar.YEAR, 2018);
        WeekViewEvent event = new WeekViewEvent(0, "Evento 1", startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorAccent));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(2018, 9, 30, 6, 00);
        endTime = Calendar.getInstance();
        endTime.set(2018, 9, 30, 9, 00);
        WeekViewEvent event2 = new WeekViewEvent(0,"Evento 2",startTime, endTime);
        event2.setColor(getResources().getColor(R.color.colorPrimary));
        events.add(event2);
    }

    private List<WeekViewEvent> getEvents(int year, int month) {
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        Log.i("PRUEBAA", "Mes 1: " + today.get(Calendar.MONTH));
        Log.i("PRUEBAA", "Mes 2: " + month);
        if(today.get(Calendar.MONTH)+1 != month){
            return new ArrayList<>();
        }

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 31);
        startTime.set(Calendar.HOUR_OF_DAY, 1);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 9);
        startTime.set(Calendar.YEAR, 2018);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 31);
        endTime.set(Calendar.HOUR_OF_DAY, 4);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.MONTH, 9);
        endTime.set(Calendar.YEAR, 2018);
        WeekViewEvent event = new WeekViewEvent(0, "Epoca", startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorAccent));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(2018, 9, 30, 6, 00);
        endTime = Calendar.getInstance();
        endTime.set(2018, 9, 30, 9, 00);
        WeekViewEvent event2 = new WeekViewEvent(0,"00kjbhjbhjbjbhj",startTime, endTime);
        event2.setColor(getResources().getColor(R.color.colorPrimary));
        events.add(event2);

        return events;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }
}

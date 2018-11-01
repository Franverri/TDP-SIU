package tdp.siu;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    String strHorarios, strDias, strNombres;

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

        Bundle b = getIntent().getExtras();
        if(b != null){
            strNombres = b.getString("strNombres");
            Log.i("PRUEBAA", "Nombres: " + strNombres);
            strHorarios = b.getString("strHorarios");
            Log.i("PRUEBAA", "Horarios: " + strHorarios);
            strDias = b.getString("strDias");
            Log.i("PRUEBAA", "Dias: " + strDias);
        }

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

        acomodarInicioCalendario();
    }

    private void acomodarInicioCalendario() {

        Calendar inicio = Calendar.getInstance();
        inicio.setTime(new Date());

        switch (inicio.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
                break;
            case Calendar.TUESDAY:
                inicio.add(Calendar.DAY_OF_YEAR, -1);
                mWeekView.goToDate(inicio);
                break;
            case Calendar.WEDNESDAY:
                inicio.add(Calendar.DAY_OF_YEAR, -2);
                mWeekView.goToDate(inicio);
                break;
            case Calendar.THURSDAY:
                inicio.add(Calendar.DAY_OF_YEAR, -3);
                mWeekView.goToDate(inicio);
                break;
            case Calendar.FRIDAY:
                inicio.add(Calendar.DAY_OF_YEAR, -4);
                mWeekView.goToDate(inicio);
                break;
            case Calendar.SATURDAY:
                inicio.add(Calendar.DAY_OF_YEAR, -5);
                mWeekView.goToDate(inicio);
                break;
            case Calendar.SUNDAY:
                inicio.add(Calendar.DAY_OF_YEAR, -6);
                mWeekView.goToDate(inicio);
                break;
        }

        //determine height and width
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        mWeekView.goToHour(7);
        mWeekView.setHourHeight(height/20);
    }

    private List<WeekViewEvent> getEvents(int year, int month) {

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        if(today.get(Calendar.MONTH)+1 != month){
            return new ArrayList<>();
        }

        List<String> listDias = Arrays.asList(strDias.split(";"));
        List<String> listHorarios = Arrays.asList(strHorarios.split(";"));
        List<String> listNombres = Arrays.asList(strNombres.split(";"));

        for (int i = 0; i < listDias.size(); i++) {

            Calendar diaCalculado = calcularDia(listDias.get(i));

            agregarEvento(listNombres.get(0), //VER COMO SACAR ESTE HARDCODEO
                    diaCalculado.get(Calendar.DAY_OF_MONTH),
                    diaCalculado.get(Calendar.MONTH),
                    diaCalculado.get(Calendar.YEAR),
                    Integer.valueOf(listHorarios.get(i).substring(0,2)),
                    Integer.valueOf(listHorarios.get(i).substring(3,5)),
                    Integer.valueOf(listHorarios.get(i).substring(6,8)),
                    Integer.valueOf(listHorarios.get(i).substring(9,11)));
        }

        return events;
    }

    private Calendar calcularDia(String s) {
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(new Date());

        switch (inicio.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
                if(s.equals("lunes")){

                } else if(s.equals("martes")) {
                    inicio.add(Calendar.DAY_OF_YEAR, 1);
                } else if(s.equals("miercoles")){
                    inicio.add(Calendar.DAY_OF_YEAR, 2);
                } else if(s.equals("jueves")){
                    inicio.add(Calendar.DAY_OF_YEAR, 3);
                } else if(s.equals("viernes")){
                    inicio.add(Calendar.DAY_OF_YEAR, 4);
                } else if(s.equals("sabado")){
                    inicio.add(Calendar.DAY_OF_YEAR, 5);
                }
                break;
            case Calendar.TUESDAY:
                if(s.equals("lunes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -1);
                } else if(s.equals("martes")) {

                } else if(s.equals("miercoles")){
                    inicio.add(Calendar.DAY_OF_YEAR, 1);
                } else if(s.equals("jueves")){
                    inicio.add(Calendar.DAY_OF_YEAR, 2);
                } else if(s.equals("viernes")){
                    inicio.add(Calendar.DAY_OF_YEAR, 3);
                } else if(s.equals("sabado")){
                    inicio.add(Calendar.DAY_OF_YEAR, 4);
                }
                break;
            case Calendar.WEDNESDAY:
                if(s.equals("lunes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -2);
                } else if(s.equals("martes")) {
                    inicio.add(Calendar.DAY_OF_YEAR, -1);
                } else if(s.equals("miercoles")){

                } else if(s.equals("jueves")){
                    inicio.add(Calendar.DAY_OF_YEAR, 1);
                } else if(s.equals("viernes")){
                    inicio.add(Calendar.DAY_OF_YEAR, 2);
                } else if(s.equals("sabado")){
                    inicio.add(Calendar.DAY_OF_YEAR, 3);
                }
                break;
            case Calendar.THURSDAY:
                if(s.equals("lunes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -3);
                } else if(s.equals("martes")) {
                    inicio.add(Calendar.DAY_OF_YEAR, -2);
                } else if(s.equals("miercoles")){
                    inicio.add(Calendar.DAY_OF_YEAR, -1);
                } else if(s.equals("jueves")){

                } else if(s.equals("viernes")){
                    inicio.add(Calendar.DAY_OF_YEAR, 1);
                } else if(s.equals("sabado")){
                    inicio.add(Calendar.DAY_OF_YEAR, 2);
                }
                break;
            case Calendar.FRIDAY:
                if(s.equals("lunes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -4);
                } else if(s.equals("martes")) {
                    inicio.add(Calendar.DAY_OF_YEAR, -3);
                } else if(s.equals("miercoles")){
                    inicio.add(Calendar.DAY_OF_YEAR, -2);
                } else if(s.equals("jueves")){
                    inicio.add(Calendar.DAY_OF_YEAR, -1);
                } else if(s.equals("viernes")){

                } else if(s.equals("sabado")){
                    inicio.add(Calendar.DAY_OF_YEAR, 1);
                }
                break;
            case Calendar.SATURDAY:
                if(s.equals("lunes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -5);
                } else if(s.equals("martes")) {
                    inicio.add(Calendar.DAY_OF_YEAR, -4);
                } else if(s.equals("miercoles")){
                    inicio.add(Calendar.DAY_OF_YEAR, -3);
                } else if(s.equals("jueves")){
                    inicio.add(Calendar.DAY_OF_YEAR, -2);
                } else if(s.equals("viernes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -1);
                } else if(s.equals("sabado")){

                }
                break;
            case Calendar.SUNDAY:
                if(s.equals("lunes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -6);
                } else if(s.equals("martes")) {
                    inicio.add(Calendar.DAY_OF_YEAR, -5);
                } else if(s.equals("miercoles")){
                    inicio.add(Calendar.DAY_OF_YEAR, -4);
                } else if(s.equals("jueves")){
                    inicio.add(Calendar.DAY_OF_YEAR, -3);
                } else if(s.equals("viernes")){
                    inicio.add(Calendar.DAY_OF_YEAR, -2);
                } else if(s.equals("sabado")){
                    inicio.add(Calendar.DAY_OF_YEAR, -1);
                }
                break;
        }

        return inicio;
    }

    private void agregarEvento(String nombre, int dia, int mes, int año, int horaInicio, int minInicio, int horaCierre, int minCierre) {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, dia);
        startTime.set(Calendar.HOUR_OF_DAY, horaInicio);
        startTime.set(Calendar.MINUTE, minInicio);
        startTime.set(Calendar.MONTH, mes);
        startTime.set(Calendar.YEAR, año);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, dia);
        endTime.set(Calendar.HOUR_OF_DAY, horaCierre);
        endTime.set(Calendar.MINUTE, minCierre);
        endTime.set(Calendar.MONTH, mes);
        endTime.set(Calendar.YEAR, año);
        WeekViewEvent event = new WeekViewEvent(0, nombre, startTime, endTime);
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

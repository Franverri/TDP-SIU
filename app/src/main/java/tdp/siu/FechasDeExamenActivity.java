package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FechasDeExamenActivity extends AppCompatActivity implements FechasDeExamenAdapter.ActualizadorFechas{
    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/docente/";
    int idCurso = -1;

    boolean periodoHabilitado;
    DateTime inicioFinales;
    DateTime finFinales;


    String DATE_PATTERN = "dd/MM/yyyy HH:mm";
    int SEPARACION_DIAS = 4;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    List<FechaExamen> fechasList;
    FechasDeExamenAdapter adapter;
    RecyclerView recyclerView;

    private DateValidator dateValidator;
    private TimeValidator timeValidator;

    //Para cuando recibo un Intent desde AlumnosInscriptosFinalActivity porque se cerró una fecha
    @Override
    protected void onNewIntent(Intent intent) {
        enviarRequestGetFechas();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recuperar el id del curso en el cual se hizo click
        Bundle b = getIntent().getExtras();
        if(b != null)
            idCurso = b.getInt("id");

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        periodoHabilitado = sharedPref.getBoolean("estaEnCursadas", false);
        inicioFinales = obtenerDateTime(sharedPref.getString("diaInicioFinales",null),sharedPref.getString("horaInicioFinales",null));
        finFinales = obtenerDateTime(sharedPref.getString("diaFinFinales",null),sharedPref.getString("horaFinFinales",null));
        Log.i("PERIODOS","PERIODO FINALES: " + inicioFinales.toString(DATE_PATTERN) +" - "+ finFinales.toString(DATE_PATTERN));

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_fechas_examen);

        configurarHTTPRequestSingleton();

        configurarBotonNuevaFecha();

        configurarRecyclerView();

        dateValidator = new DateValidator();
        timeValidator = new TimeValidator();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private DateTime obtenerDateTime(String date, String time){
        SimpleDateFormat formato = new SimpleDateFormat(DATE_PATTERN);
        String formattedString = date + " " + time;
        try {
            return new DateTime(formato.parse(formattedString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void configurarBotonNuevaFecha() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_nuevaFecha);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(periodoHabilitado){
                    DialogAgregarFecha();
                } else {
                    Toast.makeText(FechasDeExamenActivity.this, "No se encuentra habilitado el periodo de cursadas", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void DialogAgregarFecha(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Fecha de Examen");
        builder.setMessage("Inicio: " + inicioFinales.toString(DATE_PATTERN) + "\nFin: " +finFinales.toString(DATE_PATTERN));

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.nueva_fecha_examen_dialog,(ViewGroup) findViewById(android.R.id.content), false);
        // Set up the input
        final EditText inputFecha = (EditText) viewInflated.findViewById(R.id.fecha_examen);
        final EditText inputHora = (EditText) viewInflated.findViewById(R.id.hora_examen);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancelar", null);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fecha = inputFecha.getText().toString();
                        String time = inputHora.getText().toString();
                        if (dateValidator.validate(fecha)){
                            if (timeValidator.validate(time)){
                                //FORMATO VALIDADO
                                if (verificarFechas(fecha,time)) {
                                    enviarRequestAgregarFecha(fecha, time);
                                    dialog.dismiss();
                                }
                            } else{
                                Toast.makeText(FechasDeExamenActivity.this, "Hora inválida", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(FechasDeExamenActivity.this, "Fecha inválida", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private boolean verificarFechas(String fecha, String time){
        DateTime newFinalDateTime = obtenerDateTime(fecha,time);
        //Verifica que la fecha corresponda al periodo de finales
        int daysFromInicioFinales = Days.daysBetween(inicioFinales,newFinalDateTime).getDays();
        int hoursFromInicioFinales = Hours.hoursBetween(inicioFinales,newFinalDateTime).getHours();
        int minutesFromInicioFinales = Minutes.minutesBetween(inicioFinales,newFinalDateTime).getMinutes();
        int daysToFinFinales = Days.daysBetween(newFinalDateTime,finFinales).getDays();
        int hoursToFinFinales = Hours.hoursBetween(newFinalDateTime,finFinales).getHours();
        int minutestoFinFinales = Minutes.minutesBetween(newFinalDateTime,finFinales).getMinutes();
        if (daysFromInicioFinales < 0 || hoursFromInicioFinales < 0 || minutesFromInicioFinales < 0 || daysToFinFinales < 0 || hoursToFinFinales < 0 || minutestoFinFinales < 0 ){
            Toast.makeText(FechasDeExamenActivity.this, "La fecha y hora ingresadas no están comprendidas en el período de finales", Toast.LENGTH_LONG).show();
            return false;
        }
        //Verifica separación entre las fechas de final
        for (int i = 0; i < fechasList.size(); i++) {
            FechaExamen currentFinal = fechasList.get(i);
            DateTime currentFinalDateTime = obtenerDateTime(currentFinal.getFecha(),currentFinal.getHora());
            int days = Days.daysBetween(newFinalDateTime,currentFinalDateTime).getDays();
            if ((days > -SEPARACION_DIAS) && (days < SEPARACION_DIAS)){
                Toast.makeText(FechasDeExamenActivity.this, "No cumple con la separación reglamentaria de fechas (96 hs)", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public void enviarRequestAgregarFecha(final String fecha, final String hora){
        String url = APIUrl + "finales";
        Log.i("API", "url: " + url);
        JSONObject request = new JSONObject();
        try{
            request.put("id_curso", idCurso);
            request.put("fecha", fecha);
            request.put("hora", hora);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        agregarFecha(response, fecha, hora);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(FechasDeExamenActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void agregarFecha(JSONObject response, String fecha, String hora){
        try{
            boolean estado = response.getBoolean("estado");
            if (estado){
                //TODO Verificar que funcione así
                //fechasList.add(new FechaExamen(id, fecha, hora));
                //adapter.notifyDataSetChanged();
                enviarRequestGetFechas();
            }
            else {
                Toast.makeText(FechasDeExamenActivity.this, "No fue posible agregar la fecha, error en el servidor",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e){
            Log.i("JSON","Error al parsear el JSON");
        }
    }

    public void enviarRequestGetFechas() {

        String url = APIUrl + "finales/" + idCurso;
        Log.i("API", "url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        actualizarFechas(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(FechasDeExamenActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void actualizarFechas(JSONObject response){
        fechasList.clear();
        try {
            JSONArray fechasFinal = response.getJSONArray("fechas");
            int cantFechas = fechasFinal.length();
            for (int i = 0; i < cantFechas; i++) {
                JSONObject fechaFinal = fechasFinal.getJSONObject(i);
                String idFinal = fechaFinal.getString("id_final");
                String fecha = fechaFinal.getString("fecha");
                String hora = fechaFinal.getString("hora");
                String inscriptos = fechaFinal.getString("cantidad");
                fechasList.add(new FechaExamen(idFinal, fecha, hora, inscriptos));
            }
            recyclerView.setAdapter(adapter);
            if (cantFechas == 0){
                Toast.makeText(FechasDeExamenActivity.this, "No hay fechas de final registradas",
                        Toast.LENGTH_LONG).show();
            }
        }catch (JSONException e){
            Log.i("JSON","Error al parsear JSON");
        }
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_fechas_examen);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        fechasList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new FechasDeExamenAdapter(this, fechasList, queue, this);

        //Aca se manda el request al server
        enviarRequestGetFechas();

        recyclerView.setAdapter(adapter);
    }

    private void configurarHTTPRequestSingleton() {

        // Get RequestQueue Singleton
        queue = HTTPRequestSingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

        // Start the queue
        queue.start();
    }

    public class DateValidator{

        private Pattern pattern;
        private Matcher matcher;

        private static final String DATE_PATTERN =
                "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d\\d)";

        public DateValidator(){
            pattern = Pattern.compile(DATE_PATTERN);
        }

        /**
         * Validate date format with regular expression
         * @param date date address for validation
         * @return true valid date fromat, false invalid date format
         */
        public boolean validate(final String date){
            matcher = pattern.matcher(date);
            if(matcher.matches()){
                matcher.reset();
                if(matcher.find()){
                    String day = matcher.group(1);
                    String month = matcher.group(2);
                    int year = Integer.parseInt(matcher.group(3));

                    if (day.equals("31") &&
                            (month.equals("4") || month .equals("6") || month.equals("9") ||
                                    month.equals("11") || month.equals("04") || month .equals("06") ||
                                    month.equals("09"))) {
                        return false; // only 1,3,5,7,8,10,12 has 31 days
                    } else if (month.equals("2") || month.equals("02")) {
                        //leap year
                        if(year % 4==0){
                            if(day.equals("30") || day.equals("31")){
                                return false;
                            }else{
                                return true;
                            }
                        }else{
                            if(day.equals("29")||day.equals("30")||day.equals("31")){
                                return false;
                            }else{
                                return true;
                            }
                        }
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
    }

    public class TimeValidator{

        private Pattern pattern;
        private Matcher matcher;

        private static final String TIME_PATTERN =
                "([01][0-9]|2[0-3]):([0-5][0-9])";

        public TimeValidator(){
            pattern = Pattern.compile(TIME_PATTERN);
        }

        public boolean validate(final String time){
            matcher = pattern.matcher(time);
            return matcher.matches();
        }
    }
}

package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityAlumno extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";
    ProgressDialog progress;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    NavigationView navigationView;

    String padron, nombre, mail;
    String prioridad, cantEncuestas;
    String fechaInicioInscripcion, fechaCierreInscripcion, fechaInicioDesinscripcion, fechaCierreDesinscripcion, fechaInicioCursada, fechaCierreCursada ,fechaInicioFinales, fechaCierreFinales;
    String diaInscripcion, diaFinInscripcion, horaInscripcion, horaFinInscripcion;
    String diaDesinscripcion, diaFinDesinscripcion, horaDesinscripcion, horaFinDesinscripcion;
    String diaCursada, diaFinCursada, horaCursada, horaFinCursada;
    String diaFinales, diaFinFinales, horaFinales, horaFinFinales;
    String diaActualizacion;
    Boolean estaEnInscripcion, estaEnDesinscripcion, estaEnCursada ,estaEnFinales;
    String codigoCarreras, nombreCarreras;
    String codigoMateria, nombreMateria, idMateriaSeleccionada, nombreMateriaSeleccionada, codigoMateriaSeleccionada;
    String idCarreraSeleccionada, nombreCarreraSeleccionada;
    String strListEncuestas, strCodigosEncuestas, strNombreEncuestas, strIDEncuestas;
    String[] listCodigosEncuestas, listNombresEncuestas, listIDEncuestas, listEncuestasFinal;
    boolean multiCarrera, esRegular;

    TextView tvEncuestas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Topic Firebase
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        suscribeTopicsGuardados();
        //Los básicos que estan ya en la base para 95812
        /*
        FirebaseMessaging.getInstance().subscribeToTopic("curso1");
        FirebaseMessaging.getInstance().subscribeToTopic("curso7");
        FirebaseMessaging.getInstance().subscribeToTopic("curso4");
        FirebaseMessaging.getInstance().subscribeToTopic("final16");
        FirebaseMessaging.getInstance().subscribeToTopic("final31");
        FirebaseMessaging.getInstance().subscribeToTopic("final17");
        FirebaseMessaging.getInstance().subscribeToTopic("final33");
        FirebaseMessaging.getInstance().subscribeToTopic("final3");
        FirebaseMessaging.getInstance().subscribeToTopic("final5");*/

        padron = sharedPref.getString("padron", null);
        nombre = sharedPref.getString("nombre", null);
        mail = sharedPref.getString("mail", null);
        codigoCarreras = sharedPref.getString("codigoCarreras", null);
        nombreCarreras = sharedPref.getString("nombreCarreras", null);
        multiCarrera = sharedPref.getBoolean("multiCarrera", false);
        Log.d("PRUEBAA", "codigos: " + codigoCarreras);
        Log.d("PRUEBAA", "nombres: " + nombreCarreras);
        Log.d("PRUEBAA", "multi  : " + multiCarrera);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_alumno);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_alumno);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_alumno);
        navigationView.setNavigationItemSelectedListener(this);

        tvEncuestas = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_encuestas));

        configurarHTTPRequestSingleton();

        configurarAccesoAPerfil();

        configurarClickTarjetas();
    }

    private void suscribeTopicsGuardados() {

        String strTopics = sharedPref.getString("strTopics", "");
        //Log.d("PRUEBAA", "Topics: " + strTopics);
        List<String> listTopics = new ArrayList<String>(Arrays.asList(strTopics.split(";")));
        for(int i = 0; i < listTopics.size(); i++){
            //Log.d("PRUEBAA", "Topic: " + listTopics.get(i));
            if(!listTopics.get(i).equals("")) {
                FirebaseMessaging.getInstance().subscribeToTopic(listTopics.get(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_notificaciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.btn_notificaciones) {
                goNotificaciones();
            }
        return super.onOptionsItemSelected(item);
    }

    private void goNotificaciones() {
        Intent intent = new Intent(MainActivityAlumno.this, NotificacionesActivity.class);
        startActivity(intent);
    }

    private void eliminarTopics() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String url = "https://iid.googleapis.com/iid/info/"+ token + "?details=true";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        procesarTopics(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "key=AAAAARB5KBc:APA91bECc4HqoPBgdcb-GZdeUNImLZ1hKGDlI2opx1htxc9RU7TjTlxpRLJv5fC1bLMCPpE6VmwZ5Vb1v8kB33hiCI_ut28QeBi48UUxEV61DFlviHvwBTsO2x2wC9sey27SKO2ePxB1");
                return params;
            }
        };
        queue.add(getRequest);
    }

    private void procesarTopics(String response) {
        String strTopics = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject rel = jsonObject.getJSONObject("rel");
            //Log.d("PRUEBAA", "rel: " + rel);
            JSONObject topics = rel.getJSONObject("topics");
            //Log.d("PRUEBAA", "topics: " + topics);
            JSONArray topicNames = topics.names();
            //Log.d("PRUEBAA", "topic keys: " + topicNames);
            for (int i=0; i< topicNames.length() ;i++){
                //Log.d("PRUEBAA", i+1 + "): " + topicNames.get(i));
                strTopics = strTopics + topicNames.get(i) + ";";
                FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(topicNames.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("PRUEBAA", "Topics: " + strTopics);
        editorShared.putString("strTopics", strTopics);
        editorShared.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        calcularPrioridad();
        mail = sharedPref.getString("mail", null);
        actualizarDatosMenuLateral(nombre, mail);
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    private void configurarClickTarjetas() {
        CardView tHistorial = (CardView) findViewById(R.id.tarjeta_historial);
        CardView tOferta = (CardView) findViewById(R.id.tarjeta_ofertaAcademica);
        CardView tInscripciones = (CardView) findViewById(R.id.tarjeta_inscripciones);
        CardView tFinales = (CardView) findViewById(R.id.tarjeta_finales);

        tHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHistorial();
            }
        });

        tOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOfertaAcademica();
            }
        });

        tInscripciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goInscripciones();
            }
        });

        tFinales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFinales();
            }
        });
    }

    private void configurarAccesoAPerfil() {
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPerfil();
            }
        });
    }

    private void goPerfil() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void goOfertaAcademica() {
        if(multiCarrera){
            final String[] listCodigos = codigoCarreras.split(";");
            final String[] listNombres = nombreCarreras.split(";");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Seleccione la carrera");
            builder.setItems(listNombres, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    idCarreraSeleccionada = listCodigos[which];
                    nombreCarreraSeleccionada = listNombres[which];
                    Intent intent = new Intent(MainActivityAlumno.this, OfertaAcademicaActivity.class);
                    Bundle b = new Bundle();
                    b.putString("codigoCarrera", idCarreraSeleccionada);
                    b.putString("nombreCarrera", nombreCarreraSeleccionada);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            builder.show();
        } else {
            Intent intent = new Intent(MainActivityAlumno.this, OfertaAcademicaActivity.class);
            Bundle b = new Bundle();
            b.putString("codigoCarrera", codigoCarreras);
            b.putString("nombreCarrera", nombreCarreras);
            intent.putExtras(b);
            startActivity(intent);
        }
    }


    private void goHistorial() {
        if(multiCarrera){
            final String[] listCodigos = codigoCarreras.split(";");
            final String[] listNombres = nombreCarreras.split(";");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Seleccione la carrera");
            builder.setItems(listNombres, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    idCarreraSeleccionada = listCodigos[which];
                    nombreCarreraSeleccionada = listNombres[which];
                    Intent intent = new Intent(MainActivityAlumno.this, HistorialActivity.class);
                    Bundle b = new Bundle();
                    b.putString("codigoCarrera", idCarreraSeleccionada);
                    b.putString("nombreCarrera", nombreCarreraSeleccionada);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            builder.show();
        } else {
            Intent intent = new Intent(MainActivityAlumno.this, HistorialActivity.class);
            Bundle b = new Bundle();
            b.putString("codigoCarrera", codigoCarreras);
            b.putString("nombreCarrera", nombreCarreras);
            intent.putExtras(b);
            startActivity(intent);
        }
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

    private void formularRequest() {

        String url = APIUrl + "alumno/prioridad/" + padron;
        Log.d("API", "URL: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarPrioridad(response);
                        Log.i("DEBUG", "Prioridad Actualizada");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void actualizarPrioridad(JSONArray response) {
        JSONObject jsonobject = null;
        try {
            jsonobject = response.getJSONObject(0);
        } catch (JSONException e) {
            Log.i("JSON","Error al parsear JSON");
        }
        if(jsonobject.length() == 0){
            modificarPrioridad("-");
        } else {
            try {
                if (jsonobject != null) {
                    prioridad = jsonobject.getString("prioridad");
                    cantEncuestas = jsonobject.getString("encuestas");
                    String fechaActualizacion = jsonobject.getString("fecha_actualizacion");
                    diaActualizacion = obtenerDiaFecha(fechaActualizacion);
                    String descripcionPeriodo = jsonobject.getString("descripcion_periodo");

                    //Inscripcion

                    fechaInicioInscripcion = jsonobject.getString("fechaInicioInscripcionCursadas");
                    diaInscripcion = obtenerDiaFecha(fechaInicioInscripcion);
                    horaInscripcion = obtenerHoraFecha(fechaInicioInscripcion);
                    editorShared.putString("diaPrioridad", diaInscripcion);
                    editorShared.putString("horaPrioridad", horaInscripcion);

                    fechaCierreInscripcion = jsonobject.getString("fechaFinInscripcionCursadas");
                    diaFinInscripcion = obtenerDiaFecha(fechaCierreInscripcion);
                    horaFinInscripcion = obtenerHoraFecha(fechaCierreInscripcion);
                    editorShared.putString("diaFinPrioridad", diaFinInscripcion);
                    editorShared.putString("horaFinPrioridad", horaFinInscripcion);

                    estaEnInscripcion = validarPeriodo(fechaInicioInscripcion, fechaCierreInscripcion);
                    Log.d("FECHAS", "Inscripcion: " + estaEnInscripcion);
                    //editorShared.putBoolean("estaEnInscripcion", true);
                    editorShared.putBoolean("estaEnInscripcion", estaEnInscripcion);

                    //-------------

                    //DESINSCRIPCION

                    fechaInicioDesinscripcion = jsonobject.getString("fechaInicioDesinscripcionCursadas");
                    diaDesinscripcion = obtenerDiaFecha(fechaInicioDesinscripcion);
                    horaDesinscripcion = obtenerHoraFecha(fechaInicioDesinscripcion);
                    editorShared.putString("diaDesinscripcion", diaDesinscripcion);
                    editorShared.putString("horaDesinscripcion", horaDesinscripcion);

                    fechaCierreDesinscripcion = jsonobject.getString("fechaFinDesinscripcionCursadas");
                    diaFinDesinscripcion = obtenerDiaFecha(fechaCierreDesinscripcion);
                    horaFinDesinscripcion = obtenerHoraFecha(fechaCierreDesinscripcion);
                    editorShared.putString("diaFinDesinscripcion", diaFinDesinscripcion);
                    editorShared.putString("horaFinDesinscripcion", horaFinDesinscripcion);

                    estaEnDesinscripcion = validarPeriodo(fechaInicioDesinscripcion, fechaCierreDesinscripcion);
                    Log.d("FECHAS", "Desinscripcion: " + estaEnDesinscripcion);
                    //editorShared.putBoolean("estaEnDesinscripcion", true);
                    editorShared.putBoolean("estaEnDesinscripcion", estaEnDesinscripcion);

                    //---------------

                    //CURSADA

                    fechaInicioCursada = jsonobject.getString("fechaInicioCursadas");
                    diaCursada = obtenerDiaFecha(fechaInicioCursada);
                    horaCursada = obtenerHoraFecha(fechaInicioCursada);
                    editorShared.putString("diaCursada", diaCursada);
                    editorShared.putString("horaCursada", horaCursada);

                    fechaCierreCursada = jsonobject.getString("fechaFinDesinscripcionCursadas");
                    diaFinCursada = obtenerDiaFecha(fechaCierreCursada);
                    horaFinCursada = obtenerHoraFecha(fechaCierreCursada);
                    editorShared.putString("diaFinCursada", diaFinCursada);
                    editorShared.putString("horaFinCursada", horaFinCursada);

                    estaEnCursada = validarPeriodo(fechaInicioCursada, fechaCierreCursada);
                    Log.d("FECHAS", "Cursada: " + estaEnCursada);
                    //editorShared.putBoolean("estaEnCursada", true);
                    editorShared.putBoolean("estaEnCursada", estaEnCursada);

                    //---------------

                    //FINALES

                    fechaInicioFinales = jsonobject.getString("fechaInicioFinales");
                    diaFinales = obtenerDiaFecha(fechaInicioFinales);
                    horaFinales = obtenerHoraFecha(fechaInicioFinales);
                    editorShared.putString("diaFinales", diaFinales);
                    editorShared.putString("horaFinales", horaFinales);

                    fechaCierreFinales = jsonobject.getString("fechaFinFinales");
                    diaFinFinales = obtenerDiaFecha(fechaCierreFinales);
                    horaFinFinales = obtenerHoraFecha(fechaCierreFinales);
                    editorShared.putString("diaFinFinales", diaFinFinales);
                    editorShared.putString("horaFinFinales", horaFinFinales);

                    estaEnFinales = validarPeriodo(fechaInicioFinales, fechaCierreFinales);
                    Log.d("FECHAS", "Finales: " + estaEnFinales);
                    //editorShared.putBoolean("estaEnFinales", true);
                    editorShared.putBoolean("estaEnFinales", estaEnFinales);

                    //---------------

                    editorShared.putString("descPeriodo", descripcionPeriodo);
                    editorShared.apply();
                    modificarPrioridad(prioridad);
                    modificarEncuestas(cantEncuestas);
                } else {
                    modificarPrioridad(" - ");
                }
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
    }

    private void modificarEncuestas(String cantEncuestas) {
        //Gravity property aligns the text
        tvEncuestas.setGravity(Gravity.CENTER_VERTICAL);
        tvEncuestas.setTypeface(null, Typeface.BOLD);
        tvEncuestas.setTextColor(getResources().getColor(R.color.colorAccent));
        tvEncuestas.setText(cantEncuestas);
    }

    private boolean validarPeriodo(String fechaInicio, String fechaCierre) {
        boolean esValido = false;


        String dia1 = fechaInicio.substring(8,10);
        String mes1 = fechaInicio.substring(5,7);
        String año1 = fechaInicio.substring(0,4);
        String hora1 = fechaInicio.substring(11,13);
        String minutos1 = fechaInicio.substring(14,16);

        String dia2 = fechaCierre.substring(8,10);
        String mes2 = fechaCierre.substring(5,7);
        String año2 = fechaCierre.substring(0,4);
        String hora2 = fechaCierre.substring(11,13);
        String minutos2 = fechaCierre.substring(14,16);

        Calendar currentTime = Calendar.getInstance();
        int añoActual = currentTime.get(Calendar.YEAR);
        int mesActual = (currentTime.get(Calendar.MONTH)+1);
        int diaActual = currentTime.get(Calendar.DAY_OF_MONTH);
        int horaActual = currentTime.get(Calendar.HOUR_OF_DAY);
        int minutoActual = currentTime.get(Calendar.MINUTE);
        String minAux;

        if(minutoActual < 10){
            minAux = "0" + minutoActual;
        } else {
            minAux = String.valueOf(minutoActual);
        }

        String horaAux;
        if(horaActual < 10){
            horaAux = "0" + horaActual;
        } else {
            horaAux = String.valueOf(horaActual);
        }

        String diaAux;
        if(diaActual < 10){
            diaAux = "0" + diaActual;
        } else {
            diaAux = String.valueOf(diaActual);
        }

        String mesAux;
        if(mesActual < 10){
            mesAux = "0" + mesActual;
        } else {
            mesAux = String.valueOf(mesActual);
        }


        String strFechaInicio = dia1+"/"+mes1+"/"+año1+" "+hora1+":"+minutos1;
        String strFechaCierre = dia2+"/"+mes2+"/"+año2+" "+hora2+":"+minutos2;
        Log.i("PERIODO", "Fecha inicio: " + strFechaInicio);
        Log.i("PERIODO", "Fecha cierre: " + strFechaCierre);

        String strDateActual = diaAux + "/" + mesAux + "/" + añoActual + " " + horaAux + ":" + minAux;
        Log.i("PERIODO", "Fecha actual: " + strDateActual);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date dateInicio = format.parse(strFechaInicio);
            Date dateCierre = format.parse(strFechaCierre);
            Date dateActual = format.parse(strDateActual);

            Calendar c = Calendar.getInstance();
            c.setTime(dateActual); // Now use today date.

            if(c.getTime().after(dateInicio) && c.getTime().before(dateCierre)){
                esValido = true;
            } else {
                esValido = false;
            }
        } catch (ParseException e) {
            esValido = false;
            e.printStackTrace();
        }

        return esValido;

    }

    private String obtenerDiaFecha(String fecha){
        String dia = fecha.substring(8,10);
        String mes = fecha.substring(5,7);
        String año = fecha.substring(0,4);
        String strDia = (dia + "/" + mes + "/" + año);
        return strDia;
    }

    private String obtenerHoraFecha(String fecha){
        String hora = fecha.substring(11,16);
        return hora;
    }

    private String getFechaActualizacion(String fechaActualizacion) {
        String dia = fechaActualizacion.substring(8,10);
        String mes = fechaActualizacion.substring(5,7);
        String año = fechaActualizacion.substring(0,4);
        return (dia + "/" + mes + "/" + año);
    }

    private void modificarPrioridad(String prioridad) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_alumno);
        View headerView = navigationView.getHeaderView(0);
        TextView tvPrioridad = (TextView) headerView.findViewById(R.id.tvPrioridad);
        tvPrioridad.setText("  Prioridad: " + prioridad + "  ");
        if(prioridad.equals("-")){
            editorShared.putBoolean("estaEnInscripcion", false);
            editorShared.putString("descPeriodo", "");
            editorShared.apply();
        } else {
            //validezPeriodoInscripcion(fechaCierreInscripcion);
            tvPrioridad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialog();
                }
            });
        }
    }

    private void actualizarDatosMenuLateral(String nombre, String mail) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_alumno);
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombre = (TextView) headerView.findViewById(R.id.tvMenuNombre);
        TextView tvMail = (TextView) headerView.findViewById(R.id.tvMenuMail);
        tvNombre.setText(nombre);
        tvMail.setText(mail);
    }

    private void mostrarDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivityAlumno.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivityAlumno.this);
        }
        builder.setTitle("Prioridad")
                .setMessage("Fecha de inscripción \n" +
                            "Día : " + diaInscripcion + "\n" +
                            "Hora: " + horaInscripcion +" horas \n \n" +
                            "(Última actualización: " + diaActualizacion + ")")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ofertaAcademica) {
            goOfertaAcademica();
        } else if (id == R.id.nav_inscripciones) {
            goInscripciones();
        } else if (id == R.id.nav_cerrarSesionAlumno) {
            editorShared.remove("logueadoAlumno");
            editorShared.apply();
            eliminarTopics();
            goLogin();
        } else if (id == R.id.nav_finales){
            goFinales();
        } else if(id == R.id.nav_historialAcademimco){
            goHistorial();
        } else if(id == R.id.nav_alumnoRegular){
            validarRegularidad();
        } else if(id == R.id.nav_encuestas){
            if(hayEncuestaPendiente()){
                obtenerEncuestasPendientes();
            } else {
                Toast.makeText(MainActivityAlumno.this, "Sin encuestas pendientes",
                        Toast.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void obtenerEncuestasPendientes() {

        progress = ProgressDialog.show(this, "Encuestas",
                "Obteniendo encuestas pendientes...", true);
        String url = APIUrl + "alumno/encuestas?padron=" + padron;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        procesarEncuestas(response);
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(MainActivityAlumno.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                        esRegular = false;
                        progress.dismiss();
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    private void procesarEncuestas(JSONArray response) {
        strListEncuestas = "";
        strCodigosEncuestas = "";
        strNombreEncuestas = "";
        strIDEncuestas = "";
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject;
            try {
                jsonobject = response.getJSONObject(i);
                if(jsonobject.length() == 0){
                    Toast.makeText(MainActivityAlumno.this, "No hay encuestas pendientes",
                            Toast.LENGTH_LONG).show();
                } else {
                    try {
                        String nombreMateria = jsonobject.getString("nombre");
                        String codigoMateria = jsonobject.getString("codigo");
                        String idMateria = jsonobject.getString("id");
                        strListEncuestas = strListEncuestas + "[" + codigoMateria + "] " + nombreMateria + ";";
                        strNombreEncuestas = strNombreEncuestas + nombreMateria + ";";
                        strCodigosEncuestas = strCodigosEncuestas + codigoMateria + ";";
                        strIDEncuestas = strIDEncuestas + idMateria + ";";
                    } catch (JSONException e) {
                        Log.i("JSON","Error al obtener datos del JSON");
                    }
                }
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
        }
        cargarOpcionesEncuesta();
    }

    private void cargarOpcionesEncuesta() {

        listIDEncuestas = strIDEncuestas.split(";");
        listCodigosEncuestas = strCodigosEncuestas.split(";");
        listNombresEncuestas = strNombreEncuestas.split(";");
        listEncuestasFinal = strListEncuestas.split(";");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione la encuesta a completar");
        builder.setItems(listEncuestasFinal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                idMateriaSeleccionada = listIDEncuestas[which];
                nombreMateriaSeleccionada = listNombresEncuestas[which];
                codigoMateriaSeleccionada = listCodigosEncuestas[which];
                Intent intent = new Intent(MainActivityAlumno.this, EncuestasActivity.class);
                Bundle b = new Bundle();
                b.putString("padron", padron);
                b.putString("codigoMateria", codigoMateriaSeleccionada);
                b.putString("nombreMateria", nombreMateriaSeleccionada);
                b.putString("idMateria", idMateriaSeleccionada);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                for (int i = 0; i < navigationView.getMenu().size(); i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            }
        });
        builder.show();

    }

    private boolean hayEncuestaPendiente() {
        int encuestasPendientes = Integer.valueOf(String.valueOf(tvEncuestas.getText()));
        if(encuestasPendientes > 0){
            return true;
        } else {
            return false;
        }
    }

    private void descargarPDF() {

        TemplatePDF templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Certificado", "Alumno regular", "FIUBA");
        String fechaActual = getFechaAtual();
        templatePDF.addImage(addImagePDF());
        templatePDF.addTitles("Facultad de Ingeniería de la Universidad de Buenos Aires", "Certificado de alumno regular", fechaActual);
        templatePDF.addParagraph("Apellido/s: " + nombre.split("\\s+")[1]);
        templatePDF.addParagraph("Nombre/s: " + nombre.split("\\s+")[0]);
        templatePDF.addParagraph("DNI N°: " + sharedPref.getString("usuario", ""));
        //Obtengo carreras
        String[] listCodigos = codigoCarreras.split(";");
        String[] listNombres = nombreCarreras.split(";");
        String carreras = "";
        for(int i = 0; i < listCodigos.length; i++){
            carreras = carreras + "[" + listCodigos[i] + "] " + listNombres[i];
            if(i!=listCodigos.length-1){
                carreras = carreras + ", ";
            }
        }
        templatePDF.addParagraph("Carrera/s: " + carreras);
        templatePDF.addParagraph("Conste que el alumno/a cuyos datos figuran en el presente documento, se encuentra inscripto en la/s carrera/s arriba citada/s y a la fecha mantiene su condicion de Alumno Regular. A pedido del interesado se extiende el presente documento");
        templatePDF.closeDocument();
        templatePDF.viewPDF(this);
    }

    private Image addImagePDF() {

        Drawable d = getResources().getDrawable(R.drawable.logo_fiuba);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = null;
        try {
            image = Image.getInstance(stream.toByteArray());
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void validarRegularidad() {

        progress = ProgressDialog.show(this, "Certificado alumno regular",
                "Validando regularidad...", true);
        //Pegarle a la API para ver si es alumno regular
        String url = APIUrl + "alumno/regular?padron=" + padron;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        esRegular(response);
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(MainActivityAlumno.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                        esRegular = false;
                        progress.dismiss();
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void esRegular(JSONObject response) {
        try {
            esRegular = response.getBoolean("es_regular");
            if(esRegular){
                Toast.makeText(MainActivityAlumno.this, "Generando certificado...",
                        Toast.LENGTH_LONG).show();
                descargarPDF();
            } else {
                Toast.makeText(MainActivityAlumno.this, "No cumple los requisitos para ser alumno regular",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goInscripciones() {
        Intent intent = new Intent(this, InscripcionesActivity.class);
        startActivity(intent);
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goFinales() {
        Intent intent = new Intent(this, FinalesActivity.class);
        startActivity(intent);
    }

    public void calcularPrioridad() {
        formularRequest();
    }

    public String getFechaAtual() {

        Calendar currentTime = Calendar.getInstance();
        int añoActual = currentTime.get(Calendar.YEAR);
        int mesActual = (currentTime.get(Calendar.MONTH)+1);
        int diaActual = currentTime.get(Calendar.DAY_OF_MONTH);

        String fechaAtual = diaActual + "/" + mesActual + "/" + añoActual;

        return fechaAtual;
    }
}

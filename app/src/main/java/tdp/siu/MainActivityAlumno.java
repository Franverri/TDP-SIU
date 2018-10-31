package tdp.siu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivityAlumno extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    NavigationView navigationView;

    String padron, nombre;
    String prioridad;
    String fechaInicioInscripcion, fechaCierreInscripcion, fechaInicioDesinscripcion, fechaCierreDesinscripcion, fechaInicioCursada, fechaCierreCursada ,fechaInicioFinales, fechaCierreFinales;
    String diaInscripcion, diaFinInscripcion, horaInscripcion, horaFinInscripcion;
    String diaDesinscripcion, diaFinDesinscripcion, horaDesinscripcion, horaFinDesinscripcion;
    String diaCursada, diaFinCursada, horaCursada, horaFinCursada;
    String diaFinales, diaFinFinales, horaFinales, horaFinFinales;
    String diaActualizacion;
    Boolean estaEnInscripcion, estaEnDesinscripcion, estaEnCursada ,estaEnFinales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        padron = sharedPref.getString("padron", null);
        nombre = sharedPref.getString("nombre", null);
        String mail = sharedPref.getString("mail", null);

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

        configurarHTTPRequestSingleton();

        configurarAccesoAPerfil();

        configurarClickTarjetas();

        actualizarDatosMenuLateral(nombre, mail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calcularPrioridad();
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

    private void goHistorial() {
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
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
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarPrioridad(response);
                        boolean periodoHabilitado = true;
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
                    //editorShared.putBoolean("estaEnDesinscripcion", true);
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
                    //editorShared.putBoolean("estaEnFinales", false);
                    editorShared.putBoolean("estaEnFinales", estaEnFinales);

                    //---------------

                    editorShared.putString("descPeriodo", descripcionPeriodo);
                    editorShared.apply();
                    modificarPrioridad(prioridad);
                } else {
                    modificarPrioridad(" - ");
                }
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
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
            super.onBackPressed();        }
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
            goLogin();
        } else if (id == R.id.nav_finales){
            goFinales();
        } else if(id == R.id.nav_historialAcademimco){
            goHistorial();
        } else if(id == R.id.nav_alumnoRegular){
            validarAlumnoRegular();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void validarAlumnoRegular() {

        boolean esRegular = validarRegularidad();
        if(esRegular){
            descargarPDF();
        } else {
            Toast.makeText(MainActivityAlumno.this, "No cumple los requisitos para ser alumno regular",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void descargarPDF() {
        TemplatePDF templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Certificado", "Alumno regular", "FIUBA");
        String fechaActual = getFechaAtual();
        templatePDF.addTitles("Facultad de Ingeniería de la Universidad de Buenos Aires", "Certificado de alumno regular", fechaActual);
        templatePDF.addParagraph("Apellido/s: " + nombre.split("\\s+")[1]);
        templatePDF.addParagraph("Nombre/s: " + nombre.split("\\s+")[0]);
        templatePDF.addParagraph("DNI N°: " + sharedPref.getString("usuario", "");
        templatePDF.addParagraph("Carrera: " /*FALTA CARRERA*/ );
        templatePDF.addParagraph("Conste que el alumno cuyos datos figuran en el presente documento, se encuentra inscripto en la/s carrera/s arriba citada/s y a la fecha mantiene su condicion de Alumno Regular. A pedido del interesado se extiende el presente documento");
        templatePDF.closeDocument();
        templatePDF.viewPDF(this);
    }

    private boolean validarRegularidad() {
        //Pegarle a la API para ver si es alumno regular
        return true;
    }

    private void goInscripciones() {
        Intent intent = new Intent(this, InscripcionesActivity.class);
        startActivity(intent);
    }

    private void goOfertaAcademica() {
        Intent intent = new Intent(this, OfertaAcademicaActivity.class);
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

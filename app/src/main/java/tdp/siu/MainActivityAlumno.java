package tdp.siu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class MainActivityAlumno extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    NavigationView navigationView;

    String padron;
    String prioridad, diaActualizacion, diaInscripcion, horaInscripcion, fechaCierrePeriodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        padron = sharedPref.getString("padron", null);
        String nombre = sharedPref.getString("nombre", null);
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
        CardView tPerfil = (CardView) findViewById(R.id.tarjeta_perfil);
        CardView tOferta = (CardView) findViewById(R.id.tarjeta_ofertaAcademica);
        CardView tInscripciones = (CardView) findViewById(R.id.tarjeta_inscripciones);

        tPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goPerfil();
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
                        //String dia = "28/09/2018";
                        //String hora = "17:00";
                        //editorShared.putBoolean("periodoHabilitado", periodoHabilitado);
                        //editorShared.putString("diaPrioridad", dia);
                        //editorShared.putString("horaPrioridad", hora);
                        //editorShared.apply();
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
                    diaActualizacion = getFechaActualizacion(fechaActualizacion);
                    String fechaInscripcion = jsonobject.getString("fecha_inicio");
                    obtenerDiaHoraInscripcion(fechaInscripcion);
                    fechaCierrePeriodo = jsonobject.getString("fecha_cierre");
                    modificarPrioridad(prioridad);
                    validezPeriodoInscripcion(fechaCierrePeriodo);
                    String descripcionPeriodo = jsonobject.getString("descripcion_periodo");
                    editorShared.putString("descPeriodo", descripcionPeriodo);
                    editorShared.apply();
                } else {
                    modificarPrioridad(" - ");
                }
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
    }

    private void validezPeriodoInscripcion(String fechaCierrePeriodo) {
        boolean periodoValido = false;
        int dia = Integer.parseInt(fechaCierrePeriodo.substring(8,10));
        int mes = Integer.parseInt(fechaCierrePeriodo.substring(5,7));
        int año = Integer.parseInt(fechaCierrePeriodo.substring(0,4));
        int hora = Integer.parseInt(fechaCierrePeriodo.substring(11,13));
        int minutos = Integer.parseInt(fechaCierrePeriodo.substring(14,16));
        Calendar currentTime = Calendar.getInstance();
        if(año > currentTime.get(Calendar.YEAR)){
            periodoValido = true;
        } else if (año < currentTime.get(Calendar.YEAR)){
            periodoValido = false;
        } else { //Años iguales
            if(mes > (currentTime.get(Calendar.MONTH)+1)){
                periodoValido = true;
            } else if(mes < (currentTime.get(Calendar.MONTH)+1)){
                periodoValido = false;
            } else { //Mes igual
                if(dia > currentTime.get(Calendar.DAY_OF_MONTH)){
                    periodoValido = true;
                } else if(dia < currentTime.get(Calendar.DAY_OF_MONTH)){
                    periodoValido = false;
                } else { //Dia igual
                    if(hora > currentTime.get(Calendar.HOUR_OF_DAY)){
                        periodoValido = true;
                    } else if(hora < currentTime.get(Calendar.HOUR_OF_DAY)){
                        periodoValido = false;
                    } else { //Hora igual
                        if(minutos > currentTime.get(Calendar.MINUTE)){
                            periodoValido = true;
                        } else if(minutos <= currentTime.get(Calendar.MINUTE)){
                            periodoValido = false;
                        }
                    }
                }
            }
        }
        //HARDCODEADO PARA PRUEBA
        //periodoValido = true;
        editorShared.putBoolean("periodoHabilitado", periodoValido);
        editorShared.apply();
    }

    private void obtenerDiaHoraInscripcion(String fechaInscripcion) {
        String dia = fechaInscripcion.substring(8,10);
        String mes = fechaInscripcion.substring(5,7);
        String año = fechaInscripcion.substring(0,4);
        String hora = fechaInscripcion.substring(11,16);
        diaInscripcion = (dia + "/" + mes + "/" + año);
        horaInscripcion = hora;
        editorShared.putString("diaPrioridad", diaInscripcion);
        editorShared.putString("horaPrioridad", horaInscripcion);
        editorShared.apply();
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
        tvPrioridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialog();
            }
        });
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        drawer.closeDrawer(GravityCompat.START);
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

    public void calcularPrioridad() {
        formularRequest();
    }
}

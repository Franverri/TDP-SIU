package tdp.siu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityDocente extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/docente/";
    String idDocente;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    NavigationView navigationView;

    List<Curso> cursosList;
    CursosAdapter adapter;
    RecyclerView recyclerView;

    Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rand = new Random();
        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        idDocente = sharedPref.getString("legajo", null);
        String nombre = sharedPref.getString("nombre", null);
        String mail = sharedPref.getString("mail", null);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_docente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_docente);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_docente);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_docente);
        navigationView.setNavigationItemSelectedListener(this);

        configurarHTTPRequestSingleton();

        configurarRecyclerView();

        configurarAccesoAPerfil();

        actualizarDatosMenuLateral(nombre, mail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String mail = sharedPref.getString("mail", null);
        actualizarDatosMenuLateral(mail);
        obtenerPeriodos();
        enviarRequestCursos();
    }

    private void actualizarDatosMenuLateral(String mail) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_docente);
        View headerView = navigationView.getHeaderView(0);
        TextView tvMail = (TextView) headerView.findViewById(R.id.tvMenuDMail);
        tvMail.setText(mail);
    }

    private void obtenerPeriodos() {
        String url = APIUrl + "periodos";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        cargarPeriodos(response);
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

    private void cargarPeriodos(JSONArray response) {
        JSONObject jsonobject = null;
        try {
            jsonobject = response.getJSONObject(0);
        } catch (JSONException e) {
            Log.i("JSON","Error al parsear JSON");
        }
        if(jsonobject.length() == 0){

        } else {
            try {
                if (jsonobject != null) {

                    //INSCRIPCION

                    String fechaInicioInscripcion = jsonobject.getString("fechaInicioInscripcionCursadas");
                    String fechaCierreInscripcion = jsonobject.getString("fechaFinInscripcionCursadas");

                    boolean estaEnInscripcion = validarPeriodo(fechaInicioInscripcion, fechaCierreInscripcion);
                    Log.d("FECHAS", "Inscripcion: " + estaEnInscripcion);
                    editorShared.putBoolean("estaEnInscripcion", true);
                    //editorShared.putBoolean("estaEnInscripcion", estaEnInscripcion);

                    //-------------

                    //DESINSCRIPCION

                    String fechaInicioDesinscripcion = jsonobject.getString("fechaInicioDesinscripcionCursadas");
                    String fechaCierreDesinscripcion = jsonobject.getString("fechaFinDesinscripcionCursadas");

                    boolean estaEnDesinscripcion = validarPeriodo(fechaInicioDesinscripcion, fechaCierreDesinscripcion);
                    Log.d("FECHAS", "Desinscripcion: " + estaEnDesinscripcion);
                    editorShared.putBoolean("estaEnDesinscripcion", true);
                    //editorShared.putBoolean("estaEnDesinscripcion", estaEnDesinscripcion);

                    //---------------


                    //CURSADA

                    String fechaInicioCursada = jsonobject.getString("fechaInicioCursadas");
                    String fechaCierreCursada = jsonobject.getString("fechaFinCursadas");
                    Boolean estaEnCursadas = validarPeriodo(fechaInicioCursada, fechaCierreCursada);
                    editorShared.putBoolean("estaEnCursadas", true);
                    //editorShared.putBoolean("estaEnCursadas", estaEnCursadas);

                    Log.i("PERIODOS", "Inicio Cursada: " + fechaInicioCursada);
                    Log.i("PERIODOS", "Fin Cursada   : " + fechaCierreCursada);
                    Log.i("PERIODOS", "Cursada       : " + estaEnCursadas);

                    // FINALES

                    String fechaInicioFinales = jsonobject.getString("fechaInicioFinales");
                    String diaFinales = obtenerDiaFecha(fechaInicioFinales);
                    String horaFinales = obtenerHoraFecha(fechaInicioFinales);
                    editorShared.putString("diaInicioFinales", diaFinales);
                    editorShared.putString("horaInicioFinales", horaFinales);

                    String fechaCierreFinales = jsonobject.getString("fechaFinFinales");
                    String diaFinFinales = obtenerDiaFecha(fechaCierreFinales);
                    String horaFinFinales = obtenerHoraFecha(fechaCierreFinales);
                    editorShared.putString("diaFinFinales", diaFinFinales);
                    editorShared.putString("horaFinFinales", horaFinFinales);
                    editorShared.apply();
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
        if(diaActual < 10){
            mesAux = "0" + mesActual;
        } else {
            mesAux = String.valueOf(mesActual);
        }


        String strFechaInicio = dia1+"/"+mes1+"/"+año1+" "+hora1+":"+minutos1;
        String strFechaCierre = dia2+"/"+mes2+"/"+año2+" "+hora2+":"+minutos2;
        //Log.i("PERIODO", "Fecha inicio: " + strFechaInicio);
        //Log.i("PERIODO", "Fecha cierre: " + strFechaCierre);

        String strDateActual = diaAux + "/" + mesAux + "/" + añoActual + " " + horaAux + ":" + minAux;
        //Log.i("PERIODO", "Fecha actual: " + strDateActual);
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

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_docente);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        cursosList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new CursosAdapter(this, cursosList);

        //Aca se manda el request al server
        enviarRequestCursos();

    }


    private void enviarRequestCursos() {

        String url = APIUrl + "cursos/" + idDocente;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarCursos(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(MainActivityDocente.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void actualizarCursos(JSONObject response){
        cursosList.clear();
        JSONArray array = null;
        try {
            array = response.getJSONArray("cursos");
        }catch (JSONException e){
            Log.i("JSON","Error al parsear JSON");
        }
        int cantCursos = array.length();
        for (int i = 0; i < cantCursos; i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = array.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                String nombreCurso = jsonobject.getString("nombre");
                String codigoCurso = jsonobject.getString("codigo");
                int idCurso = jsonobject.getInt("id_curso");
                //TODO
                //int numeroCurso = jsonobject.getInt("numero");
                int numeroCurso = rand.nextInt(4) + 1;
                int inscriptos = jsonobject.getInt("alumnos_totales");
                int vacantesRestantes = jsonobject.getInt("vacantes");
                if (vacantesRestantes < 0){
                    vacantesRestantes = 0;
                }
                cursosList.add(new Curso(nombreCurso, codigoCurso, idCurso, numeroCurso, inscriptos, vacantesRestantes));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);
        if (cantCursos == 0){
            Toast.makeText(MainActivityDocente.this, "No tiene ningún curso registrado",
                    Toast.LENGTH_LONG).show();
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

    private void actualizarDatosMenuLateral(String nombre, String mail) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_docente);
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombre = (TextView) headerView.findViewById(R.id.tvMenuDNombre);
        TextView tvMail = (TextView) headerView.findViewById(R.id.tvMenuDMail);
        tvNombre.setText(nombre);
        tvMail.setText(mail);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_docente);
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

        if (id == R.id.nav_cursos) {
            enviarRequestCursos();
        } else if (id == R.id.nav_cerrarSesionDocente) {
            editorShared.remove("logueadoDocente");
            editorShared.apply();
            goLogin();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_docente);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

package tdp.siu;

import android.app.ProgressDialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityDocente extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/";
    int idDocente = 1234;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    List<Curso> cursosList;
    CursosAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_docente);
        navigationView.setNavigationItemSelectedListener(this);

        configurarHTTPRequestSingleton();

        configurarRecyclerView();
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_docente);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        cursosList = new ArrayList<>();


        //adding some items to our list
        cursosList.add(
                new Curso(
                        "TDP 2",
                        1,
                        35,
                        0));

        cursosList.add(
                new Curso(
                        "TDP 2",
                        1,
                        35,
                        0));

        cursosList.add(
                new Curso(
                        "TDP 2",
                        1,
                        35,
                        0));

        //creating recyclerview adapter
        adapter = new CursosAdapter(this, cursosList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }


    private void enviarRequestCursos() {

        String url = APIUrl + "cursos/" + idDocente;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        progress.dismiss();
                        actualizarCursos(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        progress.dismiss();
                        Toast.makeText(MainActivityDocente.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void actualizarCursos(JSONObject response){
        cursosList.clear();
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = response.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                String nombreCurso = jsonobject.getString("nombre");
                int numeroCurso = jsonobject.getInt("numero");
                int inscriptos = jsonobject.getInt("cantInscriptosRegular");
                int vacantesRestantes = jsonobject.getInt("vacantes");
                cursosList.add(new Curso(nombreCurso, numeroCurso, inscriptos, vacantesRestantes));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        //adapter = new CursosAdapter(this, cursosList);
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
            // Handle the camera action
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

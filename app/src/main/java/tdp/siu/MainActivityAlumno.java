package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

import java.util.List;

public class MainActivityAlumno extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    NavigationView navigationView;

    boolean estaEnPrincipal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

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

        progress = ProgressDialog.show(MainActivityAlumno.this, "Calculando prioridad",
                "Recolectando datos...", true);

        String url = APIUrl + "alumno/prioridad/95812";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        progress.dismiss();
                        Toast.makeText(MainActivityAlumno.this, "Prioridad actualizada",
                                Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        progress.dismiss();
                        Toast.makeText(MainActivityAlumno.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(estaEnPrincipal){
                super.onBackPressed();
                finish();
            } else {
                estaEnPrincipal = true;
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

            /*
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount == 1) {
                setTitle("SIU");
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }*/
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
            goLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alumno);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goInscripciones() {
        //setTitle("Inscripciones");
        estaEnPrincipal = false;
        InscripcionesFragment inscripcionesFragment = new InscripcionesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_alumno, inscripcionesFragment).addToBackStack(null).commit();
    }

    private void goOfertaAcademica() {
        //setTitle("Oferta académica");
        estaEnPrincipal = false;
        OfertaAcademicaFragment ofertaAcademicaFragment = new OfertaAcademicaFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_alumno, ofertaAcademicaFragment).addToBackStack(null).commit();
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void calcularPrioridad(View view) {
        formularRequest();
    }
}

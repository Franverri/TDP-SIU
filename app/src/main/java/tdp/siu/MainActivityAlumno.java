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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivityAlumno extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    NavigationView navigationView;

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

        configurarClickTarjetas();
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

        String url = APIUrl + "alumno/prioridad/10101";
        //String url = APIUrl + "alumno/prioridad/95812";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        try {
                            String prioridad = response.getString("prioridad");
                            //Obtengo mas datos
                            boolean periodoHabilitado = true;
                            editorShared.putBoolean("periodoHabilitado", periodoHabilitado);
                            editorShared.apply();
                            modificarPrioridad(prioridad);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void modificarPrioridad(String prioridad) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_alumno);
        View headerView = navigationView.getHeaderView(0);
        TextView tvPrioridad = (TextView) headerView.findViewById(R.id.tvPrioridad);
        if(prioridad.equals("undefined")){
            tvPrioridad.setText("  Prioridad: -  ");
        } else {
            tvPrioridad.setText("  Prioridad: " + prioridad + "  ");
        }
        tvPrioridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialog();
            }
        });
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
                            "Día : 01/10/2018 \n" +
                            "Hora: 15:00 horas \n \n" +
                            "(Última actualización: 24/09/2018)")
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
            /*

            if(estaEnPrincipal){
                super.onBackPressed();
                finish();
            } else {
                estaEnPrincipal = true;
                Intent intent = getIntent();
                finish();
                startActivity(intent);
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
        /*
        estaEnPrincipal = false;
        InscripcionesFragment inscripcionesFragment = new InscripcionesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_alumno, inscripcionesFragment).addToBackStack(null).commit();
        */
        Intent intent = new Intent(this, InscripcionesActivity.class);
        startActivity(intent);
    }

    private void goOfertaAcademica() {
        /*
        estaEnPrincipal = false;
        OfertaAcademicaFragment ofertaAcademicaFragment = new OfertaAcademicaFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_alumno, ofertaAcademicaFragment).addToBackStack(null).commit();
        */
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

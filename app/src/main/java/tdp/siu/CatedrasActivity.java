package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CatedrasActivity extends AppCompatActivity {

    ProgressDialog progress;
    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";
    String padron;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    private String idMateria, codigoMateria, nombreMateria;
    private String diaPrioridad, horaPrioridad;

    List<Catedra> catedrasList;
    CatedrasAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        diaPrioridad = sharedPref.getString("diaPrioridad", null);
        horaPrioridad = sharedPref.getString("horaPrioridad", null);

        boolean puedeInscribirse = puedeInscribirse();
        Log.i("PRUEBA", "PUEDE: " + puedeInscribirse);
        editorShared.putBoolean("puedeInscribirse", puedeInscribirse);
        editorShared.apply();

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_catedras);

        setTitle("Cursos disponibles");

        Bundle b = getIntent().getExtras();
        if(b != null){
            idMateria = b.getString("idMateria");
            //Log.i("PRUEBA", "ID    : " + idMateria);
            codigoMateria = b.getString("codigoMateria");
            //Log.i("PRUEBA", "Codigo: " + codigoMateria);
            nombreMateria = b.getString("nombreMateria");
            //Log.i("PRUEBA", "Nombre: " + nombreMateria);
            padron = b.getString("padron");
        }

        configurarHTTPRequestSingleton();

        configurarRecyclerView(idMateria);
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

    private void enviarRequestCursos(String idMateria) {
        progress = ProgressDialog.show(this, "Buscando materias",
                "Recolectando datos...", true);

        String url = APIUrl + "alumno/oferta/"+padron+"?id_materia="+idMateria;
        Log.i("PRUEBA", "URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarCursos(response);
                        progress.dismiss();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(CatedrasActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    private void actualizarCursos(JSONArray response) {
        catedrasList.clear();
        int cantCursos = response.length();
        for (int i = 0; i < cantCursos; i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = response.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                if (jsonobject != null) {
                    String numeroCurso = jsonobject.getString("id");
                    String docente = jsonobject.getString("docente");
                    String cupos = jsonobject.getString("vacantes");
                    String sedes = jsonobject.getString("sede");
                    String aulas = jsonobject.getString("aulas");
                    String dias = jsonobject.getString("dias");
                    String horarios = jsonobject.getString("horarios");
                    catedrasList.add(new Catedra("Curso " + numeroCurso, docente, dias, horarios, sedes, aulas, cupos));
                }
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);
        if (cantCursos == 0){
            Toast.makeText(CatedrasActivity.this, "No existe ningún curso registrado",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private void configurarRecyclerView(String idMateria) {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_catedras);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        catedrasList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new CatedrasAdapter(this, catedrasList);

        enviarRequestCursos(idMateria);

    }

    private boolean puedeInscribirse() {
        boolean puedeInscribirse = true;
        int intDiaPrioridad = Integer.parseInt(diaPrioridad.substring(0,2));
        int intMesPrioridad = Integer.parseInt(diaPrioridad.substring(3,5));
        int intAñoPrioridad = Integer.parseInt(diaPrioridad.substring(6,10));
        int intHoras = Integer.parseInt(horaPrioridad.substring(0,2));
        int intMinutos = Integer.parseInt(horaPrioridad.substring(3,5));
        Calendar currentTime = Calendar.getInstance();
        if(intAñoPrioridad > currentTime.get(Calendar.YEAR)){
            puedeInscribirse = false;
        } else if (intAñoPrioridad < currentTime.get(Calendar.YEAR)){
            puedeInscribirse = true;
        } else { //Años iguales
            if(intMesPrioridad > (currentTime.get(Calendar.MONTH)+1)){
                puedeInscribirse = false;
            } else if(intMesPrioridad < (currentTime.get(Calendar.MONTH)+1)){
                puedeInscribirse = true;
            } else { //Mes igual
                if(intDiaPrioridad > currentTime.get(Calendar.DAY_OF_MONTH)){
                    puedeInscribirse = false;
                } else if(intDiaPrioridad < currentTime.get(Calendar.DAY_OF_MONTH)){
                    puedeInscribirse = true;
                } else { //Dia igual
                    if(intHoras > currentTime.get(Calendar.HOUR_OF_DAY)){
                        puedeInscribirse = false;
                    } else if(intHoras < currentTime.get(Calendar.HOUR_OF_DAY)){
                        puedeInscribirse = true;
                    } else { //Hora igual
                        if(intMinutos > currentTime.get(Calendar.MINUTE)){
                            puedeInscribirse = false;
                        } else if(intMinutos <= currentTime.get(Calendar.MINUTE)){
                            puedeInscribirse = true;
                        }
                    }
                }
            }
        }
        return puedeInscribirse;
    }
}

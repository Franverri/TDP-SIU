package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InscripcionesActivity extends AppCompatActivity {

    List<Inscripcion> inscripcionList;
    InscripcionAdapter adapter;
    RecyclerView recyclerView;

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/alumno/inscripciones/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    String padron;
    String strHorarios, strDias, strNombres;
    boolean tieneInscripciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        padron = sharedPref.getString("padron", null);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_inscripciones);

        configurarHTTPRequestSingleton();

        configurarRecyclerView();

        configurarClickCalendario();
    }

    private void configurarClickCalendario() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_calendar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tieneInscripciones){
                    goCalendarioSemanal();
                } else {
                    Toast.makeText(InscripcionesActivity.this, "No existe ninguna inscripción",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goCalendarioSemanal() {
        Intent intent = new Intent(this, CalendarActivity.class);
        Bundle b = new Bundle();
        b.putString("strHorarios", strHorarios);
        b.putString("strDias", strDias);
        b.putString("strNombres", strNombres);
        intent.putExtras(b);
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

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_inscripciones);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        inscripcionList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new InscripcionAdapter(this, inscripcionList);

        buscarMateriasInscripto();
        recyclerView.setAdapter(adapter);
    }

    private void buscarMateriasInscripto() {
        if(padron != null){
            progress = ProgressDialog.show(this, "Inscripciones",
                    "Recolectando datos...", true);
            String url = APIUrl + padron;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progress.dismiss();
                            Log.i("API","Response: " + response.toString());
                            procesarRespuesta(response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.dismiss();
                            Log.i("Error.Response", String.valueOf(error));
                            Toast.makeText(InscripcionesActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
    }

    private void procesarRespuesta(JSONObject response) {
        inscripcionList.clear();
        strDias = "";
        strHorarios = "";
        strNombres = "";
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
                String idCurso = jsonobject.getString("id_curso");
                String nombreCurso = jsonobject.getString("nombre");
                String codigoCurso = jsonobject.getString("codigo");
                String docente = jsonobject.getString("docente");
                String horarioFinal = "";
                String sede, aulas, dias, horarios;
                sede = jsonobject.getString("sede");
                aulas = jsonobject.getString("aulas");
                dias = jsonobject.getString("dias");
                horarios = jsonobject.getString("horarios");
                horarioFinal = calcularHorarioFinal(sede,aulas,dias,horarios);
                strHorarios = strHorarios + horarios + ";";
                strDias = strDias + dias + ";";
                if(dias.contains(";")){
                    strNombres = strNombres + nombreCurso + ";" + nombreCurso + ";";
                } else {
                    strNombres = strNombres + nombreCurso + ";";
                }
                inscripcionList.add(new Inscripcion(idCurso, nombreCurso,codigoCurso,docente,horarioFinal));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);
        if (cantCursos == 0){
            tieneInscripciones  = false;
            Toast.makeText(InscripcionesActivity.this, "Sin inscripciones",
                    Toast.LENGTH_LONG).show();
        } else {
            tieneInscripciones = true;
        }
    }

    private String calcularHorarioFinal(String sede, String aulas, String dias, String horarios) {
        String horarioFinal = "";
        String horarioAnterior = "";
        String horarioActual = "";

        String[] arraySede = sede.split(";");
        String[] arrayAulas = aulas.split(";");
        String[] arrayDias = dias.split(";");
        String[] arrayHorarios = horarios.split(";");

        for (int i = 0; i < arrayDias.length; i++) {
            horarioActual = arrayDias[i] + " " + arrayHorarios[i] + " [" + arraySede[i] + " - " + arrayAulas[i] + "]";
            if(!horarioActual.equals(horarioAnterior)){
                horarioFinal = horarioFinal + arrayDias[i] + " " + arrayHorarios[i] + " [" + arraySede[i] + " - " + arrayAulas[i] + "] \n";
            }
            horarioAnterior = horarioActual;
        }

        return horarioFinal;
    }
}

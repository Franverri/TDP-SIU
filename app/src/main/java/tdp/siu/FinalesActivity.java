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
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FinalesActivity extends AppCompatActivity {

    List<Final> finalList;
    FinalAdapter adapter;
    RecyclerView recyclerView;

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/alumno/finales/?padron=";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    String padron;
    Boolean periodoHabilitado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Guardo para saber si puedo clickear o no
        editorShared.putBoolean("clickTarjetaFinal", false);
        editorShared.apply();

        padron = sharedPref.getString("padron", null);
        periodoHabilitado = sharedPref.getBoolean("estaEnFinales", false);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_finales);

        configurarHTTPRequestSingleton();

        configurarRecyclerView();

        configurarClickNuevoFinal();
    }

    private void configurarClickNuevoFinal() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_nuevoFinal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(periodoHabilitado){
                    goOfertaFinal();
                } else {
                    Toast.makeText(FinalesActivity.this, "No se encuentra habilitado el período de inscripción a finales",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goOfertaFinal() {
        Intent intent = new Intent(this, OfertaFinalesActivity.class);
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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_finales);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        finalList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new FinalAdapter(this, finalList);

        buscarFinalesInscripto();
        recyclerView.setAdapter(adapter);
    }

    private void buscarFinalesInscripto() {
        if(padron != null){
            progress = ProgressDialog.show(this, "Finales",
                    "Recolectando datos...", true);
            String url = APIUrl + padron;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.i("API","Response: " + response.toString());
                    procesarRespuesta(response);
                    progress.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();
                    Log.i("Error.Response", String.valueOf(error));
                    Toast.makeText(FinalesActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                            Toast.LENGTH_LONG).show();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
    }

    private void procesarRespuesta(JSONObject response) {
        finalList.clear();
        JSONArray array = null;
        try {
            array = response.getJSONArray("finales");
        }catch (JSONException e){
            Log.i("JSON","Error al parsear JSON");
        }
        int cantFinales = array.length();
        for (int i = 0; i < cantFinales; i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = array.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                String idFinal = jsonobject.getString("id_final");
                String codigoMateria = jsonobject.getString("codigo");
                String nombreMateria = jsonobject.getString("nombre");
                String docente = jsonobject.getString("docente");
                String fecha = jsonobject.getString("fecha");
                String horario = jsonobject.getString("horario");
                String horarioFinal = fecha + " - " + horario;

                finalList.add(new Final(idFinal, nombreMateria, codigoMateria, docente, horarioFinal));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);
        if (cantFinales == 0){
            Toast.makeText(FinalesActivity.this, "Sin inscripciones",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String calcularHorarioFinal(String sede, String aulas, String dias, String horarios) {
        String horarioFinal = "";

        String[] arraySede = sede.split(";");
        String[] arrayAulas = aulas.split(";");
        String[] arrayDias = dias.split(";");
        String[] arrayHorarios = horarios.split(";");

        for (int i = 0; i < arrayDias.length; i++) {
            horarioFinal = horarioFinal + arrayDias[i] + " " + arrayHorarios[i] + " [" + arraySede[i] + " - " + arrayAulas[i] + "] \n";
        }

        return horarioFinal;
    }
}

package tdp.siu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import java.util.List;

public class CatedrasActivity extends AppCompatActivity {

    ProgressDialog progress;
    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";
    String padron;

    private String idMateria, codigoMateria, nombreMateria;

    List<Catedra> catedrasList;
    CatedrasAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
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
        queue.add(jsonObjectRequest);

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
            Log.i("JSON", String.valueOf(jsonobject));
            try {
                String numeroCurso = jsonobject.getString("id");
                String docente = jsonobject.getString("docente");
                String cupos = jsonobject.getString("cupos");
                String sedes = jsonobject.getString("sede");
                String aulas = jsonobject.getString("aulas");
                String dias = jsonobject.getString("dias");
                String horarios = jsonobject.getString("horarios");
                catedrasList.add(new Catedra("Curso " + numeroCurso, docente, horarios));

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

    private String obtenerCodigoMateria(String materia) {
        String first = materia.substring(1, 3);
        String second = materia.substring(4, 6);
        String codigo = first + second;
        return codigo;
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

        //FALTARIA SINCRONIZAR CON LA API
        //Reutilizo la CARD de Inscripciones
        catedrasList.add(new Catedra("Curso 1", "Fontela", "Lunes 17:00 - 23:00"));
        catedrasList.add(new Catedra("Curso 2",  "Fontela", "Martes 17:00 - 20:000 \n  Jueves 17:00 - 20:00"));
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialog(String curso, String catedra) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CatedrasActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CatedrasActivity.this);
        }
        builder.setTitle(curso + " - " + catedra)
                .setMessage("¿Confirmar inscripción?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Confirmar inscripción
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }
}

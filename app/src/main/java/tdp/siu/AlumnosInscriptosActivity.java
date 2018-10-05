package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ImageButton;

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

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlumnosInscriptosActivity extends AppCompatActivity{

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/docente/";
    int idCurso = -1;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    List<Alumno> alumnosList;
    AlumnosInscriptosAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recuperar el id del curso en el cual se hizo click
        Bundle b = getIntent().getExtras();
        if(b != null)
            idCurso = b.getInt("id");

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_alumnos_inscriptos);


        configurarHTTPRequestSingleton();

        configurarRecyclerView();

        configurarCsvButton();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private void configurarCsvButton(){
        ImageButton ib = (ImageButton) findViewById(R.id.csv_alumnos_button);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
                File f = new File(path, "Curso" + String.valueOf(idCurso) + ".csv");
                Log.i("DEBUG","path: " + f.getAbsolutePath());
                try {
                    CSVWriter writer = new CSVWriter(new FileWriter(f));
                    Log.i("DEBUG","FileWriter y CSVWriter inicializados");
                    List<String[]> data = new ArrayList<String[]>();
                    for (Alumno al : alumnosList){
                        data.add(new String[] {al.getNombre(), String.valueOf(al.getPadron()), String.valueOf(al.getPrioridad())});
                    }
                    Log.i("DEBUG","Data lista para ser escrita");
                    writer.writeAll(data);
                    Log.i("DEBUG","Data escrita en archivo");
                    writer.close();
                    MediaScannerConnection
                            .scanFile(AlumnosInscriptosActivity.this , new String[] {f.getAbsolutePath()},null , null);
                    Toast.makeText(AlumnosInscriptosActivity.this, "El archivo se exportó a " + f.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.i("IO","Error al inicializar el FileWriter");
                    Log.i("IO",e.getMessage());
                    Toast.makeText(AlumnosInscriptosActivity.this, "No fue posible exportar el archivo csv",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_inscriptos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        alumnosList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new AlumnosInscriptosAdapter(this, alumnosList);

        //Aca se manda el request al server
        enviarRequestInscriptos();

        //Estas dos lineas se deberían borrar cuando este el endpoint del server devolviendo un JSON
        //JSONObject value = exampleJSON();
        //actualizarAlumnosInscriptos(value);
    }

    private void enviarRequestInscriptos() {

        String url = APIUrl + "inscriptos/" + idCurso;
        Log.i("API", "url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        actualizarAlumnosInscriptos(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(AlumnosInscriptosActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void actualizarAlumnosInscriptos(JSONObject response){
        alumnosList.clear();
        JSONArray array = null;
        try {
            array = response.getJSONArray("inscriptos");
        }catch (JSONException e){
            Log.i("JSON","Error al parsear JSON");
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = array.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                String nombreAlumno = jsonobject.getString("apellido_y_nombre");
                String padronAlumno = jsonobject.getString("padron");
                String prioridadAlumno = jsonobject.getString("prioridad");
                boolean condicional = !jsonobject.getBoolean("es_regular");
                alumnosList.add(new Alumno(nombreAlumno, padronAlumno, prioridadAlumno, condicional));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);

    }

    private JSONObject exampleJSON() {
        JSONObject alumno1 = new JSONObject();
        try{
            alumno1.put("nombre", "Juan Perez");
            alumno1.put("padron", 99999);
            alumno1.put("prioridad", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject alumno2 = new JSONObject();
        try{
            alumno2.put("nombre", "Fernando Garcia");
            alumno2.put("padron", 12345);
            alumno2.put("prioridad", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject alumno3 = new JSONObject();
        try{
            alumno3.put("nombre", "Jose Gimenez");
            alumno3.put("padron", 78345);
            alumno3.put("prioridad", 50);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray arr = new JSONArray();
        arr.put(alumno1);
        arr.put(alumno2);
        arr.put(alumno3);
        JSONObject obj = new JSONObject();
        try{
            obj.put("inscriptos", arr);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return obj;
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



}
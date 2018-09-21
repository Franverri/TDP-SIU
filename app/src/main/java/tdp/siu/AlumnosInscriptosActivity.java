package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlumnosInscriptosActivity extends AppCompatActivity{

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    List<Alumno> alumnosList;
    AlumnosInscriptosAdapter adapter;
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

        setContentView(R.layout.activity_alumnos_inscriptos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_docente);
        setSupportActionBar(toolbar);

        configurarHTTPRequestSingleton();

        configurarRecyclerView();
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
        //enviarRequestCursos();

        //Estas dos lineas se deberían borrar cuando este el endpoint del server devolviendo un JSON
        JSONObject value = exampleJSON();
        actualizarAlumnosInscriptos(value);
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
                String nombreAlumno = jsonobject.getString("nombre");
                int padronAlumno = jsonobject.getInt("padron");
                int prioridadAlumno = jsonobject.getInt("prioridad");
                alumnosList.add(new Alumno(nombreAlumno, padronAlumno, prioridadAlumno));
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
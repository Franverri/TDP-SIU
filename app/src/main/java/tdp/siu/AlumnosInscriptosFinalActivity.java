package tdp.siu;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlumnosInscriptosFinalActivity extends AppCompatActivity {
    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/docente/inscriptos?id_final=";
    String idFinal = null;

    List<AlumnoFinal> alumnosList;
    AlumnosInscriptosFinalAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recuperar el id del curso en el cual se hizo click
        Bundle b = getIntent().getExtras();
        if (b != null)
            idFinal = b.getString("id");


        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_alumnos_inscriptos_final);


        configurarHTTPRequestSingleton();

        configurarRecyclerView();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_inscriptos_final);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        alumnosList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new AlumnosInscriptosFinalAdapter(this, alumnosList);

        //TODO: Probar request cuando funcione API
        //Aca se manda el request al server
        //enviarRequestInscriptos();

        //Estas dos lineas se deberían borrar cuando este el endpoint del server devolviendo un JSON
        JSONObject value = exampleJSON();
        actualizarAlumnosInscriptos(value);
    }

    private void enviarRequestInscriptos(){
        String url = APIUrl + idFinal;
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
                        Toast.makeText(AlumnosInscriptosFinalActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
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
                int notaAlumno = jsonobject.getInt("nota");
                boolean regular = !jsonobject.getBoolean("es_regular");
                alumnosList.add(new AlumnoFinal(nombreAlumno, padronAlumno, notaAlumno, regular));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);

    }

    private JSONObject exampleJSON() {
        JSONObject alumno1 = new JSONObject();
        try{
            alumno1.put("apellido_y_nombre", "Juan Perez");
            alumno1.put("padron", "99999");
            alumno1.put("nota", -1);
            alumno1.put("es_regular", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject alumno2 = new JSONObject();
        try{
            alumno2.put("apellido_y_nombre", "LeBron James");
            alumno2.put("padron", "12345");
            alumno2.put("nota", 9);
            alumno2.put("es_regular", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject alumno3 = new JSONObject();
        try{
            alumno3.put("apellido_y_nombre", "Jorge García");
            alumno3.put("padron", "88888");
            alumno3.put("nota", 10);
            alumno3.put("es_regular", true);
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

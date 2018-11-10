package tdp.siu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

public class CondicionalesActivity extends AppCompatActivity {
    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/docente/";
    int idCurso = -1;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    List<Alumno> alumnosList;
    CondicionalesAdapter adapter;
    RecyclerView recyclerView;
    Button aceptarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recuperar el id del curso en el cual se hizo click
        Bundle b = getIntent().getExtras();
        if(b != null)
            idCurso = b.getInt("id");

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_condicionales);

        configurarHTTPRequestSingleton();

        configurarAceptarButton();

        configurarRecyclerView();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_condicionales);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        alumnosList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new CondicionalesAdapter(this, alumnosList, aceptarButton);

        //Aca se manda el request al server
        //enviarRequestGetCondicionales();

        //Cambiar cuando este hecho el endpoint de la API
        actualizarCondicionales(mockJSON());

        recyclerView.setAdapter(adapter);
    }

    public void enviarRequestGetCondicionales() {
        String url = APIUrl + "condicionales?id_curso=" + idCurso;
        Log.i("API", "url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        actualizarCondicionales(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(CondicionalesActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void actualizarCondicionales(JSONObject response){
        try {
            alumnosList.clear();
            JSONArray array = response.getJSONArray("condicionales");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonobject = null;
                jsonobject = array.getJSONObject(i);
                String nombreAlumno = jsonobject.getString("apellido_y_nombre");
                String padronAlumno = jsonobject.getString("padron");
                String prioridadAlumno = jsonobject.getString("prioridad");
                alumnosList.add(new Alumno(nombreAlumno, padronAlumno, prioridadAlumno, true));
                recyclerView.setAdapter(adapter);
            }
        } catch (JSONException e){
            Log.i("JSON","Error al parsear JSON de Condicionales");
        }
    }

    private JSONObject mockJSON(){
        JSONObject response = new JSONObject();
        JSONArray arr = new JSONArray();
        JSONObject alumno1 = new JSONObject();
        JSONObject alumno2 = new JSONObject();
        JSONObject alumno3 = new JSONObject();
        try {
            alumno1.put("apellido_y_nombre", "Diego Abal");
            alumno1.put("padron",99999);
            alumno1.put("prioridad",1);

            alumno2.put("apellido_y_nombre", "Darío Herrera");
            alumno2.put("padron",12345);
            alumno2.put("prioridad",4);

            alumno3.put("apellido_y_nombre", "Néstor Pitana");
            alumno3.put("padron",67876);
            alumno3.put("prioridad",10);

            arr.put(alumno1);
            arr.put(alumno2);
            arr.put(alumno3);

            response.put("condicionales", arr);
            return response;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private void configurarAceptarButton(){
        aceptarButton = findViewById(R.id.aceptar_condicionales_button);
        aceptarButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                aceptarCondicionales();
            }
        });
    }

    private void aceptarCondicionales(){
        List<Integer> indexAlumnos = adapter.getChanges();
        JSONArray padrones = new JSONArray();
        for (Integer index :indexAlumnos){
            padrones.put(alumnosList.get(index).getPadron());
        }
        String url = APIUrl + "condicional?id_curso=" + idCurso;
        Log.i("API", "url: " + url);
        JSONObject request = new JSONObject();
        try{
            request.put("padrones", padrones);
        } catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, request, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        aceptarButton.setVisibility(View.INVISIBLE);
                        enviarRequestGetCondicionales();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(CondicionalesActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
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

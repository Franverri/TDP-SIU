package tdp.siu;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

public class AlumnosInscriptosFinalActivity extends AppCompatActivity {
    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/docente/";
    String idFinal = null;

    List<AlumnoFinal> alumnosList;
    AlumnosInscriptosFinalAdapter adapter;
    RecyclerView recyclerView;
    Button changesButton;

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

        configurarBotonCambiosNotas();

        configurarRecyclerView();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed(){
        if (changesButton.getVisibility() == View.VISIBLE){
            mostrarDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void mostrarDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Notas sin guardar")
                .setMessage("¿Desea continuar sin guardar? Se perderán todos los cambios realizados")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_inscriptos_final);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        alumnosList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new AlumnosInscriptosFinalAdapter(this, alumnosList, changesButton);

        //Aca se manda el request al server
        enviarRequestInscriptos();

    }

    private void enviarRequestInscriptos(){
        String url = APIUrl + "inscriptos?id_final=" + idFinal;
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
                boolean regular = jsonobject.getBoolean("es_regular");
                alumnosList.add(new AlumnoFinal(nombreAlumno, padronAlumno, notaAlumno, regular));
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        recyclerView.setAdapter(adapter);

    }

    private void configurarBotonCambiosNotas(){
        changesButton = findViewById(R.id.guardar_cambios_button);
        changesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray alumnosArray = new JSONArray();
                for (AlumnoFinal alumno : alumnosList){
                    if (alumno.HasChanged()){
                        JSONObject alumnoJSON = new JSONObject();
                        try{
                            alumnoJSON.put("padron",alumno.getPadron());
                            alumnoJSON.put("nota",alumno.getNota());
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        alumnosArray.put(alumnoJSON);
                        alumno.setChange(false);
                    }
                }
                JSONObject data = new JSONObject();
                try {
                    data.put("notas",alumnosArray);
                } catch (JSONException e){
                    e.printStackTrace();
                }
                enviarNotas(data);
                changesButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void enviarNotas(JSONObject data){
        String url = APIUrl + "notas?id_final=" + idFinal;
        Log.i("API", "url: " + url);
        Log.i("DEBUG", "data: " + data.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        Toast.makeText(AlumnosInscriptosFinalActivity.this, "Datos guardados",
                                Toast.LENGTH_LONG).show();
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

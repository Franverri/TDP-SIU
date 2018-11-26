package tdp.siu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CondicionalesActivity extends AppCompatActivity implements CondicionalesAdapter.AdministradorPerfiles {
    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/docente/";
    int idCurso = -1;

    String MAIL = "email";
    String PADRON = "padron";
    String NOMBRE = "nombre";
    String PRIORIDAD = "prioridad";
    String CARRERA = "carrera";
    String APELLIDO = "apellido";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    List<Alumno> alumnosList;
    CondicionalesAdapter adapter;
    RecyclerView recyclerView;
    Button aceptarButton;

    boolean puedeAceptar;

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
        puedeAceptar = sharedPref.getBoolean("estaEnInscripcion", false) || sharedPref.getBoolean("estaEnDesinscripcion", false);

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
        adapter = new CondicionalesAdapter(this, alumnosList, aceptarButton, this);

        //Aca se manda el request al server
        enviarRequestGetCondicionales();

        //Cambiar cuando este hecho el endpoint de la API
        //actualizarCondicionales(mockJSON());

        recyclerView.setAdapter(adapter);
    }

    public void requestProfile(String padron, final View anchor){
        String url = APIUrl + "alumno?padron=" + padron;
        Log.i("API", "url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API","Response: " + response.toString());
                        showProfile(response, anchor);

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


    public void showProfile(JSONObject response, View anchor){
        Map<String,String> profile = parseJSONProfile(response);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.popup_profile_alumno,null);
        //set background
        View backgroundImage = layout.findViewById(R.id.background);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(215);
        //load results
        ((TextView)layout.findViewById(R.id.tvNombre)).setText(profile.get(NOMBRE));
        ((TextView)layout.findViewById(R.id.tvPadron)).setText(profile.get(PADRON));
        ((TextView)layout.findViewById(R.id.tvPrioridad)).setText(profile.get(PRIORIDAD));
        ((TextView)layout.findViewById(R.id.tvMail)).setText(profile.get(MAIL));
        ((TextView)layout.findViewById(R.id.tvCarrera)).setText(profile.get(CARRERA));
        float density=this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        //Set up touch closing outside of pop-up
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });
        pw.setOutsideTouchable(true);
        //pw.showAsDropDown(anchor);
        // display the pop-up in the center
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    public Map<String,String> parseJSONProfile(JSONObject response){
        try{
            HashMap<String,String> map = new HashMap<String,String>();
            JSONArray carreras = response.getJSONArray(CARRERA);
            String strCarreras = carreras.getString(0);
            for (int i = 1; i < carreras.length(); i++){
                strCarreras = strCarreras + ", " + carreras.getString(i);
            }
            map.put(PADRON,response.getString(PADRON));
            map.put(NOMBRE,response.getString(NOMBRE)+" "+response.getString(APELLIDO));
            map.put(MAIL,response.getString(MAIL));
            map.put(CARRERA, strCarreras);
            map.put(PRIORIDAD, response.getString(PRIORIDAD));
            return map;
        }catch (JSONException e){
            return null;
        }
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
            int cantCondicionales = array.length();
            for (int i = 0; i < cantCondicionales; i++) {
                JSONObject jsonobject = array.getJSONObject(i);
                String nombreAlumno = jsonobject.getString("apellido_y_nombre");
                String padronAlumno = jsonobject.getString("padron");
                String prioridadAlumno = jsonobject.getString("prioridad");
                alumnosList.add(new Alumno(nombreAlumno, padronAlumno, prioridadAlumno, true));
            }
            if (cantCondicionales == 0){
                Toast.makeText(CondicionalesActivity.this, "No hay alumnos condicionales a esta materia",
                        Toast.LENGTH_LONG).show();
            }
            recyclerView.setAdapter(adapter);
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
            alumno1.put("padron","96803");
            alumno1.put("prioridad",1);

            alumno2.put("apellido_y_nombre", "Darío Herrera");
            alumno2.put("padron","00001");
            alumno2.put("prioridad",4);

            alumno3.put("apellido_y_nombre", "Néstor Pitana");
            alumno3.put("padron","00002");
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
                if (puedeAceptar){
                    aceptarCondicionales();
                } else{
                    Toast.makeText(CondicionalesActivity.this, "No es posible aceptar condicionales en este período",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void aceptarCondicionales(){
        List<Integer> indexAlumnos = adapter.getChanges();
        Log.d("DEBUG","Indices Alumnos: " + indexAlumnos.toString());
        JSONArray padrones = new JSONArray();
        for (Integer index :indexAlumnos){
            padrones.put(alumnosList.get(index).getPadron());
        }
        String url = APIUrl + "condicional?id_curso=" + idCurso;
        Log.i("API", "url: " + url);
        Log.i("API", "padrones: " + padrones.toString());
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
                        Toast.makeText(CondicionalesActivity.this, "Condicionales aceptados",
                                Toast.LENGTH_LONG).show();
                        adapter.resetChanges();
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

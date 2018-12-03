package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistorialActivity extends AlphaBackGroundActivity {

    ProgressDialog progress;
    EditText etSearch;

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/alumno/";

    private TableLayout tableLayout;
    private TableLayout tableHeader;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    String padron, nombreCarrera, codigoCarrera;

    TableRow.LayoutParams params1;
    TableRow.LayoutParams params2;
    TableRow.LayoutParams params3;
    TableRow.LayoutParams params4;

    TextView tvTitulo;

    int color;

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

        setContentView(R.layout.activity_historial);
        setAlphaBackGround();

        Bundle b = getIntent().getExtras();
        if(b != null){
            codigoCarrera = b.getString("codigoCarrera");
            nombreCarrera = b.getString("nombreCarrera");
            //Log.d("PRUEBAA", "codigos: " + codigoCarrera);
            //Log.d("PRUEBAA", "nombres: " + nombreCarrera);
        }

        configurarHTTPRequestSingleton();

        configurarTabla();
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

    private void configurarTabla() {

        color = getResources().getColor(R.color.colorTitleFont);
        tvTitulo = (TextView) findViewById(R.id.table_titulo);
        tvTitulo.setText(nombreCarrera);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //determine height and width
        int width = metrics.widthPixels;

        //read database and put values dynamically into table
        tableLayout = (TableLayout) findViewById(R.id.table_historial);

        tableHeader = (TableLayout) findViewById(R.id.table_header);

        //initialize header row and define LayoutParams
        params1 = new TableRow.LayoutParams(3*width/16, TableRow.LayoutParams.WRAP_CONTENT);
        params2 = new TableRow.LayoutParams(7*width/16, TableRow.LayoutParams.WRAP_CONTENT);
        params3 = new TableRow.LayoutParams(2*width/16, TableRow.LayoutParams.WRAP_CONTENT);
        params4 = new TableRow.LayoutParams(4*width/16, TableRow.LayoutParams.WRAP_CONTENT);

        TableRow header_row = new TableRow(this);



        //column 1
        TextView header_tv = new TextView(this);
        header_tv.setLayoutParams(params1);
        header_tv.setText("Codigo");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(color);
        header_tv.setTextSize(20);
        header_tv.setTypeface(header_tv.getTypeface(), Typeface.BOLD);
        header_row.addView(header_tv);

        //column 2
        header_tv = new TextView(this);
        header_tv.setLayoutParams(params2);
        header_tv.setText("Nombre");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(color);
        header_tv.setTextSize(20);
        header_tv.setTypeface(header_tv.getTypeface(), Typeface.BOLD);
        header_row.addView(header_tv);

        //column 3
        header_tv = new TextView(this);
        header_tv.setLayoutParams(params3);
        header_tv.setText("Nota");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(color);
        header_tv.setTextSize(20);
        header_tv.setTypeface(header_tv.getTypeface(), Typeface.BOLD);
        header_row.addView(header_tv);

        //column 4
        header_tv = new TextView(this);
        header_tv.setLayoutParams(params4);
        header_tv.setText("Fecha");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(color);
        header_tv.setTextSize(20);
        header_tv.setTypeface(header_tv.getTypeface(), Typeface.BOLD);
        header_row.addView(header_tv);

        header_row.setGravity(Gravity.CENTER);

        tableHeader.addView(header_row, 0);

        obtenerDatos();
    }

    private void obtenerDatos() {
        obtenerDatosHistorial();
        obtenerDatosAvance();
    }

    private void obtenerDatosAvance() {

        //AGREGAR ID_CARRERA AL ENDPOINT

        String url = APIUrl + "creditos?padron=" + padron + "&id_carrera=" + codigoCarrera;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarTarjeta(response);
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(HistorialActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    private void actualizarTarjeta(JSONObject response) {

        if(response.length() == 0){
            Toast.makeText(HistorialActivity.this, "No existen materias aprobadas aún",
                    Toast.LENGTH_LONG).show();
        } else {
            try {
                String creditosTotales = response.getString("creditos_totales");
                String creditosObtenidos = response.getString("creditos_obtenidos");
                String porcentaje = response.getString("porcentaje");
                actualizarDatosTarjeta(creditosObtenidos, creditosTotales, porcentaje);
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
    }

    private void actualizarDatosTarjeta(String creditosObtenidos, String creditosTotales, String porcentaje) {
        //Modifico creditos obtenidos
        int idCreditosObtenidos = getResources().getIdentifier("tv_creditosObtenidos","id", getPackageName());
        TextView tvCreditosObtenidos = (TextView) findViewById(idCreditosObtenidos);
        tvCreditosObtenidos.setText(creditosObtenidos);

        //Modifico creditos totales
        int idCreditosTotales = getResources().getIdentifier("tv_creditosTotales","id", getPackageName());
        TextView tvCreditosTotales = (TextView) findViewById(idCreditosTotales);
        tvCreditosTotales.setText(creditosTotales);

        //Modifico creditos obtenidos
        int idPorcentaje = getResources().getIdentifier("tv_porcentajeAvance","id", getPackageName());
        TextView tvPorcentaje = (TextView) findViewById(idPorcentaje);
        tvPorcentaje.setText(porcentaje + "%");
    }

    private void obtenerDatosHistorial() {
        progress = ProgressDialog.show(this, "Historial académico",
                "Recolectando datos...", true);
        String url = APIUrl + "historial?padron=" + padron + "&id_carrera=" + codigoCarrera;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarTabla(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(HistorialActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    private void actualizarTabla(JSONArray response) {
        tableLayout.removeAllViews();

        if(response.length() == 0){
            añadirFila(0,"-","-", "-", "-");
        } else {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonobject = null;
                try {
                    jsonobject = response.getJSONObject(i);
                    if(jsonobject.length() == 0){
                        Toast.makeText(HistorialActivity.this, "Sin materias aprobadas",
                                Toast.LENGTH_LONG).show();
                        añadirFila(0,"-", "", "-", "-");
                    } else {
                        try {
                            String nombreMateria = jsonobject.getString("nombre");
                            String codigoMateria = jsonobject.getString("codigo");
                            String nota = jsonobject.getString("nota");
                            String fecha = jsonobject.getString("fecha");
                            añadirFila(i, codigoMateria, nombreMateria, nota, fecha);
                        } catch (JSONException e) {
                            Log.i("JSON","Error al obtener datos del JSON");
                        }
                    }
                } catch (JSONException e) {
                    Log.i("JSON","Error al parsear JSON");
                }
            }
        }
    }

    private void añadirFila(int i, String codigoMateria, String nombreMateria, String nota, String fecha) {

        TableRow row = new TableRow(this);

        //column 1
        TextView tv = new TextView(this);
        tv.setLayoutParams(params1);
        tv.setText(codigoMateria);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER);
        row.addView(tv);

        //column 2
        tv = new TextView(this);
        tv.setLayoutParams(params2);
        tv.setText(nombreMateria);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(tv);

        //column 3
        tv = new TextView(this);
        tv.setLayoutParams(params3);
        tv.setText(nota);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER);
        row.addView(tv);

        //column 4
        tv = new TextView(this);
        tv.setLayoutParams(params4);
        tv.setText(fecha);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER);
        row.setGravity(Gravity.CENTER);
        row.addView(tv);

        tableLayout.addView(row, i);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }
}

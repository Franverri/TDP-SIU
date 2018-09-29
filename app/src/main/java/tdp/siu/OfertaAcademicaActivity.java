package tdp.siu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;

public class OfertaAcademicaActivity extends AppCompatActivity {

    ProgressDialog progress;
    EditText etSearch;

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    SwipeRefreshLayout pullToRefresh;
    ListView listaMaterias;
    ArrayList<Materia> listMateriaAux = new ArrayList<Materia>();
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    String padron;
    Boolean periodoHabilitado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        padron = sharedPref.getString("padron", null);
        periodoHabilitado = sharedPref.getBoolean("periodoHabilitado", false);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_oferta_academica);

        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listItems);

        listaMaterias = (ListView) findViewById(R.id.listaMaterias);
        listaMaterias.setAdapter(adapter);

        searchKeyboardClick();
        searchScreenClick();

        addMaterias();

        configurarClickMateria();

        configurarHTTPRequestSingleton();

        configurarRefresh();

        enviarRequestOferta("");
    }

    private void configurarRefresh() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                enviarRequestOferta(""); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void configurarClickMateria() {
        listaMaterias.setClickable(true);
        listaMaterias.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                if(periodoHabilitado){
                    goCurso(adapter.getItem(position),
                            listMateriaAux.get(position).getId(),
                            listMateriaAux.get(position).getCodigo(),
                            listMateriaAux.get(position).getNombre());
                } else {
                    Toast.makeText(OfertaAcademicaActivity.this, "El período de inscripción aún no se encuentra habilitado",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void goCurso(String materia, String id, String codigo, String nombre) {
        if(!materia.equals("No existen coincidencias")){
            Intent intent = new Intent(this, CatedrasActivity.class);
            Bundle b = new Bundle();
            b.putString("nombreMateria", nombre);
            b.putString("idMateria", id);
            b.putString("codigoMateria", codigo);
            b.putString("padron", padron);
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
        }
    }

    private void searchKeyboardClick() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String strMateria = String.valueOf(etSearch.getText());
                    hideKeyboard(OfertaAcademicaActivity.this);
                    etSearch.getText().clear();
                    filtrarMaterias(strMateria);
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void searchScreenClick() {
        etSearch.setLongClickable(false);
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String strMateria = String.valueOf(etSearch.getText());
                        hideKeyboard(OfertaAcademicaActivity.this);
                        etSearch.getText().clear();
                        filtrarMaterias(strMateria);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void filtrarMaterias(String strMateria) {
        enviarRequestOferta(strMateria);
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void enviarRequestOferta(String filtro) {
        progress = ProgressDialog.show(this, "Buscando materias",
                "Recolectando datos...", true);
        String url;

        if(filtro.equals("")){
            url = APIUrl + "alumno/oferta/"+padron;
        } else {
            url = APIUrl + "alumno/oferta/" + padron + "?filtro=" + filtro;
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        actualizarOferta(response);
                        progress.dismiss();
                        
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(OfertaAcademicaActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void actualizarOferta(JSONArray response) {
        listItems.clear();
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = response.getJSONObject(i);
                if(jsonobject.length() == 0){
                    listItems.add("No existen coincidencias");
                } else {
                    try {
                        String nombreMateria = jsonobject.getString("nombre");
                        String codigoMateria = jsonobject.getString("codigo");
                        String idMateria = jsonobject.getString("id");
                        listMateriaAux.add(new Materia(idMateria, codigoMateria, nombreMateria));
                        listItems.add("(" + codigoMateria + ") " + nombreMateria);
                    } catch (JSONException e) {
                        Log.i("JSON","Error al obtener datos del JSON");
                    }
                }
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
        }
        adapter.notifyDataSetChanged();
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

    private void addMaterias() {
        listItems.add("No hay materias disponibles");
        adapter.notifyDataSetChanged();
    }
}

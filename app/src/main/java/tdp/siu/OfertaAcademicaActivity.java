package tdp.siu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

    ListView listaMaterias;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        enviarRequestOferta();
    }

    private void configurarClickMateria() {
        listaMaterias.setClickable(true);
        listaMaterias.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                goCurso(adapter.getItem(position));
            }
        });
    }

    private void goCurso(String materia) {
        Intent intent = new Intent(this, CursosActivity.class);
        Bundle b = new Bundle();
        b.putString("materia", materia);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

    private void searchKeyboardClick() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String strMateria = String.valueOf(etSearch.getText());
                    hideKeyboard(OfertaAcademicaActivity.this);
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
                        filtrarMaterias(strMateria);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void filtrarMaterias(String strMateria) {
        //FALTA UN ENDPOINT MAS CON FILTRO EN LA API
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

    private void enviarRequestOferta() {
        progress = ProgressDialog.show(this, "Buscando materias",
                "Recolectando datos...", true);
        String url = APIUrl + "alumno/oferta";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
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

    private void actualizarOferta(JSONObject response) {
        listItems.clear();
        JSONArray array = null;
        try {
            array = response.getJSONArray("oferta");
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
                String nombreMateria = jsonobject.getString("nombre");
                String codigoMateria = jsonobject.getString("codigo");
                listItems.add("(" + codigoMateria + ") " + nombreMateria);
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
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

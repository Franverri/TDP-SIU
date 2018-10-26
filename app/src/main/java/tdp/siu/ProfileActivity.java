package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.TestLooperManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;
    private boolean esAlumno;

    TextView tvNombre, tvDNI, tvPadron, tvMail, tvPSW;

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/perfil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        esAlumno = sharedPref.getBoolean("logueadoAlumno", false);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTitle("Perfil");

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_profile);

        //Determinar si colocar Padron o Legajo según el usuario
        configurarAtributos();

        configurarHTTPRequestSingleton();
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

    private void configurarAtributos() {
        TextView tvAtributo = (TextView) findViewById(R.id.tvProfile_padron1);
        tvNombre = (TextView) findViewById(R.id.tvProfile_nombre2);
        tvDNI = (TextView) findViewById(R.id.tvProfile_DNI2);
        tvPadron = (TextView) findViewById(R.id.tvProfile_padron2);
        tvMail = (TextView) findViewById(R.id.tvProfile_mail2);
        tvPSW = (TextView) findViewById(R.id.tvProfile_psw2);
        if(esAlumno){
            tvAtributo.setText("Padrón:");
        } else {
            tvAtributo.setText("Legajo:");
        }
        completarCampos();
    }

    private void completarCampos() {
        tvNombre.setText(sharedPref.getString("nombre", ""));
        tvDNI.setText(sharedPref.getString("usuario", ""));
        if(esAlumno){
            tvPadron.setText(sharedPref.getString("padron", ""));
        } else {
            tvPadron.setText(sharedPref.getString("legajo", ""));
        }
        tvMail.setText(sharedPref.getString("mail", ""));

    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    public void guardarCambios(View view) {
        //TO DO: hacer llamada a API para modificar los datos
        Toast.makeText(this, "Cambios guardados!",
                Toast.LENGTH_LONG).show();
        /*
        String url = APIUrl + "?mail={nuevo_mail}&pswactual={actual_psw}&pswnueva={nueva_psw}";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Log.i("API","Response: " + response.toString());
                        //procesarRespuesta(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(ProfileActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);*/

        super.onBackPressed();
    }
}

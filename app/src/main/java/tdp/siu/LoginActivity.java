package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;

    private boolean esAlumno;

    RequestQueue queue;
    ProgressDialog progress;
    String APIUrl ="https://siu-api.herokuapp.com/login";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Verifico si ya se encuentra logueado
        boolean alumnoLogueado = sharedPref.getBoolean("logueadoAlumno", false);
        boolean docenteLogueado = sharedPref.getBoolean("logueadoDocente", false);
        if(alumnoLogueado){
            goMainAlumno();
        } else if(docenteLogueado){
            goMainDocente();
        }

        //Le quito la barra de notificaciones
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_login);

        //Fondo
        getWindow().setBackgroundDrawableResource(R.drawable.fondo_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            usuario = String.valueOf(mEmailView.getText());
            enviarRequestLogin(email,password);
        }
    }

    private void enviarRequestLogin(String username, String password) {
        progress = ProgressDialog.show(this, "SIU",
                "Validando datos...", true);
        String url = APIUrl + "?usuario=" + username + "&contrasena=" + password;
        Log.i("API","URL: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Log.i("API","Response: " + response.toString());
                        procesarRespuestaLogin(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(LoginActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void procesarRespuestaLogin(JSONObject response){
        int tipo = 0;
        try {
            tipo = response.getInt("tipo");
        }catch (JSONException e){
            Log.i("JSON","Error al parsear JSON 1");
        }
        if (tipo == 0){
            mEmailView.setError(getString(R.string.error_incorrect_user_or_password));
            mEmailView.requestFocus();
        } else if (tipo == 1){ //ALUMNO
            try {
                editorShared.putString("padron",response.getString("padron"));
                String nombre = response.getString("nombre");
                String apellido = response.getString("apellido");
                String email = response.getString("email");
                String codCarreras = response.getString("carreras");
                String nombreCarreras = response.getString("desc_carreras");
                boolean multiCarrera;
                if(codCarreras.contains(";")){
                    multiCarrera = true;
                } else {
                    multiCarrera = false;
                }
                editorShared.putBoolean("multiCarrera", multiCarrera);
                editorShared.putString("codigoCarreras", codCarreras);
                editorShared.putString("nombreCarreras", nombreCarreras);
                editorShared.putString("nombre", nombre + " " + apellido);
                editorShared.putString("usuario", usuario);
                editorShared.putString("mail", email);
                editorShared.putBoolean("logueadoAlumno",true);
                editorShared.apply();
                goMainAlumno();
            } catch (JSONException e){
                Log.i("JSON","Error al parsear JSON 2");
            }
        } else if (tipo == 2){ //DOCENTE
            try {
                editorShared.putString("legajo",response.getString("legajo"));
                String nombre = response.getString("nombre");
                String apellido = response.getString("apellido");
                String email = response.getString("email");
                editorShared.putString("usuario", usuario);
                editorShared.putString("nombre", nombre + " " + apellido);
                editorShared.putBoolean("logueadoDocente",true);
                editorShared.putString("mail", email);
                editorShared.apply();
                goMainDocente();
            } catch (JSONException e){
                Log.i("JSON","Error al parsear JSON");
            }
        }
    }


    private void goMainAlumno() {
        Intent intent = new Intent(this, MainActivityAlumno.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goMainDocente() {
        Intent intent = new Intent(this, MainActivityDocente.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}


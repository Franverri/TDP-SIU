package tdp.siu;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.StringRequest;

public class EncuestasActivity extends AlphaBackGroundActivity {

    RequestQueue queue;
    String APIUrl ="https://siu-api.herokuapp.com/";
    ProgressDialog progress;
    String padron, nombreCarrera, codigoCarrera, strDatos, idMateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_encuestas);
        setAlphaBackGround();

        Bundle b = getIntent().getExtras();
        if(b != null){
            codigoCarrera = b.getString("codigoMateria");
            nombreCarrera = b.getString("nombreMateria");
            idMateria = b.getString("idMateria");
            padron = b.getString("padron");
        }

        configurarHTTPRequestSingleton();

        setearTitulo();

        setearComentarios();

        configurarBtnEnviar();
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

    private void setearTitulo() {

        TextView tvTitulo = (TextView) findViewById(R.id.tvTituloEncuesta);
        tvTitulo.setText("[" + codigoCarrera + "] " + nombreCarrera);

    }

    private void setearComentarios(){
        EditText editText = findViewById(R.id.comentario_encuesta);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void configurarBtnEnviar() {

        Button btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean camposCompeltos = verificarCamposObligatorios();
                if(camposCompeltos){
                    strDatos = generarStrDatos();
                    enviarDatos(strDatos);
                    Log.d("PRUEBAA", strDatos);
                } else {
                    Toast.makeText(EncuestasActivity.this, "Debe completar todas las preguntas obligatorias",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void enviarDatos(String strDatos) {

        progress = ProgressDialog.show(this, "Encuestas",
                "Enviando respuestas...", true);
        String url = APIUrl + "alumno/encuestas?padron=" + padron + "&id_materia=" + idMateria + "&respuesta="+ strDatos;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("ok")){
                            Toast.makeText(EncuestasActivity.this, "Encuesta enviada",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(EncuestasActivity.this, "Error al intentar enviar la encuesta. Intente nuevamente",
                                    Toast.LENGTH_LONG).show();
                        }
                        progress.dismiss();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                        progress.dismiss();
                    }
                }
        );
        queue.add(postRequest);

    }

    private String generarStrDatos() {
        String strDatos = "";

        RadioGroup respuesta1 = (RadioGroup) findViewById(R.id.radio_group1);
        RadioGroup respuesta2 = (RadioGroup) findViewById(R.id.radio_group2);
        RadioGroup respuesta3 = (RadioGroup) findViewById(R.id.radio_group3);
        RadioGroup respuesta4 = (RadioGroup) findViewById(R.id.radio_group4);
        RadioGroup respuesta5 = (RadioGroup) findViewById(R.id.radio_group5);
        RadioGroup respuesta6 = (RadioGroup) findViewById(R.id.radio_group6);
        EditText respuesta7 = (EditText) findViewById(R.id.comentario_encuesta);

        strDatos = "{Pregunta1:" + (Integer.valueOf(respuesta1.indexOfChild(findViewById(respuesta1.getCheckedRadioButtonId())))+1) + ";";
        strDatos = strDatos + "Pregunta2:" + (Integer.valueOf(respuesta2.indexOfChild(findViewById(respuesta2.getCheckedRadioButtonId())))+1) + ";";
        strDatos = strDatos + "Pregunta3:" + (Integer.valueOf(respuesta3.indexOfChild(findViewById(respuesta3.getCheckedRadioButtonId())))+1) + ";";
        strDatos = strDatos + "Pregunta4:" + (Integer.valueOf(respuesta4.indexOfChild(findViewById(respuesta4.getCheckedRadioButtonId())))+1) + ";";
        strDatos = strDatos + "Pregunta5:" + (Integer.valueOf(respuesta5.indexOfChild(findViewById(respuesta5.getCheckedRadioButtonId())))+1) + ";";
        strDatos = strDatos + "Pregunta6:" + (Integer.valueOf(respuesta6.indexOfChild(findViewById(respuesta6.getCheckedRadioButtonId())))+1) + ";";
        strDatos = strDatos + "Pregunta7:\"" + respuesta7.getText() + "\"}";

        return strDatos;
    }

    private boolean verificarCamposObligatorios() {

        boolean camposCompletos;

        RadioGroup respuesta1 = (RadioGroup) findViewById(R.id.radio_group1);
        RadioGroup respuesta2 = (RadioGroup) findViewById(R.id.radio_group2);
        RadioGroup respuesta3 = (RadioGroup) findViewById(R.id.radio_group3);
        RadioGroup respuesta4 = (RadioGroup) findViewById(R.id.radio_group4);
        RadioGroup respuesta5 = (RadioGroup) findViewById(R.id.radio_group5);
        RadioGroup respuesta6 = (RadioGroup) findViewById(R.id.radio_group6);

        if(respuesta1.getCheckedRadioButtonId()!=-1 &&
                respuesta2.getCheckedRadioButtonId()!=-1 &&
                respuesta3.getCheckedRadioButtonId()!=-1 &&
                respuesta4.getCheckedRadioButtonId()!=-1 &&
                respuesta5.getCheckedRadioButtonId()!=-1 &&
                respuesta6.getCheckedRadioButtonId()!=-1){
            camposCompletos = true;
        } else {
            camposCompletos = false;
        }

        return camposCompletos;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

}

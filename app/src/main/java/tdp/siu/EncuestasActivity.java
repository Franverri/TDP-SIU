package tdp.siu;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EncuestasActivity extends AppCompatActivity {

    String padron, nombreCarrera, codigoCarrera;

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

        Bundle b = getIntent().getExtras();
        if(b != null){
            codigoCarrera = b.getString("codigoMateria");
            nombreCarrera = b.getString("nombreMateria");
            padron = b.getString("padron");
        }

        setearTitulo();

        configurarBtnEnviar();
    }

    private void setearTitulo() {

        TextView tvTitulo = (TextView) findViewById(R.id.tvTituloEncuesta);
        tvTitulo.setText("[" + codigoCarrera + "] " + nombreCarrera);

    }

    private void configurarBtnEnviar() {

        Button btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean camposCompeltos = verificarCamposObligatorios();
                if(camposCompeltos){
                    //enviarDatos()
                } else {
                    Toast.makeText(EncuestasActivity.this, "Debe completar todas las preguntas obligatorias",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

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

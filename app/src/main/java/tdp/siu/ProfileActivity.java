package tdp.siu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.TestLooperManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;
    private boolean esAlumno;

    TextView tvNombre, tvDNI, tvPadron, tvMail, tvPSW;

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
        tvDNI.setText(sharedPref.getString("dni", ""));
        tvPadron.setText(sharedPref.getString("padron", ""));
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
        super.onBackPressed();
    }
}

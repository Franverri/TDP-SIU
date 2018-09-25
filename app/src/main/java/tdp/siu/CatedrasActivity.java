package tdp.siu;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class CatedrasActivity extends AppCompatActivity {

    private String codigoMateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_cursos);

        Bundle b = getIntent().getExtras();
        String materia = ""; // or other values
        if(b != null){
            materia = b.getString("materia");
        }

        enviarRequestCursos(materia);

        setTitle("Cursos disponibles");
    }

    private void enviarRequestCursos(String materia) {
        String codigoMateria = obtenerCodigoMateria(materia);
        //Toast.makeText(this, codigoMateria, Toast.LENGTH_LONG).show();
    }

    private String obtenerCodigoMateria(String materia) {
        String first = materia.substring(1, 3);
        String second = materia.substring(4, 6);
        String codigo = first + second;
        return codigo;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }
}

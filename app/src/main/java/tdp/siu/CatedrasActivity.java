package tdp.siu;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class CatedrasActivity extends AppCompatActivity {

    private String codigoMateria;

    List<Inscripcion> catedrasList;
    InscripcionAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_catedras);

        setTitle("Cursos disponibles");

        Bundle b = getIntent().getExtras();
        String materia = ""; // or other values
        if(b != null){
            materia = b.getString("materia");
        }

        configurarRecyclerView(materia);
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

    private void configurarRecyclerView(String materia) {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_catedras);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        catedrasList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new InscripcionAdapter(this, catedrasList);

        enviarRequestCursos(materia);

        //FALTARIA SINCRONIZAR CON LA API
        //Reutilizo la CARD de Inscripciones
        catedrasList.add(new Inscripcion("Curso 1", "", "Fontela", "Lunes 17:00 - 23:00"));
        catedrasList.add(new Inscripcion("Curso 2", "",  "Fontela", "Martes 17:00 - 20:000 \n  Jueves 17:00 - 20:00"));
        recyclerView.setAdapter(adapter);
    }
}

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

public class InscripcionesActivity extends AppCompatActivity {

    List<Inscripcion> inscripcionList;
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

        setContentView(R.layout.activity_inscripciones);

        configurarRecyclerView();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private void configurarRecyclerView() {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_inscripciones);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        inscripcionList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new InscripcionAdapter(this, inscripcionList);

        //FALTARIA SINCRONIZAR CON LA API
        inscripcionList.add(new Inscripcion("TDP2", "7546", "Fontela", "Lunes 17:00 - 23:00"));
        inscripcionList.add(new Inscripcion("Algo 3", "7504", "Fontela", "Martes 17:00 - 20:000 \n  Jueves 17:00 - 20:00"));
        recyclerView.setAdapter(adapter);
    }
}

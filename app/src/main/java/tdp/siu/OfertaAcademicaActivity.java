package tdp.siu;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OfertaAcademicaActivity extends AppCompatActivity {

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

        addMaterias();

    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    private void addMaterias() {
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        adapter.notifyDataSetChanged();
    }
}

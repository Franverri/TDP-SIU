package tdp.siu;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class CatedrasActivity extends AppCompatActivity {

    private String idMateria, codigoMateria, nombreMateria;

    List<Catedra> catedrasList;
    CatedrasAdapter adapter;
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
        if(b != null){
            idMateria = b.getString("idMateria");
            //Log.i("PRUEBA", "ID    : " + idMateria);
            codigoMateria = b.getString("codigoMateria");
            //Log.i("PRUEBA", "Codigo: " + codigoMateria);
            nombreMateria = b.getString("nombreMateria");
            //Log.i("PRUEBA", "Nombre: " + nombreMateria);
        }

        configurarRecyclerView(idMateria);
    }

    private void enviarRequestCursos(String materia) {

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

    private void configurarRecyclerView(String idMateria) {
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_catedras);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        catedrasList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new CatedrasAdapter(this, catedrasList);

        enviarRequestCursos(idMateria);

        //FALTARIA SINCRONIZAR CON LA API
        //Reutilizo la CARD de Inscripciones
        catedrasList.add(new Catedra("Curso 1", "Fontela", "Lunes 17:00 - 23:00"));
        catedrasList.add(new Catedra("Curso 2",  "Fontela", "Martes 17:00 - 20:000 \n  Jueves 17:00 - 20:00"));
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialog(String curso, String catedra) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CatedrasActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CatedrasActivity.this);
        }
        builder.setTitle(curso + " - " + catedra)
                .setMessage("¿Confirmar inscripción?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Confirmar inscripción
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }
}

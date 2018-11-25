package tdp.siu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class NotificacionesActivity extends AlphaBackGroundActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_notificaciones);
        setAlphaBackGround();

        configurarBtnClear();
    }

    private void configurarBtnClear() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_clear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificacionesActivity.this, "Historial borrado",
                        Toast.LENGTH_LONG).show();
                String padron = sharedPref.getString("padron", null);
                editorShared.putString("strNotificaciones"+padron, "");
                editorShared.apply();
                agregarNotificaciones();
            }
        });
    }

    @Override
    protected void onResume() {
        agregarNotificaciones();
        super.onResume();
    }

    private void agregarNotificaciones() {

        //Obtengo el historial de notificaciones del SharedPref
        String padron = sharedPref.getString("padron", null);
        String strNotificaciones = sharedPref.getString("strNotificaciones"+padron, "");
        if(strNotificaciones.equals("")){
            strNotificaciones = "Sin notificaciones";
        }
        List<String> listaNotificaciones = Arrays.asList(strNotificaciones.split(";"));

        ListView listView = (ListView) findViewById(R.id.listaNotificaciones);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, listaNotificaciones);

        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

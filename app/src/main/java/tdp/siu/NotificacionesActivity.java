package tdp.siu;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class NotificacionesActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onResume() {
        agregarNotificaciones();
        super.onResume();
    }

    private void agregarNotificaciones() {

        //Obtengo el historial de notificaciones del SharedPref
        String strNotificaciones = sharedPref.getString("strNotificaciones", "");
        if(strNotificaciones.equals("")){
            strNotificaciones = "Sin notificaciones";
        }
        //String strNotificaciones = "noti 1;noti 2;noti 3;noti 4";
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
}

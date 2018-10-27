package tdp.siu;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HistorialActivity extends AppCompatActivity {

    int idIncrementeal = 0;
    //TableLayout tl;

    private TableLayout tableLayout;
    private TableLayout tableHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Agrego la flecha de volver atras en la barra superior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_historial);

        //configurarHeaders();

        configurarTabla();

        //agregarFila("7547", "Adm. y Control de Proy. Informaticos II", "10", "06/12/2018");

        //cargarTabla();
    }

    private void configurarTabla() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //determine height and width
        int width = metrics.widthPixels;

        //read database and put values dynamically into table
        tableLayout = (TableLayout) findViewById(R.id.table_historial);

        tableHeader = (TableLayout) findViewById(R.id.table_header);

        //initialize header row and define LayoutParams
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(52, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params3 = new TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params4 = new TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        TableRow header_row = new TableRow(this);

        //column 1
        TextView header_tv = new TextView(this);
        header_tv.setLayoutParams(params);
        header_tv.setText("Codigo");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(Color.WHITE);
        header_tv.setTextSize(20);
        header_row.addView(header_tv);

        //column 2
        header_tv = new TextView(this);
        header_tv.setLayoutParams(params);
        header_tv.setText("Nombre");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(Color.WHITE);
        header_tv.setTextSize(20);
        header_row.addView(header_tv);

        //column 3
        header_tv = new TextView(this);
        header_tv.setLayoutParams(params);
        header_tv.setText("Nota");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(Color.WHITE);
        header_tv.setTextSize(20);
        header_row.addView(header_tv);

        //column 4
        header_tv = new TextView(this);
        header_tv.setLayoutParams(params);
        header_tv.setText("Fecha");
        header_tv.setGravity(Gravity.CENTER);
        header_tv.setTextColor(Color.WHITE);
        header_tv.setTextSize(20);
        header_row.addView(header_tv);

        tableHeader.addView(header_row, 0);

        for (int i = 0; i < 20; i++) {

            TableRow row = new TableRow(this);

            //column 1
            TextView tv = new TextView(this);
            tv.setLayoutParams(params1);
            tv.setText("7547");
            tv.setGravity(Gravity.CENTER);
            row.addView(tv);

            //column 2
            tv = new TextView(this);
            tv.setLayoutParams(params2);
            tv.setText("Adm. y Control de Proy. Informaticos II");
            tv.setGravity(Gravity.CENTER);
            row.addView(tv);

            //column 3
            tv = new TextView(this);
            tv.setLayoutParams(params3);
            tv.setText("10");
            tv.setGravity(Gravity.CENTER);
            row.addView(tv);

            //column 4
            tv = new TextView(this);
            tv.setLayoutParams(params4);
            tv.setText("06/12/2018");
            tv.setGravity(Gravity.CENTER);
            row.addView(tv);

            tableLayout.addView(row, i);
        }
    }

    /*
    private void agregarFila(String strCodigo, String strNombre, String strNota, String strFecha) {

        TableRow tr_head = new TableRow(this);
        tr_head.setId(idIncrementeal);
        idIncrementeal = idIncrementeal + 1;
        tr_head.setBackgroundColor(Color.WHITE);        // part1
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //Primer columna
        TextView col_codigo = new TextView(this);
        col_codigo.setId(idIncrementeal);
        idIncrementeal = idIncrementeal + 1;
        col_codigo.setText(strCodigo);
        col_codigo.setTextColor(Color.BLACK);          // part2
        col_codigo.setPadding(5, 5, 5, 5);
        col_codigo.setGravity(Gravity.CENTER);
        col_codigo.setTextSize(20);
        tr_head.addView(col_codigo);// add the column to the table row here

        //Segunda columna
        TextView col_nombre = new TextView(this);    // part3
        col_nombre.setId(idIncrementeal);// define id that must be unique
        idIncrementeal = idIncrementeal + 1;
        col_nombre.setText(strNombre); // set the text for the header
        col_nombre.setTextColor(Color.BLACK); // set the color
        col_nombre.setTextSize(20);
        tr_head.addView(col_nombre); // add the column to the table row here

        //Tercer columna
        TextView col_nota = new TextView(this);    // part3
        col_nota.setId(idIncrementeal);// define id that must be unique
        idIncrementeal = idIncrementeal + 1;
        col_nota.setText(strNota); // set the text for the header
        col_nota.setTextColor(Color.BLACK); // set the color
        col_nota.setPadding(5, 5, 5, 5); // set the padding (if required)
        col_nota.setGravity(Gravity.CENTER);
        col_nota.setTextSize(20);
        tr_head.addView(col_nota); // add the column to the table row here

        //Cuarta columna
        TextView col_fecha = new TextView(this);    // part3
        col_fecha.setId(idIncrementeal);// define id that must be unique
        idIncrementeal = idIncrementeal + 1;
        col_fecha.setText(strFecha); // set the text for the header
        col_fecha.setTextColor(Color.BLACK); // set the color
        col_fecha.setPadding(5, 5, 5, 5); // set the padding (if required)
        col_fecha.setGravity(Gravity.CENTER);
        col_fecha.setTextSize(20);
        tr_head.addView(col_fecha); // add the column to the table row here

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }*/

    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    /*
    @SuppressLint("ResourceType")
    public void configurarHeaders(){
        tl = (TableLayout) findViewById(R.id.table_historial);
        TableRow tr_head = new TableRow(this);
        tr_head.setId(idIncrementeal);
        idIncrementeal = idIncrementeal + 1;
        tr_head.setBackgroundColor(Color.GRAY);        // part1
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //Primer columna
        TextView col_codigo = new TextView(this);
        col_codigo.setId(idIncrementeal);
        idIncrementeal = idIncrementeal + 1;
        col_codigo.setText("Código");
        col_codigo.setTextColor(Color.WHITE);          // part2
        col_codigo.setPadding(5, 5, 5, 5);
        col_codigo.setGravity(Gravity.CENTER);
        col_codigo.setTextSize(20);
        tr_head.addView(col_codigo);// add the column to the table row here

        //Segunda columna
        TextView col_nombre = new TextView(this);    // part3
        col_nombre.setId(idIncrementeal);// define id that must be unique
        idIncrementeal = idIncrementeal + 1;
        col_nombre.setText("Nombre"); // set the text for the header
        col_nombre.setTextColor(Color.WHITE); // set the color
        col_nombre.setPadding(5, 5, 5, 5); // set the padding (if required)
        col_nombre.setGravity(Gravity.CENTER);
        col_nombre.setTextSize(20);
        tr_head.addView(col_nombre); // add the column to the table row here

        //Tercer columna
        TextView col_nota = new TextView(this);    // part3
        col_nota.setId(idIncrementeal);// define id that must be unique
        idIncrementeal = idIncrementeal + 1;
        col_nota.setText("Nota"); // set the text for the header
        col_nota.setTextColor(Color.WHITE); // set the color
        col_nota.setPadding(5, 5, 5, 5); // set the padding (if required)
        col_nota.setGravity(Gravity.CENTER);
        col_nota.setTextSize(20);
        tr_head.addView(col_nota); // add the column to the table row here

        //Cuarta columna
        TextView col_fecha = new TextView(this);    // part3
        col_fecha.setId(idIncrementeal);// define id that must be unique
        idIncrementeal = idIncrementeal + 1;
        col_fecha.setText("Fecha"); // set the text for the header
        col_fecha.setTextColor(Color.WHITE); // set the color
        col_fecha.setPadding(5, 5, 5, 5); // set the padding (if required)
        col_fecha.setGravity(Gravity.CENTER);
        col_fecha.setTextSize(20);
        tr_head.addView(col_fecha); // add the column to the table row here

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }*/
}

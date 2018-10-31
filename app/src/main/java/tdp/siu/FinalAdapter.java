package tdp.siu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FinalAdapter extends RecyclerView.Adapter<FinalAdapter.ProductViewHolder> {

    String padron;
    Boolean estadoDesinscripcion, estadoInscripcion;
    Boolean clickableCard;
    int positionClick;
    String APIUrl ="https://siu-api.herokuapp.com/alumno";
    RequestQueue queue;
    ProgressDialog progress;

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Final> finalList;

    //getting the context and product list with constructor
    public FinalAdapter(Context mCtx, List<Final> finalList, boolean clickable) {
        this.mCtx = mCtx;
        this.finalList = finalList;
        this.clickableCard = clickable;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_final_layout, null);
        SharedPreferences sharedPref = mCtx.getSharedPreferences(mCtx.getString(R.string.saved_data), Context.MODE_PRIVATE);
        padron = sharedPref.getString("padron", null);
        configurarHTTPRequestSingleton();
        return new ProductViewHolder(view);
    }

    private void configurarHTTPRequestSingleton() {

        // Get RequestQueue Singleton
        queue = HTTPRequestSingleton.getInstance(mCtx.getApplicationContext()).
                getRequestQueue();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

        // Start the queue
        queue.start();

    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        //getting the product of the specified position
        Final finalActual = finalList.get(position);

        //binding the data with the view holder views
        holder.tvNombreMateria.setText(finalActual.getNombreMateria() + " (" + finalActual.getCodigoMateria() + ")");
        holder.tvNombreCatedra.setText(finalActual.getNombreCatedra());
        holder.tvHorario.setText(finalActual.getHorario());

        final String nombreMateria = finalActual.getNombreMateria();
        final String idFinal = finalActual.getIdFinal();
        final String fechaFinal = finalActual.getHorario();
        if(clickableCard){
            holder.ivCancel.setVisibility(View.GONE);
            holder.cvFinalCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(estoy48hsAntes(fechaFinal)){
                        mostrarDialogInscripcion(nombreMateria, idFinal);
                    } else {
                        Toast.makeText(mCtx, "No es posible inscribirse a menos de 48hs del final",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            holder.ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(estoy48hsAntes(fechaFinal)){
                        positionClick = position;
                        mostrarDialog(nombreMateria, idFinal);
                    } else {
                        Toast.makeText(mCtx, "No es posible desincribirse a menos de 48hs del final",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean estoy48hsAntes(String fechaFinal) {
        boolean estoy48hsAntes;

        String fecha = fechaFinal.substring(0,10);
        String hora = fechaFinal.substring(13,18);

        Calendar currentTime = Calendar.getInstance();
        int añoActual = currentTime.get(Calendar.YEAR);
        int mesActual = (currentTime.get(Calendar.MONTH)+1);
        int diaActual = currentTime.get(Calendar.DAY_OF_MONTH);
        int horaActual = currentTime.get(Calendar.HOUR_OF_DAY);
        int minutoActual = currentTime.get(Calendar.MINUTE);

        String strDate = fecha + " " + hora;
        //String strDate = "24/10/2018" + " " + "19:00";
        String strDateActual = diaActual + "/" + mesActual + "/" + añoActual + " " + horaActual + ":" + minutoActual;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = format.parse(strDate);
            Date dateActual = format.parse(strDateActual);

            Calendar c = Calendar.getInstance();
            c.setTime(dateActual); // Now use today date.
            c.add(Calendar.DATE, 2); // Adding 2 days
            String output = format.format(c.getTime());

            if(c.getTime().after(date)){
                estoy48hsAntes = false;
            } else {
                estoy48hsAntes = true;
            }
        } catch (ParseException e) {
            estoy48hsAntes = false;
            e.printStackTrace();
        }

        return estoy48hsAntes;

    }

    private void mostrarDialogInscripcion(String nombreMateria, final String idFinal) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        builder.setTitle("Inscipción al final de " + nombreMateria)
                .setMessage("¿Confirmar inscripción?")
                .setPositiveButton("Inscribirme", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        estadoInscripcion = false;
                        inscribirse(padron, idFinal);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();

    }

    private void inscribirse(String padron, String idFinal) {
        progress = ProgressDialog.show(mCtx, "Inscripción",
                "Inscribiendose a final...", true);
        String url = APIUrl + "/inscribir?final="+ idFinal +"&padron=" + padron;
        Log.i("PRUEBA", "URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        procesarRespuestaInscripcion(response);
                        progress.dismiss();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(mCtx, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void procesarRespuestaInscripcion(JSONObject response) {

        try {
            estadoInscripcion = response.getBoolean("estado");
            Log.i("PRUEBAAA", String.valueOf(estadoInscripcion));
        } catch (JSONException e) {
            Log.i("JSON","Error al parsear JSON");
        }

        if(estadoInscripcion == true){
            Toast.makeText(mCtx, "Inscripción exitosa!",
                    Toast.LENGTH_LONG).show();
            goMain();
        } else {
            Toast.makeText(mCtx, "Error al intentar desincribirse!",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void goMain() {

        Intent intent = new Intent(mCtx, MainActivityAlumno.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mCtx.startActivity(intent);

    }

    private void mostrarDialog(String nombreMateria, final String idFinal) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        builder.setTitle("Desincripcion al final de " + nombreMateria)
                .setMessage("¿Confirmar desinscripción?")
                .setPositiveButton("Desinscribirme", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        estadoDesinscripcion = false;
                        desincribirse(padron, idFinal);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }

    private void desincribirse(String padron, String idFinal) {
        if(padron != null && idFinal != null) {
            progress = ProgressDialog.show(mCtx, "Desinscripción",
                    "Desinscribiendose de final...", true);
            String url = APIUrl + "/desinscribir?final=" + idFinal + "&padron=" + padron;
            Log.i("URL", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.i("API","Response: " + response.toString());
                    procesarRespuesta(response);
                    progress.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();
                    Log.i("Error.Response", String.valueOf(error));
                    Toast.makeText(mCtx, "No fue posible conectarse al servidor, por favor intente más tarde",
                            Toast.LENGTH_LONG).show();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);

        } else {
            estadoDesinscripcion = false;
        }
    }

    private void procesarRespuesta(JSONObject response) {
        try {
            estadoDesinscripcion = response.getBoolean("estado");
            Log.i("PRUEBAAA", String.valueOf(estadoDesinscripcion));
        } catch (JSONException e) {
            Log.i("JSON","Error al parsear JSON");
        }

        if(estadoDesinscripcion == true){
            Toast.makeText(mCtx, "Desinscripción exitosa!",
                    Toast.LENGTH_LONG).show();
            finalList.remove(positionClick);
            notifyDataSetChanged();
        } else {
            Toast.makeText(mCtx, "Error al intentar desincribirse!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return finalList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreMateria, tvNombreCatedra, tvHorario;
        ImageView ivCancel;
        CardView cvFinalCard;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreMateria = itemView.findViewById(R.id.tvF_nombreMateria);
            tvNombreCatedra = itemView.findViewById(R.id.tvF_nombreCatedra);
            tvHorario = itemView.findViewById(R.id.tvF_horario);
            cvFinalCard = itemView.findViewById(R.id.cvFinalCard);
            ivCancel = itemView.findViewById(R.id.tvF_cancelButton);
        }
    }
}
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InscripcionAdapter extends RecyclerView.Adapter<InscripcionAdapter.ProductViewHolder> {

    String padron;
    String idCurso;
    Boolean estadoDesinscripcion;
    Boolean periodoHabilitado;
    int positionClick;
    String APIUrl ="https://siu-api.herokuapp.com/alumno/desinscribir";
    RequestQueue queue;
    ProgressDialog progress;

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Inscripcion> inscripcionList;

    //getting the context and product list with constructor
    public InscripcionAdapter(Context mCtx, List<Inscripcion> inscripcionList) {
        this.mCtx = mCtx;
        this.inscripcionList = inscripcionList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_inscripcion_layout, null);
        SharedPreferences sharedPref = mCtx.getSharedPreferences(mCtx.getString(R.string.saved_data), Context.MODE_PRIVATE);
        padron = sharedPref.getString("padron", null);
        idCurso = sharedPref.getString("idCursoDesinscribir", null);
        periodoHabilitado = sharedPref.getBoolean("estaEnDesinscripcion", false);
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
        final Inscripcion inscripcion = inscripcionList.get(position);
        idCurso = inscripcion.getIdCurso();
        positionClick = position;

        //binding the data with the view holder views
        holder.tvNombreMateria.setText(inscripcion.getNombreMateria() + " (" + inscripcion.getCodigoMateria() + ")");
        holder.tvNombreCatedra.setText(inscripcion.getNombreCatedra());
        holder.tvHorario.setText(inscripcion.getHorario());
        holder.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(periodoHabilitado){
                    mostrarDialog(inscripcion.getNombreMateria());
                } else {
                    Toast.makeText(mCtx, "No se encuentra habilitado el periodo de desincripción a cursadas",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void mostrarDialog(String nombreMateria) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        builder.setTitle("Desincripcion a " + nombreMateria)
                .setMessage("¿Confirmar desinscripción?")
                .setPositiveButton("Desinscribirme", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        estadoDesinscripcion = false;
                        desincribirse(padron, idCurso);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }

    private void desincribirse(String padron, final String idCurso) {
        if(padron != null && idCurso != null) {
            progress = ProgressDialog.show(mCtx, "Desinscripción",
                    "Desinscribiendose de materia...", true);
            String url = APIUrl + "?curso=" + idCurso + "&padron=" + padron;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.i("API","Response: " + response.toString());
                    procesarRespuesta(response);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("curso"+idCurso);
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
            inscripcionList.remove(positionClick);
            notifyDataSetChanged();
        } else {
            Toast.makeText(mCtx, "Error al intentar desincribirse!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return inscripcionList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreMateria, tvNombreCatedra, tvHorario;
        ImageView ivCancel;
        CardView cvInscrpcionCard;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreMateria = itemView.findViewById(R.id.tvI_nombreMateria);
            tvNombreCatedra = itemView.findViewById(R.id.tvI_nombreCatedra);
            tvHorario = itemView.findViewById(R.id.tvI_horario);
            cvInscrpcionCard = itemView.findViewById(R.id.cvInscripcionCard);
            ivCancel = itemView.findViewById(R.id.tvI_cancelButton);
        }
    }
}
package tdp.siu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CatedrasAdapter extends RecyclerView.Adapter<CatedrasAdapter.ProductViewHolder> {

    RequestQueue queue;
    private ActualizadorCursos mActualizadorCursos;
    private String padron;
    private String idMateria;
    private String APIUrl ="https://siu-api.herokuapp.com/";

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Catedra> catedraList;

    //getting the context and product list with constructor
    public CatedrasAdapter(Context mCtx, List<Catedra> catedraList, String padron, RequestQueue queue, ActualizadorCursos act, String idMateria) {
        this.mCtx = mCtx;
        this.catedraList = catedraList;
        this.padron = padron;
        this.queue = queue;
        this.mActualizadorCursos = act;
        this.idMateria = idMateria;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_catedra_layout, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Catedra catedra = catedraList.get(position);

        //binding the data with the view holder views
        holder.tvCurso.setText("Curso " + catedra.getCurso());
        holder.tvNombreCatedra.setText(catedra.getCatedra());
        holder.tvHorario.setText(catedra.getHorario());
        holder.tvCupos.setText(catedra.getCupos());

        if (Integer.parseInt(catedra.getCupos()) > 0) {
            holder.cvInscrpcionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialog(catedra.getCurso(), catedra.getCatedra());
                }
            });
        } else{
            holder.cvInscrpcionCard.setCardBackgroundColor(ResourcesCompat.getColor(mCtx.getResources(), R.color.colorUnclickableGrey, null));
        }
    }


    @Override
    public int getItemCount() {
        return catedraList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvCurso, tvNombreCatedra, tvHorario, tvCupos;
        CardView cvInscrpcionCard;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvCurso = itemView.findViewById(R.id.tvC_curso);
            tvNombreCatedra = itemView.findViewById(R.id.tvC_nombreCatedra);
            tvHorario = itemView.findViewById(R.id.tvC_horario);
            tvCupos = itemView.findViewById(R.id.tvC_cupos);
            cvInscrpcionCard = itemView.findViewById(R.id.cvCatedraCard);

        }
    }

    private void mostrarDialog(final String curso, String catedra) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        builder.setTitle("Curso " + curso + " - " + catedra)
                .setMessage("¿Confirmar inscripción?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enviarRequestInscripcion(curso);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }

    private void enviarRequestInscripcion(String idCurso) {

        String url = APIUrl + "alumno/inscribir?curso="+ idCurso +"&padron=" + padron;
        Log.i("PRUEBA", "URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        procesarRespuestaInscripcion(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        Toast.makeText(mCtx, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    private void procesarRespuestaInscripcion(JSONObject response){
        try{
            int estado = response.getInt("estado");
            switch (estado){
                case -1:
                    Toast.makeText(mCtx, "Error en la comunicación con el servidor",
                            Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(mCtx, "La inscripción se realizó correctamente",
                            Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(mCtx, OfertaAcademicaActivity.class);
                    mCtx.startActivity(myIntent);
                    break;
                case 2:
                    Toast.makeText(mCtx, "El curso seleccionado está lleno, pruebe con otro curso",
                            Toast.LENGTH_LONG).show();
                    mActualizadorCursos.enviarRequestCursos(idMateria);
                    break;
                case 3:
                    Toast.makeText(mCtx, "Ningún curso tiene vacantes disponibles. Puede anotarse como condicional",
                            Toast.LENGTH_LONG).show();
                    mActualizadorCursos.enviarRequestCursos(idMateria);
                    break;
            }
        } catch (JSONException e){
            Log.i("JSON","Error al obtener datos del JSON");
        }
    }

    public static interface ActualizadorCursos {
        void enviarRequestCursos(String idMateria);
    }

}
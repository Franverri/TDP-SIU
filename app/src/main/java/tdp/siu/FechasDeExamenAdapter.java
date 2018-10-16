package tdp.siu;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

public class FechasDeExamenAdapter extends RecyclerView.Adapter<FechasDeExamenAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<FechaExamen> fechasList;
    private String APIUrl ="https://siu-api.herokuapp.com/";
    private RequestQueue queue;
    private ActualizadorFechas mActualizadorFechas;

    //getting the context and product list with constructor
    public FechasDeExamenAdapter(Context mCtx, List<FechaExamen> productList, RequestQueue queue, ActualizadorFechas act) {
        this.mCtx = mCtx;
        this.fechasList = productList;
        this.queue = queue;
        this.mActualizadorFechas = act;
    }

    @Override
    public FechasDeExamenAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_fecha_examen, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FechasDeExamenAdapter.ProductViewHolder holder, final int position) {
        //getting the product of the specified position
        final FechaExamen fecha = fechasList.get(position);

        String numero = String.valueOf(position + 1);
        //binding the data with the view holder views
        holder.tvNumeroExamen.setText("Fecha " + numero);
        holder.tvFechaExamen.setText(fecha.getFecha());
        holder.tvHoraExamen.setText(fecha.getHora());
        holder.ivDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialog(position, fecha.getFecha(), fecha.getId() );
            }
        });
    }

    private void mostrarDialog(final int position, String fecha, final String id) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        String numero = String.valueOf(position + 1);
        builder.setTitle("Fecha " + numero + " - " + fecha)
                .setMessage("¿Quiere eliminar esta fecha?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fechasList.remove(position);
                        //TODO Está bien removerlo instantaneamente y actualizar luego? Otra forma?
                        notifyDataSetChanged();
                        informarFechaEliminada(id);
                        mActualizadorFechas.enviarRequestGetFechas();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }

    private void informarFechaEliminada(String idFecha) {

        String url = APIUrl + "docente/finales?final="+ idFecha;
        Log.i("PRUEBA", "URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        //TODO Procesar respuesta?

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

    @Override
    public int getItemCount() {
        return fechasList.size();
    }

    public static interface ActualizadorFechas {
        void enviarRequestGetFechas();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNumeroExamen, tvFechaExamen, tvHoraExamen;
        ImageView ivDeleteButton;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNumeroExamen = itemView.findViewById(R.id.tvNumeroExamen);
            tvFechaExamen = itemView.findViewById(R.id.tvFechaExamen);
            tvHoraExamen = itemView.findViewById(R.id.tvHoraExamen);
            ivDeleteButton = itemView.findViewById(R.id.ivDeleteButton);
        }
    }


}

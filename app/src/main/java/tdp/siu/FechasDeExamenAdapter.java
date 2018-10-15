package tdp.siu;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FechasDeExamenAdapter extends RecyclerView.Adapter<FechasDeExamenAdapter.ProductViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<FechaExamen> fechasList;

    //getting the context and product list with constructor
    public FechasDeExamenAdapter(Context mCtx, List<FechaExamen> productList) {
        this.mCtx = mCtx;
        this.fechasList = productList;
    }

    @Override
    public FechasDeExamenAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_fecha_examen, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FechasDeExamenAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final FechaExamen fecha = fechasList.get(position);

        final String numero = String.valueOf(position + 1);

        //binding the data with the view holder views
        holder.tvNumeroExamen.setText("Fecha " + numero);
        holder.tvFechaExamen.setText(fecha.getFecha());
        holder.tvHoraExamen.setText(fecha.getHora());
        holder.ivDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialog(numero, fecha.getFecha(), fecha.getId() );
            }
        });
    }

    private void mostrarDialog(String numero, String fecha, String id) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        builder.setTitle("Fecha " + numero + " - " + fecha)
                .setMessage("¿Quiere eliminar esta fecha?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Simplemente se cierra
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return fechasList.size();
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

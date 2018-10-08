package tdp.siu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class InscripcionAdapter extends RecyclerView.Adapter<InscripcionAdapter.ProductViewHolder> {


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
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        //getting the product of the specified position
        Inscripcion inscripcion = inscripcionList.get(position);

        //binding the data with the view holder views
        holder.tvNombreMateria.setText(inscripcion.getNombreMateria() + " (" + inscripcion.getCodigoMateria() + ")");
        holder.tvNombreCatedra.setText(inscripcion.getNombreCatedra());
        holder.tvHorario.setText(inscripcion.getHorario());
        holder.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inscripcionList.remove(position);
                notifyDataSetChanged();
                //FALTARIA PEGARLE A LA API PARA DESINCRIBIR
                Toast.makeText(mCtx, "Desinscripción exitosa!",
                        Toast.LENGTH_LONG).show();
            }
        });
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
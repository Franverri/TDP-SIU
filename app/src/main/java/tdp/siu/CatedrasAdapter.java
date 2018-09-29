package tdp.siu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class CatedrasAdapter extends RecyclerView.Adapter<CatedrasAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    private boolean puede;

    //we are storing all the products in a list
    private List<Catedra> catedraList;

    public static final String SHARED_PREF_NAME = "myloginapp";

    //getting the context and product list with constructor
    public CatedrasAdapter(Context mCtx, List<Catedra> catedraList) {
        this.mCtx = mCtx;
        this.catedraList = catedraList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_catedra_layout, null);
        SharedPreferences sharedPref = mCtx.getSharedPreferences(mCtx.getString(R.string.saved_data), Context.MODE_PRIVATE);
        puede = sharedPref.getBoolean("puedeInscribirse", false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Catedra catedra = catedraList.get(position);

        //binding the data with the view holder views
        holder.tvCurso.setText(catedra.getCurso() + " - [" + catedra.getCupos() + " cupos restantes]");
        holder.tvNombreCatedra.setText(catedra.getCatedra());
        holder.tvHorario.setText(catedra.getHorario());
        holder.cvInscrpcionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(puede){
                    mostrarDialog(catedra.getCurso(), catedra.getCatedra());
                } else {
                    Toast.makeText(mCtx, "Prioridad insuficiente para inscribirse",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mostrarDialog(String curso, String catedra) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
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


    @Override
    public int getItemCount() {
        return catedraList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvCurso, tvNombreCatedra, tvHorario;
        CardView cvInscrpcionCard;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvCurso = itemView.findViewById(R.id.tvC_curso);
            tvNombreCatedra = itemView.findViewById(R.id.tvC_nombreCatedra);
            tvHorario = itemView.findViewById(R.id.tvC_horario);
            cvInscrpcionCard = itemView.findViewById(R.id.cvCatedraCard);
        }
    }
}
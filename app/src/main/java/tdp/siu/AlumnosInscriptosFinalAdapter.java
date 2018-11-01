package tdp.siu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AlumnosInscriptosFinalAdapter extends RecyclerView.Adapter<AlumnosInscriptosFinalAdapter.ProductViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<AlumnoFinal> alumnosList;

    //getting the context and product list with constructor
    public AlumnosInscriptosFinalAdapter(Context mCtx, List<AlumnoFinal> productList) {
        this.mCtx = mCtx;
        this.alumnosList = productList;
    }

    @Override
    public AlumnosInscriptosFinalAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_alumno_final, null);
        return new AlumnosInscriptosFinalAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlumnosInscriptosFinalAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        AlumnoFinal alumno = alumnosList.get(position);
        int nota = alumno.getNota();

        String condicion;
        if (alumno.isRegular()){
            condicion = "Regular";
        } else {
            condicion = "Libre";
        }

        //binding the data with the view holder views
        holder.tvNombreAlumno.setText(alumno.getNombre());
        holder.tvPadronAlumno.setText(alumno.getPadron());
        holder.tvCondicionAlumno.setText(condicion);
        holder.tvNotaAlumno.setText(String.valueOf(nota));
        if (nota < 0) {
            holder.tvNotaAlumno.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return alumnosList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreAlumno, tvPadronAlumno, tvNotaAlumno, tvCondicionAlumno;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreAlumno = itemView.findViewById(R.id.tvNombreAlumno);
            tvPadronAlumno = itemView.findViewById(R.id.tvPadronAlumno);
            tvNotaAlumno = itemView.findViewById(R.id.tvNotaAlumno);
            tvCondicionAlumno = itemView.findViewById(R.id.tvCondicionAlumno);
        }
    }
}

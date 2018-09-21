package tdp.siu;

import android.content.Context;;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AlumnosInscriptosAdapter extends RecyclerView.Adapter<AlumnosInscriptosAdapter.ProductViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Alumno> alumnosList;

    //getting the context and product list with constructor
    public AlumnosInscriptosAdapter(Context mCtx, List<Alumno> productList) {
        this.mCtx = mCtx;
        this.alumnosList = productList;
    }

    @Override
    public AlumnosInscriptosAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_alumno_layout, null);
        return new AlumnosInscriptosAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlumnosInscriptosAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Alumno alumno = alumnosList.get(position);

        //binding the data with the view holder views
        holder.tvNombreAlumno.setText(alumno.getNombre());
        holder.tvPadronAlumno.setText(String.valueOf(alumno.getPadron()));
        holder.tvPrioridadAlumno.setText("Prioridad: " + String.valueOf(alumno.getPrioridad()));
    }

    @Override
    public int getItemCount() {
        return alumnosList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreAlumno, tvPadronAlumno, tvPrioridadAlumno;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreAlumno = itemView.findViewById(R.id.tvNombreAlumno);
            tvPadronAlumno = itemView.findViewById(R.id.tvPadronAlumno);
            tvPrioridadAlumno = itemView.findViewById(R.id.tvPrioridadAlumno);
        }
    }
}

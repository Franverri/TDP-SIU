package tdp.siu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CondicionalesAdapter extends RecyclerView.Adapter<CondicionalesAdapter.ProductViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Alumno> alumnosList;

    public List<Integer> changesList;

    private Button aceptarButton;

    //getting the context and product list with constructor
    public CondicionalesAdapter(Context mCtx, List<Alumno> productList, Button button) {
        this.mCtx = mCtx;
        this.alumnosList = productList;
        this.aceptarButton = button;
        changesList = new ArrayList<>();
    }

    @Override
    public CondicionalesAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_condicional_layout, null);
        return new CondicionalesAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CondicionalesAdapter.ProductViewHolder holder, final int position) {
        //getting the product of the specified position
        Alumno alumno = alumnosList.get(position);

        //binding the data with the view holder views
        holder.tvNombreAlumno.setText(alumno.getNombre());
        holder.tvPadronAlumno.setText(String.valueOf(alumno.getPadron()));
        holder.tvPrioridadAlumno.setText("Prioridad: " + String.valueOf(alumno.getPrioridad()));

        holder.swAlumno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    changesList.add(position);
                    if (aceptarButton.getVisibility() == View.INVISIBLE){
                        aceptarButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    changesList.remove(Integer.valueOf(position));
                    if (changesList.isEmpty()){
                        aceptarButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alumnosList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreAlumno, tvPadronAlumno, tvPrioridadAlumno;
        Switch swAlumno;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreAlumno = itemView.findViewById(R.id.tvNombreAlumno);
            tvPadronAlumno = itemView.findViewById(R.id.tvPadronAlumno);
            tvPrioridadAlumno = itemView.findViewById(R.id.tvPrioridadAlumno);
            swAlumno = itemView.findViewById(R.id.switchAlumno);
        }
    }
}

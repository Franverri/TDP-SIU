package tdp.siu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;

public class AlumnosInscriptosFinalAdapter extends RecyclerView.Adapter<AlumnosInscriptosFinalAdapter.ProductViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<AlumnoFinal> alumnosList;

    //Botón que aparece cuando se modifica alguna nota para guardar los cambios
    private Button changesButton;

    private String STR_NOTA = "Nota: ";
    String[] DISPLAYED_VALUES  = new String[] {"A", "D", "4", "5", "6", "7", "8", "9", "10"};

    //getting the context and product list with constructor
    public AlumnosInscriptosFinalAdapter(Context mCtx, List<AlumnoFinal> productList, Button changesButton) {
        this.mCtx = mCtx;
        this.alumnosList = productList;
        this.changesButton = changesButton;
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
        final AlumnoFinal alumno = alumnosList.get(position);
        String nota = alumno.getNota();
        String condicion;
        if (alumno.isRegular()){
            condicion = "Regular";
        } else {
            condicion = "Libre";
        }
        final ViewSwitcher viewSwitcher = holder.vsNota;
        final TextView textView = holder.tvNotaAlumno;
        final NumberPicker np = holder.npNotaAlumno;

        //binding the data with the view holder views
        holder.tvNombreAlumno.setText(alumno.getNombre());
        holder.tvPadronAlumno.setText(alumno.getPadron());
        holder.tvCondicionAlumno.setText(condicion);
        if (!nota.contentEquals("-1")){
            holder.tvNotaAlumno.setText(nota);
        }

        holder.npNotaAlumno.setMinValue(0);
        holder.npNotaAlumno.setMaxValue(DISPLAYED_VALUES.length-1);
        holder.npNotaAlumno.setDisplayedValues(DISPLAYED_VALUES);

        holder.ivEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showNext();
            }
        });

        holder.ivCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showPrevious();
            }
        });

        holder.ivConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nuevaNota = DISPLAYED_VALUES[np.getValue()];
                if (!nuevaNota.contentEquals(alumno.getNota())){
                    alumno.setNota(nuevaNota);
                    textView.setText(nuevaNota);
                    if (changesButton.getVisibility() == View.INVISIBLE){
                        changesButton.setVisibility(View.VISIBLE);
                    }
                }
                viewSwitcher.showPrevious();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alumnosList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreAlumno, tvPadronAlumno, tvNotaAlumno, tvCondicionAlumno;
        NumberPicker npNotaAlumno;
        ViewSwitcher vsNota;
        ImageView ivEditButton, ivConfirmButton, ivCancelButton;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreAlumno = itemView.findViewById(R.id.tvNombreAlumno);
            tvPadronAlumno = itemView.findViewById(R.id.tvPadronAlumno);
            vsNota = itemView.findViewById(R.id.vsNota);
            tvNotaAlumno = itemView.findViewById(R.id.tvNotaAlumno);
            npNotaAlumno = itemView.findViewById(R.id.npNotaAlumno);
            tvCondicionAlumno = itemView.findViewById(R.id.tvCondicionAlumno);
            ivEditButton = itemView.findViewById(R.id.ivEditButton);
            ivConfirmButton = itemView.findViewById(R.id.ivConfirmButton);
            ivCancelButton = itemView.findViewById(R.id.ivCancelButton);
        }
    }
}

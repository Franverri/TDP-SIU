package tdp.siu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
        final ViewSwitcher viewSwitcher = holder.vsNota;
        final TextView textView = holder.tvNotaAlumno;
        final EditText editText = holder.etNotaAlumno;

        //binding the data with the view holder views
        holder.tvNombreAlumno.setText(alumno.getNombre());
        holder.tvPadronAlumno.setText(alumno.getPadron());
        holder.tvCondicionAlumno.setText(condicion);
        holder.tvNotaAlumno.setText(String.valueOf(nota));
        holder.tvNotaAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    viewSwitcher.showNext();//if the current view is the Textview, then show
                }                           //the next one, which is the EditText
        });
        holder.etNotaAlumno.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textView.setText(editText.getText());
                    editText.setText("");
                    viewSwitcher.showPrevious();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    public int getItemCount() {
        return alumnosList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreAlumno, tvPadronAlumno, tvNotaAlumno, tvCondicionAlumno;
        EditText etNotaAlumno;
        ViewSwitcher vsNota;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreAlumno = itemView.findViewById(R.id.tvNombreAlumno);
            tvPadronAlumno = itemView.findViewById(R.id.tvPadronAlumno);
            vsNota = itemView.findViewById(R.id.vsNota);
            tvNotaAlumno = itemView.findViewById(R.id.tvNotaAlumno);
            etNotaAlumno = itemView.findViewById(R.id.etNotaAlumno);
            tvCondicionAlumno = itemView.findViewById(R.id.tvCondicionAlumno);
        }
    }
}

package tdp.siu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CursosAdapter extends RecyclerView.Adapter<CursosAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Curso> cursoList;

    //getting the context and product list with constructor
    public CursosAdapter(Context mCtx, List<Curso> productList) {
        this.mCtx = mCtx;
        this.cursoList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_curso_layout, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Curso curso = cursoList.get(position);

        //binding the data with the view holder views
        holder.tvNombreCurso.setText(curso.getNombreCurso());
        holder.tvNumeroCurso.setText("Curso número " + curso.getNumeroCurso());
        holder.tvAlumnosInscriptos.setText(String.valueOf(curso.getAlumnosInscriptos()));
        holder.tvVacantes.setText(String.valueOf(curso.getVacantesRestantes()));

        holder.cvCursoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AlumnosInscriptosActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", 1); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return cursoList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCurso, tvNumeroCurso, tvAlumnosInscriptos, tvVacantes;
        CardView cvCursoCard;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvNombreCurso = itemView.findViewById(R.id.tvCursoNombre);
            tvNumeroCurso = itemView.findViewById(R.id.tvCursoNumero);
            tvAlumnosInscriptos = itemView.findViewById(R.id.tvCursoAlumnos);
            tvVacantes = itemView.findViewById(R.id.tvCursoVacantes);
        }
    }
}
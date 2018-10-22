package tdp.siu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
        final Curso curso = cursoList.get(position);

        //binding the data with the view holder views
        holder.tvNombreCurso.setText(curso.getNombreCurso() + " (" + curso.getCodigoCurso() + ")");
        holder.tvNumeroCurso.setText("Curso número " + curso.getNumeroCurso());
        holder.tvAlumnosInscriptos.setText(String.valueOf(curso.getAlumnosInscriptos()));
        holder.tvVacantes.setText(String.valueOf(curso.getVacantesRestantes()));
        holder.cvCursoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, view);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_curso_docente);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_alumnos_inscriptos:
                                Intent intent = new Intent(mCtx, AlumnosInscriptosActivity.class);
                                Bundle b = new Bundle();
                                b.putInt("id", curso.getIdCurso()); //Your id
                                intent.putExtras(b); //Put your id to your next Intent
                                mCtx.startActivity(intent);
                                break;
                            case R.id.menu_fechas_examen:
                                intent = new Intent(mCtx, FechasDeExamenActivity.class);
                                b = new Bundle();
                                b.putInt("id", curso.getIdCurso()); //Your id
                                intent.putExtras(b); //Put your id to your next Intent
                                mCtx.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
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
            cvCursoCard = itemView.findViewById(R.id.cvCursoCard);
        }
    }
}
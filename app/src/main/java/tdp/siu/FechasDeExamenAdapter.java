package tdp.siu;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        //TODO card fecha
        View view = inflater.inflate(R.layout.card_curso_layout, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FechasDeExamenAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final FechaExamen fecha = fechasList.get(position);

        //binding the data with the view holder views
    }

    @Override
    public int getItemCount() {
        return fechasList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {


        public ProductViewHolder(View itemView) {
            super(itemView);

            //tvNombreCurso = itemView.findViewById(R.id.tvCursoNombre);
        }
    }


}

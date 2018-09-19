package tdp.siu;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfertaAcademicaFragment extends Fragment {

    ListView listaMaterias;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;


    public OfertaAcademicaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Oferta académica");
        NavigationView navigation = (NavigationView)getActivity().findViewById(R.id.nav_view_alumno);
        Menu drawer_menu = navigation.getMenu();
        MenuItem menuItem;
        menuItem = drawer_menu.findItem(R.id.nav_ofertaAcademica);
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                listItems);

        View rootView = inflater.inflate(R.layout.fragment_oferta_academica, container, false);

        listaMaterias = (ListView) rootView.findViewById(
                R.id.listaMaterias);
        listaMaterias.setAdapter(adapter);

        addMaterias();

        return rootView;
    }

    private void addMaterias() {
        listItems.add("Prueba 1");
        listItems.add("Prueba 2");
        adapter.notifyDataSetChanged();
    }

}

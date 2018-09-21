package tdp.siu;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class InscripcionesFragment extends Fragment {


    public InscripcionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Inscripciones");
        NavigationView navigation = (NavigationView)getActivity().findViewById(R.id.nav_view_alumno);
        Menu drawer_menu = navigation.getMenu();
        MenuItem menuItem;
        menuItem = drawer_menu.findItem(R.id.nav_inscripciones);
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inscripciones, container, false);
    }

}

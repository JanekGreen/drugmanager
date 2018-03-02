package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.IDrugFound;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByNameFragment extends Fragment implements IDrugFound {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_by_name, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((AddDrugActivity)getContext()).getSupportActionBar().getThemedContext());

    }

    @Override
    public void getDrugData(String ean) {

    }
}

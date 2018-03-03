package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.IDrugFound;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByNameFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    private CursorAdapter suggestionsAdapter;
    private DrugViewModel drugViewModel;
    private  SearchView searchView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
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
        searchView = (SearchView) item.getActionView();
        suggestionsAdapter = getSuggestionsAdapter();
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        searchView.setOnSuggestionListener(this);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        drugViewModel.getNameSuggestionsForDrug(newText)
                .subscribe(cursor -> suggestionsAdapter.swapCursor(cursor),
                        throwable -> System.out.println(throwable.getMessage()));
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        String selectedItem = getDrugName(position);
        searchView.setQuery(selectedItem,false);

        return true;
    }
    private CursorAdapter getSuggestionsAdapter() {
        return new SimpleCursorAdapter(getContext(),
                R.layout.dropdown_li_query_suggestion,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
    }
    private String getDrugName(int position){
        Cursor cursor = suggestionsAdapter.getCursor();
        cursor.moveToPosition( position );
        return cursor.getString(1);
    }

}

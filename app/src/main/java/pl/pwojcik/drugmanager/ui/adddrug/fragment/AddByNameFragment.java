package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druginfo.AddDrugManualActivity;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByNameFragment extends Fragment implements SearchView.OnQueryTextListener, DrugListAdapter.OnDrugListAdapterItemClick {
    private DrugViewModel drugViewModel;
    private SearchView searchView;
    private DrugListAdapter drugListAdapter;
    private ArrayList<DrugDb> drugListGlobal;
    @BindView(R.id.rvDrugList)
    RecyclerView rvDrugList;
    private Handler handler;
    private String mqueryString;


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("QUERY", mqueryString);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_by_name, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvDrugList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDrugList.setItemAnimator(new DefaultItemAnimator());
        rvDrugList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        drugListAdapter = new DrugListAdapter();
        drugListAdapter.setOnDrugListAdapterItemClick(this);
        rvDrugList.setAdapter(drugListAdapter);

        if (savedInstanceState != null) {
            mqueryString = savedInstanceState.getString("QUERY", null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Szukaj podając nazwę leku");
        searchView.setOnQueryTextListener(this);
        if (mqueryString != null) {
            handleSearch(mqueryString);
            searchView.setQuery(mqueryString, true);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        System.out.println("onQuery text change " + query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchTerm) {
        mqueryString = searchTerm;
        handler.removeCallbacksAndMessages(null);

        handler.postDelayed(() -> {
            handleSearch(mqueryString);
        }, 300);
        return true;
    }

    @Override
    public void onAdapterItemClick(int position, View sharedElement) {
        Intent intent = new Intent(getContext(), DrugInfoActivity.class);
        intent.putExtra("DRUG", drugListGlobal.get(position));
        intent.putExtra("TRANSITION_NAME", drugListGlobal.get(position).getName());
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(getActivity(), sharedElement, drugListGlobal.get(position).getName());

        startActivity(intent, options.toBundle());

    }

    private void handleSearch(String query) {
        if (query.length() > 2) {
            drugViewModel.getDrugsForName(query)
                    .subscribe(list -> {
                                if (list == null || list.size() == 0) {
                                    drugListAdapter.clearData();
                                    showSnackBar();
                                } else {
                                    drugListGlobal = new ArrayList<>(list);
                                    drugListAdapter.setDrugsForTime(list);
                                }
                            },
                            throwable -> Toast.makeText(getContext(),throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show());
        } else {

            drugListAdapter.clearData();
        }
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(rvDrugList, "Nie znaleziono!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Dodaj ręcznie", view -> {
            Intent intent = new Intent(getContext(), AddDrugManualActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        View view = snackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();

    }
}

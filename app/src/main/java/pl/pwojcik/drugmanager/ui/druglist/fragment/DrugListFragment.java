package pl.pwojcik.drugmanager.ui.druglist.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapterTouchHelper;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 19.02.18.
 */
public class DrugListFragment extends Fragment implements DrugListAdapterTouchHelper.RecyclerItemTouchHelperListener, DrugListAdapter.OnDrugListAdapterItemClick {

    @BindView(R.id.rvDrugList)
    RecyclerView rvDrugList;
    @BindView(R.id.constraintLayout)
    ConstraintLayout rootLayout;

    private DrugListViewModel drugListViewModel;
    private String selectedTimeName;
    private ArrayList<DrugDb> drugsForTimeGlobal;

    public DrugListFragment() {
    }

    public static DrugListFragment newInstance() {
        return new DrugListFragment();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drug_list_activity2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        selectedTimeName = args.getString("SELECTED_TIME", "Rano");
        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        rvDrugList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDrugList.setItemAnimator(new DefaultItemAnimator());
        rvDrugList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itSimpleCallback = new DrugListAdapterTouchHelper(0, ItemTouchHelper.LEFT, this);
        drugListViewModel.getDrugsForTime(selectedTimeName)
                .subscribe(drugsForTime -> {
                            drugsForTimeGlobal = new ArrayList<>(drugsForTime);
                            DrugListAdapter drugListAdapter = new DrugListAdapter(drugsForTime);
                            rvDrugList.setAdapter(drugListAdapter);
                            drugListAdapter.setOnDrugListAdapterItemClick(this);

                        },
                        e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                .show());

        new ItemTouchHelper(itSimpleCallback).attachToRecyclerView(rvDrugList);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DrugListAdapter.DrugListViewHolder) {
            DrugListAdapter drugListAdapter = (DrugListAdapter) rvDrugList.getAdapter();
            DrugDb removedItem = drugListAdapter.removeItem(position);
            long drugId = removedItem.getId();
            ArrayList<DrugTime> drugTime_ = new ArrayList<>();
            drugListViewModel.getIdDefinedTimeIdForName(selectedTimeName)
                    .doOnSuccess(definedTimeId -> {
                        drugListViewModel.getDrugTime(drugId,definedTimeId)
                                .subscribe(drugTime_::add);
                        drugListViewModel.removeDrugTime(definedTimeId, drugId);
                    })
                    .subscribe();
            Snackbar snackbar = Snackbar
                    .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
            snackbar.setAction("COFNIJ!", view -> {

                drugListAdapter.restoreItem(removedItem, position);
                drugListViewModel.restoreDrugTime(drugTime_.get(0));
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    @Override
    public void onAdapterItemClick(int position) {

    }
}
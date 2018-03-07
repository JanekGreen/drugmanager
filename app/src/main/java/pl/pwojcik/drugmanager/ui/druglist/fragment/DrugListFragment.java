package pl.pwojcik.drugmanager.ui.druglist.fragment;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapterTouchHelper;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.Misc;
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
    private DrugListAdapter drugListAdapter;

    public DrugListFragment() {
         drugListAdapter = new DrugListAdapter();
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

        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        rvDrugList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDrugList.setItemAnimator(new DefaultItemAnimator());
        rvDrugList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL)); rvDrugList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshView();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DrugListAdapter.DrugListViewHolder) {
            DrugListAdapter drugListAdapter = (DrugListAdapter) rvDrugList.getAdapter();
            DrugDb removedItem = drugListAdapter.removeItem(position);
            long drugId = removedItem.getId();
            List<DrugTime> relatedDrugTimes = new ArrayList<>();

            if(selectedTimeName.equals("DRUG_LIST__")) {

                drugListViewModel.getDrugTimesForDrug(drugId)
                        .subscribe(list -> {
                            relatedDrugTimes.addAll(list);
                            drugListViewModel.removeDrugTimes(list)
                                    .subscribe(drugTimes -> {
                                        drugListViewModel.removeDrug(removedItem);
                                    });

                        }, e -> {

                        });
                Snackbar snackbar = Snackbar
                        .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
                snackbar.setAction("COFNIJ!", view -> {

                    drugListAdapter.restoreItem(removedItem, position);
                    drugListViewModel.restoreDrug(removedItem)
                            .subscribe(drugDb -> {
                                drugListViewModel.restoreDrugTimes(relatedDrugTimes);
                            });
                });

                View view = snackbar.getView();
                CoordinatorLayout.LayoutParams para = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                para.bottomMargin = Misc.dpToPx(getContext(), 62 + 4);
                view.setLayoutParams(para);
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }else{
                drugListViewModel.getIdDefinedTimeIdForName(selectedTimeName)
                        .doOnSuccess(definedTimeId -> {
                            drugListViewModel.getDrugTime(drugId, definedTimeId)
                                    .subscribe(drugTime -> {
                                        relatedDrugTimes.add(drugTime);
                                        drugListViewModel.removeDrugTime(drugTime);
                                        drugListViewModel.updateOrSetAlarms(getContext())
                                                .subscribe(definedTimes -> System.out.println("Alarms have been set " + definedTimes.size()),
                                                        e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                                    });

                        })
                        .subscribe();

                Snackbar snackbar = Snackbar
                        .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
                snackbar.setAction("COFNIJ!", view -> {

                    drugListAdapter.restoreItem(removedItem, position);
                    drugListViewModel.restoreDrugTime(relatedDrugTimes.get(0));
                    drugListViewModel.updateOrSetAlarms(getContext())
                            .subscribe(definedTimes -> System.out.println("Alarms have been set " + definedTimes.size()),
                                    e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                });
                View view = snackbar.getView();
                CoordinatorLayout.LayoutParams para = (CoordinatorLayout.LayoutParams)view.getLayoutParams();
                para.bottomMargin = Misc.dpToPx(getContext(),62+4);
                view.setLayoutParams(para);
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onAdapterItemClick(int position, View sharedElement) {
        Intent intent = new Intent(getContext(), DrugInfoActivity.class);
        intent.putExtra("DRUG_ID",
                drugsForTimeGlobal.get(position).getId());
        intent.putExtra("TRANSITION_NAME", drugsForTimeGlobal.get(position).getName());
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(getActivity(), sharedElement, drugsForTimeGlobal.get(position).getName());

        startActivity(intent,options.toBundle());

    }

    private void refreshView(){
        Bundle args = getArguments();
        selectedTimeName = args.getString("SELECTED_TIME", "Rano");
        ItemTouchHelper.SimpleCallback itSimpleCallback = new DrugListAdapterTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itSimpleCallback).attachToRecyclerView(rvDrugList);

        if (!selectedTimeName.equals("DRUG_LIST__")) {
            //list of notifications for drugs
            drugListViewModel.getDrugsForTime(selectedTimeName)
                    .subscribe(drugsForTime -> {
                                drugsForTimeGlobal = new ArrayList<>(drugsForTime);
                                DrugListAdapter drugListAdapter = new DrugListAdapter(drugsForTime);
                                rvDrugList.setAdapter(drugListAdapter);
                                drugListAdapter.setOnDrugListAdapterItemClick(this);
                            },
                            e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                    .show());
        } else {
            //List of all drugs
            drugListViewModel.getAllDrugs()
                    .subscribe(drugsForTime -> {
                                drugsForTimeGlobal = new ArrayList<>(drugsForTime);
                                DrugListAdapter drugListAdapter = new DrugListAdapter(drugsForTime);
                                rvDrugList.setAdapter(drugListAdapter);
                                drugListAdapter.setOnDrugListAdapterItemClick(this);
                            },
                            e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                    .show());
        }
    }
}
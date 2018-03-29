package pl.pwojcik.drugmanager.ui.druglist.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pl.pwojcik.drugmanager.ui.druglist.DrugListActivity;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapterObserver;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapterTouchHelper;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.ui.uicomponents.DialogUtil;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 19.02.18.
 */
public class DrugListFragment extends Fragment implements DrugListAdapterTouchHelper.RecyclerItemTouchHelperListener, DrugListAdapter.OnDrugListAdapterItemClick {

    @BindView(R.id.rvDrugList)
    RecyclerView rvDrugList;
    @BindView(R.id.constraintLayout)
    LinearLayout rootLayout;
    @BindView(R.id.emptyDrugListView)
    RelativeLayout emptyDrugListView;
    @BindView(R.id.emptyNotificationListView)
    RelativeLayout emptyNotificationListView;

    private DrugListViewModel drugListViewModel;
    private String selectedTimeName;
    private ArrayList<DrugDb> drugsForTimeGlobal;
    private DrugListAdapter drugListAdapter;
    private  DrugListAdapterObserver observer;
    private String currentView;
    private IActivityCommunication iActivityCommunication;

    public interface IActivityCommunication{
        void setOrUpdateAlarms();
        void refreshActivityViewForFragment();
    }

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

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAttach(context);
    }

    private void _onAttach(Context context) {
        if(context instanceof DrugListActivity){
            iActivityCommunication = (IActivityCommunication) context;

        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println(" on view created");
        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        rvDrugList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDrugList.setItemAnimator(new DefaultItemAnimator());
        rvDrugList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itSimpleCallback = new DrugListAdapterTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itSimpleCallback).attachToRecyclerView(rvDrugList);
        drugListAdapter = new DrugListAdapter();
        rvDrugList.setAdapter(drugListAdapter);
        drugListAdapter.setOnDrugListAdapterItemClick(this);
        HashMap<String,View> emptyViews = new HashMap<>();
        emptyViews.put(Constants.DRUG_LIST,emptyDrugListView);
        emptyViews.put(Constants.DRUG_NOTIFICATION, emptyNotificationListView);
        currentView = Constants.DRUG_NOTIFICATION;

        observer = new DrugListAdapterObserver(this,rvDrugList, emptyViews, currentView);
        drugListAdapter.registerAdapterDataObserver(observer);
        refreshView();
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DrugListAdapter.DrugListViewHolder) {
            drugListAdapter = (DrugListAdapter) rvDrugList.getAdapter();
            DrugDb toBeRemoved = drugListAdapter.getItemIdForPosition(position);

            if (selectedTimeName.equals("DRUG_LIST__")) {
                drugListViewModel.getDrugTimesForDrug(toBeRemoved.getId())
                        .doOnSuccess(list -> {
                            if (list.size() > 0) {
                                DialogUtil dialogUtil = new DialogUtil(this);
                                dialogUtil.setButtonListener(new DialogUtil.DialogUtilButtonListener() {
                                    @Override
                                    public void onPositiveButtonClicked() {
                                        deleteDrugWithDrugs(list, position);

                                    }
                                    @Override
                                    public void onNegativeButtonClicked() {
                                        drugListAdapter.notifyDataSetChanged();

                                    }
                                });
                                dialogUtil.showYestNoDialog(getContext(),"Uwaga","Lek posiada przypomnienia, usunąć mimo to?");
                                throw new IllegalStateException("Nie można usunąć ponieważ przypisano przypomnienie do leku");
                            }
                        })
                        .subscribe(list -> deleteDrugWithDrugs(list, position), e -> {
                        });
            } else {
               deleteDrugTime(toBeRemoved.getId(), position);

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

        startActivity(intent, options.toBundle());

    }

    private void refreshView() {
        Bundle args = getArguments();
        selectedTimeName = args.getString("SELECTED_TIME", "Rano");
        currentView = "DRUG_LIST__".equals(selectedTimeName)? Constants.DRUG_LIST : Constants.DRUG_NOTIFICATION;
        observer.setActiveFragment(currentView);
        if (!selectedTimeName.equals("DRUG_LIST__")) {
            //list of notifications for drugs
            drugListViewModel.getDrugsForTime(selectedTimeName)
                    .subscribe(drugsForTime -> {
                                drugsForTimeGlobal = new ArrayList<>(drugsForTime);
                                drugListAdapter.setDrugsForTime(drugsForTime);
                            },
                            e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                    .show());
        } else {
            //List of all drugs
            drugListViewModel.getAllDrugs()
                    .subscribe(drugsForTime -> {
                                drugsForTimeGlobal = new ArrayList<>(drugsForTime);
                                drugListAdapter.setDrugsForTime(drugsForTime);
                            },
                            e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                    .show());
        }
    }
    private void deleteDrugWithDrugs(List<DrugTime> list, int position){
        DrugDb removedItem = drugListAdapter.removeItem(position);
        drugListAdapter.notifyItemRemoved(position);
        drugListViewModel.removeDrugTimes(list)
                .subscribe(drugTimes -> {
                    drugListViewModel.removeDrug(removedItem)
                            .subscribe(drugDb_ -> {
                                    iActivityCommunication.setOrUpdateAlarms();
                                    iActivityCommunication.refreshActivityViewForFragment();
                                Snackbar snackbar = Snackbar
                                        .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
                                drugListViewModel.getDefinedTimes();
                                snackbar.setAction("COFNIJ!", view -> {
                                    drugListAdapter.restoreItem(removedItem, position);
                                    drugListViewModel.restoreDrug(removedItem)
                                            .subscribe(drugDb -> {
                                                drugListViewModel.restoreDrugTimes(list)
                                                        .subscribe(drugTime_ -> {
                                                            iActivityCommunication.refreshActivityViewForFragment();
                                                            drugListAdapter.notifyDataSetChanged();
                                                            iActivityCommunication.setOrUpdateAlarms();
                                                        });
                                            });
                                });

                                View view = snackbar.getView();
                                CoordinatorLayout.LayoutParams para = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                                para.bottomMargin = Misc.dpToPx(getContext(), 62 + 4);
                                view.setLayoutParams(para);
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();
                            });
                });

    }
    private void deleteDrugTime(long drugId, int position){
        final List<DrugTime> relatedDrugTimes = new ArrayList<>();
        DrugDb removedItem = drugListAdapter.removeItem(position);
        drugListViewModel.getIdDefinedTimeIdForName(selectedTimeName)
                .doOnSuccess(definedTimeId -> drugListViewModel.getDrugTime(drugId, definedTimeId)
                        .subscribe(drugTime -> {
                            relatedDrugTimes.add(drugTime);
                            drugListViewModel.removeDrugTime(drugTime)
                                    .subscribe(drugTime_ -> {
                                        iActivityCommunication.refreshActivityViewForFragment();
                                        iActivityCommunication.setOrUpdateAlarms();

                                    });
                            Snackbar snackbar = Snackbar
                                    .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
                            snackbar.setAction("COFNIJ!", view -> {

                                drugListAdapter.restoreItem(removedItem, position);
                                drugListViewModel.restoreDrugTime(relatedDrugTimes.get(0))
                                        .subscribe(drugTime_ -> {
                                            iActivityCommunication.setOrUpdateAlarms();
                                            iActivityCommunication.refreshActivityViewForFragment();
                                        });
                            });
                            View view = snackbar.getView();
                            CoordinatorLayout.LayoutParams para = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                            para.bottomMargin = Misc.dpToPx(getContext(), 62 + 4);
                            view.setLayoutParams(para);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();
                        }))
                .subscribe(id -> System.out.println("Status OK"), e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void setLayoutForView(int viewId){
        DrugListActivity activity = (DrugListActivity) getActivity();
        if(activity!=null)
            activity.setLayoutForView(viewId);
    }

}
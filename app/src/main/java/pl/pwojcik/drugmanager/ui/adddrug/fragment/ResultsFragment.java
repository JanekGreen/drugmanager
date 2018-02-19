package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.ui.adddrug.adapter.DefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.DrugListActivity;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment implements DefinedTimeAdapter.SwitchChangeCallback {

    @BindView(R.id.tvDetectedDrugName)
    TextView tvDetectedDrugName;
    @BindView(R.id.btnAddDrug)
    Button btnAddDrug;
    @BindView(R.id.tvDetectedDrugProducer)
    TextView tvDetectedDrugProducer;
    @BindView(R.id.tvUsageType)
    TextView tvUsageType;
    @BindView(R.id.rvDefinedTimes)
    RecyclerView rvDefinedTimes;
    private DefinedTimeAdapter definedTimeAdapter;
    private boolean blockAdding = true;

    private DrugViewModel drugViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        definedTimeAdapter = new DefinedTimeAdapter();
        definedTimeAdapter.setSwitchChangeCallback(this);


        drugViewModel = ViewModelProviders.of(getActivity()).get(DrugViewModel.class);
        drugViewModel.getDefinedTimesData().observe(this, definedTimes -> {
            definedTimeAdapter.setDefinedTimes(definedTimes);
            definedTimeAdapter.notifyDataSetChanged();
        });
        drugViewModel.getSelectedTimesIds().observe(this, selectedIds -> {
            if (selectedIds != null) {
                if (selectedIds.isEmpty()) {
                    blockAdding = true;
                } else {
                    blockAdding = false;
                }
                definedTimeAdapter.setDrugTimes(selectedIds.keySet());
            }
        });
        drugViewModel.getDrugData().observe(this, drug -> {
            if (drug != null) {
                tvDetectedDrugName.setText(drug.getName());
                tvDetectedDrugProducer.setText(drug.getProducer());
                tvUsageType.setText(drug.getUsageType());
            }
        });
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        ButterKnife.bind(this, view);

        rvDefinedTimes.setAdapter(definedTimeAdapter);
        rvDefinedTimes.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @OnClick(R.id.btnAddDrug)
    public void onBtnAddDrugClicked() {
        if (!blockAdding) {
            drugViewModel.saveDrugTimeData()
                    .doAfterTerminate(() -> {
                        Intent intent = new Intent(getContext(), DrugListActivity.class);
                        startActivity(intent);
                    })
                    .subscribe(collection -> System.out.print("Saved"),
                            throwable ->Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show());

        } else {

            Toast.makeText(getContext(), "Wybierz pory w jakich brać leki", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onCheckedChangedCallback(long definedTimeId, boolean isSelected) {
        System.out.println("checkChanged definedTimeId" + definedTimeId + " isSelected " + isSelected);
        drugViewModel.addSelectedTimeForDrug(definedTimeId, isSelected);
    }
}

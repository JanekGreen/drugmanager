package pl.pwojcik.drugmanager.ui.druglist.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 19.02.18.
 */
public class DrugListFragment extends Fragment {

    @BindView(R.id.rvDrugList)
    RecyclerView rvDrugList;
    private DrugListViewModel drugListViewModel;

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
        String selectedTime = args.getString("SELECTED_TIME", "Rano");
        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        rvDrugList.setLayoutManager(new LinearLayoutManager(getContext()));
        drugListViewModel.getDrugsForTime(selectedTime)
                .subscribe(drugsForTime -> {
                    rvDrugList.setAdapter(new DrugListAdapter(getContext(),drugsForTime));

                        },
                        e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                .show());


    }
}
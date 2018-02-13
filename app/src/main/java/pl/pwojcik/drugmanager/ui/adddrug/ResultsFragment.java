package pl.pwojcik.drugmanager.ui.adddrug;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment {

    @BindView(R.id.tvDetectedDrugName)
    TextView tvDetectedDrugName;
    @BindView(R.id.btnAddDrug)
    Button btnAddDrug;
    @BindView(R.id.tvDetectedDrugProducer)
    TextView getTvDetectedDrugProducer;

    private DrugViewModel drugViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        drugViewModel = ViewModelProviders.of(getActivity()).get(DrugViewModel.class);
        drugViewModel.getData().observe(this, drug -> {
          if(drug!=null){
              tvDetectedDrugName.setText(drug.getName());
              getTvDetectedDrugProducer.setText(drug.getProducer());
          }
        });
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

}

package pl.pwojcik.drugmanager.ui.adddrug;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

}

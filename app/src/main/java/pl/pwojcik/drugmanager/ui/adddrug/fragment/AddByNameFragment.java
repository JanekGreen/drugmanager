package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.pwojcik.drugmanager.ui.adddrug.IDrugFound;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByNameFragment extends Fragment implements IDrugFound {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_by_name, container, false);
    }


    @Override
    public void getDrugData(String ean) {

    }
}

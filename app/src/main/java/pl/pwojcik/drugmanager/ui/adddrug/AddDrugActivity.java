package pl.pwojcik.drugmanager.ui.adddrug;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.AddByBarcodeFragment;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.AddByNameFragment;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.ResultsFragment;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pwojcik.pl.archcomponentstestproject.R;

import static pl.pwojcik.drugmanager.utils.Constants.ADD_BARCODE_TAG_NAME;
import static pl.pwojcik.drugmanager.utils.Constants.ADD_NAME_TAG_NAME;
import static pl.pwojcik.drugmanager.utils.Constants.RESULTS_FRAGMENT;

public class AddDrugActivity extends AppCompatActivity implements IDrugFound {


    private DrugViewModel drugViewModel;
    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);
        ButterKnife.bind(this);
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
        subscribeToData();
        Bundle bundle = getIntent().getExtras();
        String searchTypeSelected = bundle != null ? bundle.getString("SEARCH_TYPE_FRAGMENT") : null;
        if (savedInstanceState == null) {
            if (searchTypeSelected != null)
                setFragment(searchTypeSelected);
        }
    }

    @Override
    public void getDrugData(String ean) {
        drugViewModel.getDrugByEan(ean);
    }

    private boolean setFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (tag) {
            case ADD_NAME_TAG_NAME:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case ADD_BARCODE_TAG_NAME:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case RESULTS_FRAGMENT:
                transaction.setCustomAnimations(R.anim.enter_from_above, R.anim.exit_to_below);
                break;
        }
        currentFragment = manager.findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case ADD_NAME_TAG_NAME:
                    currentFragment = new AddByNameFragment();
                    System.out.println("CREATING");
                    break;
                case ADD_BARCODE_TAG_NAME:
                    currentFragment = new AddByBarcodeFragment();
                    break;
                case RESULTS_FRAGMENT:
                    currentFragment = new ResultsFragment();
                    break;
            }
        }
        transaction.replace(R.id.fragment_container, currentFragment, tag);
        //transaction.addToBackStack(null);
        transaction.commit();

        return true;

    }

    private void subscribeToData() {
        drugViewModel.getDrugData().removeObservers(this);
        drugViewModel.getDrugData()
                .observe(this, drug -> {
                    System.out.println("STATE CHANGED!");
                    if (drug != null) {
                        System.out.println("VISIBLE!");
                        setFragment(RESULTS_FRAGMENT);
                    } else {
                        System.out.println("INVISIBLE!");

                    }
                });
    }

}

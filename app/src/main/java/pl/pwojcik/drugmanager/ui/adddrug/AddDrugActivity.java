package pl.pwojcik.drugmanager.ui.adddrug;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.AddByBarcodeFragment;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.AddByNameFragment;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pwojcik.pl.archcomponentstestproject.R;

import static pl.pwojcik.drugmanager.utils.Constants.ADD_BARCODE_TAG_NAME;
import static pl.pwojcik.drugmanager.utils.Constants.ADD_NAME_TAG_NAME;

public class AddDrugActivity extends AppCompatActivity implements IDrugFound {


    private DrugViewModel drugViewModel;
    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);
        ButterKnife.bind(this);
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
        drugViewModel.getDrugDbData().observe(this,drug -> {
            if(drug!=null) {
                Intent intent = new Intent(this, DrugInfoActivity.class);
                if(drug.getId()!=0){
                    intent.putExtra("DRUG_ID", drug.getId());
                }else {
                    intent.putExtra("DRUG", drug);
                }
                startActivity(intent);
                finish();
            }
        });
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

    private void setFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (tag) {
            case ADD_NAME_TAG_NAME:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case ADD_BARCODE_TAG_NAME:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
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
            }
        }
        transaction.replace(R.id.fragment_container, currentFragment, tag);
        //transaction.addToBackStack(null);
        transaction.commit();

    }

}

package pl.pwojcik.drugmanager.ui.adddrug;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import pwojcik.pl.archcomponentstestproject.R;

public class AddDrugActivity extends AppCompatActivity implements IDrugFound{

    public final static String ADD_BARCODE_TAG_NAME="GET_BY_BARCODE";
    public final static String ADD_NAME_TAG_NAME="GET_BY_NAME";
    public final static String RESULTS_FRAGMENT="RESULTS_FRAGMENT";


    @BindView(R.id.getDrugInfoNav)
    BottomNavigationView navigation;


    private DrugViewModel drugViewModel;
    private Fragment currentFragment = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.nav_capture_by_name:
                if(!(currentFragment instanceof AddByNameFragment))
                  return setFragment(ADD_NAME_TAG_NAME);
               return true;
            case R.id.nav_capture_by_barcode:
                if(!(currentFragment instanceof AddByBarcodeFragment))
                 return setFragment(ADD_BARCODE_TAG_NAME);

        }

       return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        drugViewModel = ViewModelProviders.of(this).get(pl.pwojcik.drugmanager.ui.adddrug.DrugViewModel.class);
        subscribeToData();
        if(savedInstanceState == null) {
            setFragment(ADD_BARCODE_TAG_NAME);
        }
    }

    @Override
    public void getDrugData(String ean) {
       drugViewModel.getDrugByEan(ean);
    }

    private boolean setFragment(String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (tag){
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
         currentFragment= manager.findFragmentByTag(tag);
        if(currentFragment == null){
            switch (tag){
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
        transaction.replace(R.id.fragment_container, currentFragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();

        return true;

    }

    private void subscribeToData() {
        drugViewModel.getData().removeObservers(this);
        drugViewModel.getData()
                .observe(this, drug -> {
                    System.out.println("STATE CHANGED!");
                    if(drug != null) {
                        System.out.println("VISIBLE!");
                        setFragment(RESULTS_FRAGMENT);
                    }else{
                        System.out.println("INVISIBLE!");

                    }
                });
    }

}

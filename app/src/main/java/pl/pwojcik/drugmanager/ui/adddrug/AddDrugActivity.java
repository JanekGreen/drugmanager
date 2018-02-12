package pl.pwojcik.drugmanager.ui.adddrug;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import pwojcik.pl.archcomponentstestproject.R;

public class AddDrugActivity extends AppCompatActivity {

    @BindView(R.id.getDrugInfoNav)
    BottomNavigationView navigation;
    private Fragment fragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.nav_capture_by_name:
                if(!(fragment instanceof AddByNameFragment))
                    fragment = new AddByNameFragment();
                break;
            case R.id.nav_capture_by_barcode:
                if(!(fragment instanceof AddByBarcodeFragment))
                    fragment = new AddByBarcodeFragment();
                break;
        }

       return setFragment();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        this.fragment = new AddByBarcodeFragment();
        setFragment();
    }

    private boolean setFragment(){
        if(this.fragment == null){
            return false;
        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
        return true;

    }

}

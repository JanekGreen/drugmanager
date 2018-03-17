package pl.pwojcik.drugmanager.ui.adddrug;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.EOFException;
import java.util.NoSuchElementException;

import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.AddByBarcodeFragment;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.AddByNameFragment;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pl.pwojcik.drugmanager.ui.druglist.DefinedTimesActivity;
import pl.pwojcik.drugmanager.ui.uicomponents.DialogUtil;
import pwojcik.pl.archcomponentstestproject.R;

import static pl.pwojcik.drugmanager.utils.Constants.ADD_BARCODE_TAG_NAME;
import static pl.pwojcik.drugmanager.utils.Constants.ADD_NAME_TAG_NAME;

public class AddDrugActivity extends AppCompatActivity  {

    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String searchTypeSelected = bundle != null ? bundle.getString("SEARCH_TYPE_FRAGMENT") : null;
        if (savedInstanceState == null) {
            if (searchTypeSelected != null)
                setFragment(searchTypeSelected);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
           default: return super.onOptionsItemSelected(item);
        }

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

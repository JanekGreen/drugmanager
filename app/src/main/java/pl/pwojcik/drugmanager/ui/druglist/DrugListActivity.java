package pl.pwojcik.drugmanager.ui.druglist;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.SearchTypeListDialogFragment;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.MainListSpinnerAdapter;
import pl.pwojcik.drugmanager.ui.druglist.fragment.DrugListFragment;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.Constants;
import pwojcik.pl.archcomponentstestproject.R;

public class DrugListActivity extends AppCompatActivity implements SearchTypeListDialogFragment.Listener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.fabAdd)
    FloatingActionButton fab;
    private DrugListViewModel drugListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        drugListViewModel.getDefinedTimes().observe(this,listDefinedTimes ->{
            spinner.setAdapter( new MainListSpinnerAdapter(toolbar.getContext(),listDefinedTimes));
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              String selectedTime = spinner.getSelectedItem().toString()
                      .substring(0,spinner.getSelectedItem().toString().indexOf(" "));
              Bundle args = new Bundle();
                args.putString("SELECTED_TIME", selectedTime);
                Fragment fragment = DrugListFragment.newInstance();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drug_list_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.set_defined_times) {
            Intent intent = new Intent(this, DefinedTimesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fabAdd)
    public void onAddFabClicked() {
        SearchTypeListDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onScanTypeClicked(int position) {
        Intent intent = new Intent(this, AddDrugActivity.class);
        if (position == 0) {
            intent.putExtra("SEARCH_TYPE_FRAGMENT", Constants.ADD_BARCODE_TAG_NAME);
        } else {
            intent.putExtra("SEARCH_TYPE_FRAGMENT", Constants.ADD_NAME_TAG_NAME);
        }
        startActivity(intent);
    }

}

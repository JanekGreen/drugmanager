package pl.pwojcik.drugmanager.ui.druglist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.SearchTypeListDialogFragment;
import pl.pwojcik.drugmanager.ui.druglist.adapter.MainListSpinnerAdapter;
import pl.pwojcik.drugmanager.ui.druglist.fragment.DrugListFragment;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.Constants;
import pwojcik.pl.archcomponentstestproject.R;

import static pl.pwojcik.drugmanager.utils.Constants.DRUG_LIST;
import static pl.pwojcik.drugmanager.utils.Constants.DRUG_NOTIFICATION;

public class DrugListActivity extends AppCompatActivity implements SearchTypeListDialogFragment.Listener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.fabAdd)
    FloatingActionButton fab;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    String currentFragmentSelected = DRUG_NOTIFICATION;
    String currentTimeSelected = "";
    private DrugListViewModel drugListViewModel;
    boolean backPressed = false;
    private int selectedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {
            selectedItemPosition = savedInstanceState.getInt("SELECTED_ITEM", 0);
            currentFragmentSelected = savedInstanceState.getString("SELECTED_FRAGMENT", "");
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("BOTTOM_NAV", R.id.notificationItem));
            if (currentFragmentSelected.equals(DRUG_NOTIFICATION)) {

                spinner.setSelection(selectedItemPosition);
            } else {
                switchFragments(currentFragmentSelected, true);
            }
            changeViewForMode();
        }

        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        drugListViewModel.getDefinedTimes().observe(this, listDefinedTimes -> {
            spinner.setAdapter(new MainListSpinnerAdapter(toolbar.getContext(), listDefinedTimes));
            if (savedInstanceState != null) {
                spinner.setSelection(selectedItemPosition);
            }
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleViewChange(R.id.notificationItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> handleViewChange(item.getItemId()));

    }

    private void changeViewForMode() {
        if (currentFragmentSelected.equals(DRUG_LIST)) {
            spinner.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Lista leków");
        } else {
            spinner.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drug_list_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_defined_times:
                Intent intent = new Intent(this, DefinedTimesActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
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

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("On resume");
        drugListViewModel.getDefinedTimes();
    }

    @Override
    public void onBackPressed() {
        if (!backPressed) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                backPressed = false;

            }, 3000);

            backPressed = true;
            Toast.makeText(this, "Naciśnij jeszcze raz aby wyjść", Toast.LENGTH_SHORT).show();
        } else {
            this.finishAffinity();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("SELECTED_ITEM", selectedItemPosition);
        outState.putString("SELECTED_FRAGMENT", currentFragmentSelected);
        outState.putInt("BOTTOM_NAV", bottomNavigationView.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    private void switchFragments(String argument, boolean noAnimation) {

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(argument);
        Bundle args = new Bundle();
        if (fragment == null) {
            args.putString("SELECTED_TIME", argument);
            fragment = DrugListFragment.newInstance();
            fragment.setArguments(args);
        }
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        if (!argument.equals(DRUG_LIST)) {
            if (!noAnimation)
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            if (!noAnimation)
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        transaction
                .replace(R.id.container, fragment, argument)
                .commit();
        changeViewForMode();

    }

    private boolean handleViewChange(int type) {
        switch (type) {
            case R.id.drugListItem:
                if (currentFragmentSelected.equals(DRUG_NOTIFICATION)) {
                    currentFragmentSelected = DRUG_LIST;
                    currentTimeSelected = "";
                    changeViewForMode();
                    switchFragments(DRUG_LIST, false);
                    return true;
                }
                break;
            case R.id.notificationItem:
                String selectedTime;
                selectedItemPosition = spinner.getSelectedItemPosition();
                if (selectedItemPosition != -1) {
                    selectedTime = spinner.getSelectedItem().toString()
                            .substring(0, spinner.getSelectedItem().toString().indexOf(" "));

                    if (!selectedTime.equals(currentFragmentSelected)) {
                        currentTimeSelected = selectedTime;
                        currentFragmentSelected = DRUG_NOTIFICATION;
                        changeViewForMode();
                        switchFragments(selectedTime, false);
                    }
                }
                return true;
        }

        return false;
    }
}

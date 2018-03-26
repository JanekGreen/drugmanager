package pl.pwojcik.drugmanager.ui.druglist;

import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;
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
    private int selectedItemPosition;
    private List<String> listDefinedTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_drug_list2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        if (savedInstanceState != null) {
            selectedItemPosition = savedInstanceState.getInt("SELECTED_ITEM", 0);
            currentFragmentSelected = savedInstanceState.getString("SELECTED_FRAGMENT", "");
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("BOTTOM_NAV", R.id.notificationItem));
            if (currentFragmentSelected.equals(DRUG_NOTIFICATION)) {
                spinner.setSelection(selectedItemPosition);
            } else {
                switchFragments(DRUG_LIST, true);
            }
        }
        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        drugListViewModel.getDefinedTimes().observe(this, listDefinedTimes -> {
            if (listDefinedTimes == null || listDefinedTimes.isEmpty() && !currentFragmentSelected.equals(DRUG_LIST)) {
                currentTimeSelected = "";
                spinner.setVisibility(View.GONE);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle("Powiadomienia");
            } else {
                this.listDefinedTimes = listDefinedTimes;
                spinner.setAdapter(new MainListSpinnerAdapter(toolbar.getContext(), listDefinedTimes));

            }
            if (savedInstanceState != null) {
                spinner.setSelection(selectedItemPosition);
            }
            if (currentFragmentSelected.equals(DRUG_NOTIFICATION)) {

                Bundle bundle = getIntent().getExtras();

                if (bundle != null && bundle.getBoolean("NOTIFICATION_OFF", false)) {

                    NotificationManager notificationManager = (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    if (notificationManager != null) {
                        notificationManager.cancel(Constants.INTENT_REQUEST_CODE);
                    }

                    int requestCode = bundle.getInt("REQUEST_CODE", -1);

                    if (requestCode != -1) {
                        drugListViewModel.getDefinedTimeForRequestCode(requestCode)
                                .filter(list -> list.size() > 0)
                                .map(list -> list.get(0))
                                .subscribe(definedTime -> {
                                            if (this.listDefinedTimes != null && this.listDefinedTimes.contains(definedTime)) {
                                                spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(definedTime));
                                            }
                                        },
                                        e -> System.out.println(e.getMessage()));
                    }

                    getIntent().putExtra("NOTIFICATION_OFF",false);
                }

                handleViewChange(R.id.notificationItem, true);
            }


        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleViewChange(R.id.notificationItem, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> handleViewChange(item.getItemId(),false));

    }

    private void changeViewForMode() {
        if (currentFragmentSelected.equals(DRUG_LIST)) {
            spinner.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Lista leków");
            fab.show();
        } else if (currentFragmentSelected.equals(DRUG_NOTIFICATION)) {
            if (listDefinedTimes != null && !listDefinedTimes.isEmpty()) {
                spinner.setVisibility(View.VISIBLE);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            } else {
                spinner.setVisibility(View.GONE);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle("Powiadomienia");
            }

            fab.hide();
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
            case R.id.crashApp:
                throw new UnsupportedOperationException("Test crash to check error reporting");
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
            if (drugListViewModel != null)
                drugListViewModel.getDefinedTimes();
        }

        changeViewForMode();
        transaction
                .replace(R.id.container, fragment, argument)
                .commitAllowingStateLoss();

    }

    private boolean handleViewChange(int type, boolean noAnimation) {
        switch (type) {
            case R.id.drugListItem:
                if (currentFragmentSelected.equals(DRUG_NOTIFICATION)) {
                    currentFragmentSelected = DRUG_LIST;
                    currentTimeSelected = "";
                    switchFragments(DRUG_LIST, noAnimation);
                    return true;
                }
                break;
            case R.id.notificationItem:
                String selectedTime = "";
                selectedItemPosition = spinner.getSelectedItemPosition();
                if (selectedItemPosition != -1) {
                    if (spinner.getSelectedItem() != null) {
                        selectedTime = spinner.getSelectedItem().toString()
                                .substring(0, spinner.getSelectedItem().toString().indexOf(" "));
                    }
                } else {
                    drugListViewModel.getDefinedTimes();
                }
                if (!selectedTime.equals(currentFragmentSelected)) {
                    currentTimeSelected = selectedTime;
                    currentFragmentSelected = DRUG_NOTIFICATION;
                    switchFragments(selectedTime, noAnimation);
                }
                return true;
        }

        return false;
    }

}

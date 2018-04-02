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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.SearchTypeListDialogFragment;
import pl.pwojcik.drugmanager.ui.druglist.adapter.MainListSpinnerAdapter;
import pl.pwojcik.drugmanager.ui.druglist.fragment.DrugListFragment;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

import static pl.pwojcik.drugmanager.utils.Constants.DRUG_LIST;
import static pl.pwojcik.drugmanager.utils.Constants.DRUG_NOTIFICATION;

public class DrugListActivity extends AppCompatActivity implements SearchTypeListDialogFragment.Listener, DrugListFragment.IActivityCommunication {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.fabAdd)
    FloatingActionButton fab;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.container)
    NestedScrollView nestedScrollView;

    private DrugListViewModel drugListViewModel;
    private List<String> listDefinedTimes;
    private DrugListState drugListState;
    private boolean initialized = false;
    private boolean refreshCalled = false;

    //todo wywala się jak będąc na liście leków przechodze do leku, odznaczam go i wracam na powiadomienia (wtedy można 2x kliknąć na bottom nav powiadomienia)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate called ");
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_drug_list2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        refresh(savedInstanceState);
        // flag if set refresh will not be called inside onResume
        refreshCalled = true;
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drugListState.handleSpinnerSelectionChange();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item ->
        {
            System.out.println("onBottomnavigationClicked "+item.getItemId());
            return drugListState.changeMode(item.getItemId(), false);});
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
        if(!refreshCalled) {
            refresh(null);
        }
        refreshCalled = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        getIntent().putExtra("SELECTED_ITEM",(String) spinner.getSelectedItem());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("SELECTED_ITEM", drugListState.getSpinnerPosition());
        outState.putString("SELECTED_FRAGMENT", drugListState.getActiveFragmentTag());
        super.onSaveInstanceState(outState);
    }

    private void refresh(Bundle savedInstanceState){
        System.out.println("refresh called!");
        drugListViewModel.getDefinedTimes().subscribe(definedTimes->{
            this.listDefinedTimes = definedTimes;
            spinner.setAdapter(new MainListSpinnerAdapter(toolbar.getContext(), definedTimes));
            if (savedInstanceState == null) {
                //savedInstanceState is null create new state object
                drugListState = new DrugListState();
                drugListState.handleNotificationCall();
            } else {
                if (!initialized) {
                    //flag to ensure that state will be restored only once after orientation change
                    String currentFragmentSelected = savedInstanceState.getString("SELECTED_FRAGMENT", "");
                    int spinnerPosition = savedInstanceState.getInt("SELECTED_ITEM", -1);
                    if (currentFragmentSelected.equals(DRUG_LIST)) {
                        drugListState = new DrugListState(DRUG_LIST);
                        drugListState.setSpinnerSelection(spinnerPosition);
                    } else {
                        drugListState = new DrugListState(spinnerPosition);
                    }
                    initialized = true;
                }
            }

        }, e ->{
            e.printStackTrace();
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        });
    }
    public void setLayoutForView(int viewId) {
        nestedScrollView.setLayoutParams(Misc.getCoordinatorLayoutParams(this, viewId));
    }

    @Override
    public void setOrUpdateAlarms() {
        drugListViewModel.updateOrSetAlarms(this)
                .subscribe();
    }

    @Override
    public void refreshActivityViewForFragment() {
        refresh(null);

    }

    class DrugListState {
        private static final String DRUG_NOTIFICATION = Constants.DRUG_NOTIFICATION;
        private static final String DRUG_LIST = Constants.DRUG_LIST;

        DrugListState() {
            changeMode(bottomNavigationView.getSelectedItemId(), true);
        }

        DrugListState(int spinnerPosition) {
            applyViewDrugNotification();
            setSpinnerSelection(spinnerPosition);
        }

        DrugListState(String fragmentType) {
            if (fragmentType.equals(DRUG_LIST))
                changeMode(R.id.drugListItem, true);
            else {
                changeMode(R.id.notificationItem, true);
            }

        }

        private void handleSpinnerSelectionChange() {
            switchFragments(getTimeNameFromSpinner(), true);
        }

        private boolean changeMode(int type, boolean noAnimation) {

            switch (type) {
                case R.id.notificationItem:
                    applyViewDrugNotification();
                    switchFragments(getTimeNameFromSpinner(), noAnimation);
                    break;
                case R.id.drugListItem:
                    applyViewDrugList();
                    switchFragments(DRUG_LIST, noAnimation);
                    break;
            }
            return true;
        }

        private String getTimeNameFromSpinner() {
            if (spinner.getSelectedItem() == null)
                return "";

            return spinner.getSelectedItem().toString()
                    .substring(0, spinner.getSelectedItem().toString().lastIndexOf("-")).trim();
        }
        private String getSavedSpinnerTimeName(){
            return getIntent().getExtras() == null ? "" : getIntent().getExtras().getString("SELECTED_ITEM", "");
        }
        private void applyViewDrugList() {
            applyViewLackOfSpinnerWithTitle("Lista leków", true);
        }

        private void applyViewDrugNotification() {
            if (isSpinnerEmpty()) {
                applyViewLackOfSpinnerWithTitle("Powiadomienia", false);
            } else {
                if (getSupportActionBar() == null)
                    throw new NullPointerException("SupportActionBar is null");

                getSupportActionBar().setDisplayShowTitleEnabled(false);
                spinner.setVisibility(View.VISIBLE);
                //spinner.setSelection(getSavedSpinnerPosition());
                setSpinnerSelection(getSavedSpinnerTimeName());
            }
        }

        private void applyViewLackOfSpinnerWithTitle(String title, boolean fabVisible) {
            if (getSupportActionBar() == null)
                throw new NullPointerException("SupportActionBar is null");
            spinner.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);

            if (fabVisible)
                fab.show();
            else {
                fab.hide();
            }
        }

        private boolean isSpinnerEmpty() {
            return spinner.getAdapter().getCount() == 0;
        }

        private int getSpinnerPosition() {
            return spinner.getSelectedItemPosition();
        }

        private void setSpinnerSelection(int index) {
            if (index == -1)
                return;
            if (spinner.getAdapter() != null &&
                    index <= spinner.getAdapter().getCount() - 1)
                spinner.setSelection(index);
        }

        private void setSpinnerSelection(String definedTimeName){
            if (listDefinedTimes != null && listDefinedTimes.contains(definedTimeName)) {
                spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(definedTimeName));
            }
        }
        private void handleNotificationCall() {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.getBoolean("NOTIFICATION_OFF", false)) {

                NotificationManager notificationManager = (NotificationManager)
                        DrugListActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.cancel(Constants.INTENT_REQUEST_CODE);
                }

                int requestCode = bundle.getInt("REQUEST_CODE", -1);

                if (requestCode != -1) {
                    drugListViewModel.getDefinedTimeForRequestCode(requestCode)
                            .filter(list -> list.size() > 0)
                            .map(list -> list.get(0))
                            .subscribe(definedTime -> {
                                        if (listDefinedTimes != null && listDefinedTimes.contains(definedTime)) {
                                            spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(definedTime));
                                        }
                                    },
                                    e -> System.out.println(e.getMessage()));
                }

                getIntent().putExtra("NOTIFICATION_OFF", false);
            }
        }

        private void switchFragments(String viewId, boolean noAnimation) {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentByTag(viewId);
            Bundle args = new Bundle();
            if (fragment == null) {
                args.putString("VIEW_ID", viewId);
                fragment = DrugListFragment.newInstance();
                fragment.setArguments(args);
            }
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            if (!viewId.equals(DRUG_LIST)) {
                if (!noAnimation)
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            } else {
                if (!noAnimation)
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            }
            transaction
                    .replace(R.id.container, fragment, viewId)
                    .commitAllowingStateLoss();

        }

        private String getActiveFragmentTag() {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.container);
            return fragment != null ? fragment.getTag() : "__EMPTY__";
        }
    }

}

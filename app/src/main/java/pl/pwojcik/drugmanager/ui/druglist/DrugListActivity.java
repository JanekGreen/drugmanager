package pl.pwojcik.drugmanager.ui.druglist;

import android.app.NotificationManager;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
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
import pl.pwojcik.drugmanager.notification.service.RingtonePlayingService;
import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.SearchTypeListDialogFragment;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.MainListSpinnerAdapter;
import pl.pwojcik.drugmanager.ui.druglist.fragment.DrugListFragment;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

public class DrugListActivity extends AppCompatActivity implements SearchTypeListDialogFragment.Listener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.fabAdd)
    FloatingActionButton fab;
    private DrugListViewModel drugListViewModel;
    boolean backPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        drugListViewModel.getDefinedTimes().observe(this, listDefinedTimes -> {
            spinner.setAdapter(new MainListSpinnerAdapter(toolbar.getContext(), listDefinedTimes));

        });


        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTime = spinner.getSelectedItem().toString()
                        .substring(0, spinner.getSelectedItem().toString().indexOf(" "));
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
    protected void onStart() {
        Bundle extras = getIntent().getExtras();
        System.out.println("on Start entered");
        if (extras != null) {
            int requestCode = extras.getInt("REQUEST_CODE", -1);
            System.out.print("Request code"+ requestCode);
            if (requestCode != -1) {


                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.cancel(Constants.INTENT_REQUEST_CODE);

                    Intent stopIntent = new Intent(this, RingtonePlayingService.class);
                    stopService(stopIntent);
                }

                drugListViewModel.getDefinedTimeForRequestCode(requestCode)
                        .subscribe(list -> {
                            if (list != null && !list.isEmpty()) {
                                System.out.println("Spinner value " + list.get(0));
                                Misc.selectSpinnerItemByValue(spinner, list.get(0));
                            }
                        });
            }
        }

        super.onStart();
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
            this.finishAndRemoveTask();
        }
    }
}

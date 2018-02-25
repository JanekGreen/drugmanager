package pl.pwojcik.drugmanager.ui.druginfo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.TypeConverter;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.ui.adddrug.adapter.DefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druginfo.ActiveSubstanceAdapter;
import pl.pwojcik.drugmanager.ui.druglist.DrugListActivity;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

public class DrugInfoActivity extends AppCompatActivity implements DefinedTimeAdapter.SwitchChangeCallback{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.initialLetterIcon)
    MaterialLetterIcon initialLetterIcon;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvDrugNameDetails)
    TextView tvDrugNameDetails;

    @BindView(R.id.tvDrugNameDetails2)
    TextView tvDrugNameDetails2;

    @BindView(R.id.tvLeaflet)
    TextView tvLeaflet;

    @BindView(R.id.tvCharacteristics)
    TextView tvCharacteristics;

    @BindView(R.id.tvInternetSearch)
    TextView tvInternetSearch;

    @BindView(R.id.tvProducer)
    TextView tvProducer;

    @BindView(R.id.rvActiveSubstances)
    RecyclerView rvActiveSubstance;


    @BindView(R.id.rvDefinedTimes)
    RecyclerView rvDefinedTimes;
    private DefinedTimeAdapter definedTimeAdapter;
    private boolean blockAdding = true;
    private DrugViewModel drugViewModel;
    private ActiveSubstanceAdapter activeSubstanceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drug_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        definedTimeAdapter = new DefinedTimeAdapter();
        definedTimeAdapter.setSwitchChangeCallback(this);

        rvDefinedTimes.setAdapter(definedTimeAdapter);
        rvDefinedTimes.setLayoutManager(new LinearLayoutManager(this));


        rvActiveSubstance.setLayoutManager(new LinearLayoutManager(this));
        activeSubstanceAdapter = new ActiveSubstanceAdapter();
        rvActiveSubstance.setAdapter(activeSubstanceAdapter);

        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
        drugViewModel.getDrugDbData().observe(this, this::initializeView);
        drugViewModel.getDefinedTimesData().observe(this, definedTimes -> {
            definedTimeAdapter.setDefinedTimes(definedTimes);
            definedTimeAdapter.notifyDataSetChanged();
        });
        drugViewModel.getSelectedTimesIds().observe(this, selectedIds -> {
            if (selectedIds != null) {
                if (selectedIds.isEmpty()) {
                    blockAdding = true;
                } else {
                    blockAdding = false;
                }
                definedTimeAdapter.setDrugTimes(selectedIds.keySet());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            long drugId = extras.getLong("DRUG_ID",-1L);
            if(drugId == -1L) {
                DrugDb drugDb = extras.getParcelable("DRUG");
                drugViewModel.getDrugDbData().setValue(drugDb);
                getIntent().removeExtra("DRUG");
            }else{
                drugViewModel.getSelectedTimesIds(drugId);
                drugViewModel.getDrugDbData(drugId).observe(this, this::initializeView);
                getIntent().removeExtra("DRUG_ID");
            }

        }
    }

    private void initializeView(DrugDb drugDb) {
        initialLetterIcon.setLetter(drugDb.getName());
        tvProducer.setText(drugDb.getProducer());
        tvDrugNameDetails.setText(drugDb.getUsageType());
        tvDrugNameDetails2.setText(drugDb.getPackQuantity());
        tvName.setText(drugDb.getName());
        activeSubstanceAdapter.setActiveSubstances(Misc.getContentsDataFromDrugDb(drugDb));
        activeSubstanceAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,DrugListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChangedCallback(long definedTimeId, boolean isSelected) {
        System.out.println("checkChanged definedTimeId" + definedTimeId + " isSelected " + isSelected);
        drugViewModel.addSelectedTimeForDrug(definedTimeId, isSelected);
    }
    @OnClick(R.id.btnAddDrug)
    public void onBtnAddDrugClicked() {
        if (!blockAdding) {
            drugViewModel.saveDrugTimeData()
                    .doAfterTerminate(() -> {
                        Intent intent = new Intent(this, DrugListActivity.class);
                        startActivity(intent);

                    })
                    .subscribe(collection ->  drugViewModel.updateOrSetAlarms(this)
                                    .subscribe(definedTimes -> System.out.println("Alarms have been set "+definedTimes.size())
                                            ,Throwable::printStackTrace)
                            , throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());

        } else {

            Toast.makeText(this, "Wybierz pory w jakich brać leki", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}

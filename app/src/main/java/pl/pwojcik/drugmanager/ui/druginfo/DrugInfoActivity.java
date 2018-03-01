package pl.pwojcik.drugmanager.ui.druginfo;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.io.File;
import java.util.ArrayList;

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

public class DrugInfoActivity extends AppCompatActivity implements DefinedTimeAdapter.SwitchChangeCallback {

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
    private DrugViewModel drugViewModel;
    private ActiveSubstanceAdapter activeSubstanceAdapter;
    private String characteristicsUrl;
    private String leafletUrl;
    ArrayList<File> filesToDelete;


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
        filesToDelete = new ArrayList<>();

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
                definedTimeAdapter.setDrugTimes(selectedIds.keySet());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
        Bundle extras = getIntent().getExtras();
        handleExtras(extras);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("onNewIntent");
        Bundle extras = intent.getExtras();
        handleExtras(extras);
    }

    private void initializeView(DrugDb drugDb) {

        initialLetterIcon.setLetter(drugDb.getName());
        tvProducer.setText(drugDb.getProducer());
        tvDrugNameDetails.setText(drugDb.getUsageType());
        tvDrugNameDetails2.setText(drugDb.getPackQuantity());
        tvName.setText(drugDb.getName());
        activeSubstanceAdapter.setActiveSubstances(Misc.getContentsDataFromDrugDb(drugDb));
        supportStartPostponedEnterTransition();
        activeSubstanceAdapter.notifyDataSetChanged();
        characteristicsUrl = drugDb.getCharacteristics();
        leafletUrl = drugDb.getFeaflet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFiles();
        finishAfterTransition();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
        drugViewModel.saveDrugTimeData()
                .subscribe(collection -> drugViewModel.updateOrSetAlarms(this)
                                .subscribe(definedTimes -> finishAfterTransition()
                                        , Throwable::printStackTrace)
                        , throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @OnClick(R.id.tvCharacteristics)
    public void ontvCharacteristicsClicked() {
       handleFileDownload(characteristicsUrl);
    }

    @OnClick(R.id.tvInternetSearch)
    public void ontvInternetSearchClicked() {
        String url = "https://www.google.pl/search?q="+tvName.getText().toString();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.tvLeaflet)
    public void onTvleafletClicked() {
        handleFileDownload(leafletUrl);
    }

    private void handleExtras(Bundle extras) {
        if (extras != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                System.out.println("TRANSITION_NAME " + extras.getString("TRANSITION_NAME"));
                if (initialLetterIcon.getTransitionName() == null)
                    supportPostponeEnterTransition();
                initialLetterIcon.setTransitionName(extras.getString("TRANSITION_NAME"));
            }
            long drugId = extras.getLong("DRUG_ID", -1L);
            DrugDb drugDb = extras.getParcelable("DRUG");
            if (drugDb != null) {
                drugViewModel.getDrugDbData().setValue(drugDb);
                getIntent().removeExtra("DRUG");
            } else if (drugId != -1L) {
                System.out.println("onNewStart " + drugId);
                drugViewModel.getSelectedTimesIds(drugId);
                drugViewModel.getDrugDbData(drugId).observe(this, this::initializeView);
                getIntent().removeExtra("DRUG_ID");
            }
        }
    }

    private void handleFileProcessing(File file) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setDataAndType(Uri.fromFile(file),"application/pdf");
        startActivity(myIntent);
    }

    private void handleFileDownload(String url){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Proszę czekać trwa pobieranie pliku");
        drugViewModel.downloadFileByUrl(url)
                .doOnSubscribe(disposable -> progressDialog.show())
                .subscribe(downloadedFile -> {
                            progressDialog.dismiss();
                            handleFileProcessing(downloadedFile);
                            filesToDelete.add(downloadedFile);
                        },
                        throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show());
    }
    private void removeFiles() {
        for(File file : filesToDelete){
           file.delete();
        }
        System.out.println("Deleted "+filesToDelete.size()+"Files ");
    }
}

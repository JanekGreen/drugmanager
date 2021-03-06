package pl.pwojcik.drugmanager.ui.druginfo;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.ui.adddrug.adapter.DefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.DefinedTimesActivity;
import pl.pwojcik.drugmanager.ui.uicomponents.DefinedTimesDialog;
import pl.pwojcik.drugmanager.ui.uicomponents.DialogUtil;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;


public class DrugInfoActivity extends AppCompatActivity implements DefinedTimeAdapter.SwitchChangeCallback, DefinedTimesDialog.OnDialogButtonClickedListener, PopupMenu.OnMenuItemClickListener {

    private static final String AUTHORITY = "pl.pwojcik.drugmanager.fileprovider";

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

    @BindView(R.id.ivExpand)
    ImageView ivExpand;

    @BindView(R.id.rvDefinedTimes)
    RecyclerView rvDefinedTimes;

    @BindView(R.id.remindersArea)
    LinearLayout remindersArea;

    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;

    private DefinedTimeAdapter definedTimeAdapter;
    private DrugViewModel drugViewModel;
    private ActiveSubstanceAdapter activeSubstanceAdapter;
    private String characteristicsUrl;
    private String leafletUrl;
    private ArrayList<File> filesToDelete;
    private boolean contentChanged = false;
    private boolean dialogVisible = false;
    private boolean alreadyShown = false;
    private DefinedTimesDialog definedTimesDialog;
    private String actionOnRequestPermission;

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
        rootLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

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
            definedTimeAdapter.setDrugViewModel(drugViewModel);
            definedTimeAdapter.notifyDataSetChanged();

            if (savedInstanceState != null && !alreadyShown) {
                this.dialogVisible = savedInstanceState.getBoolean("DIALOG_VISIBLE", false);

                if (dialogVisible) {
                    System.out.println("dialog visible!");
                    int hour = savedInstanceState.getInt("DIALOG_HOUR");
                    int minute = savedInstanceState.getInt("DIALOG_MINUTE");
                    String name = savedInstanceState.getString("DEFINED_TIME_NAME");
                    List<Integer> activeDays = savedInstanceState.getIntegerArrayList("ACTIVE_DAYS");
                    definedTimesDialog = new DefinedTimesDialog(this);
                    definedTimesDialog.buildNewDefinedTimeDialog();
                    definedTimesDialog.setOnDialogButtonClicked(this);
                    definedTimesDialog.setHour(hour);
                    definedTimesDialog.setMinute(minute);
                    if (activeDays != null)
                        definedTimesDialog.setActiveDays(activeDays);
                    if (name != null)
                        definedTimesDialog.setDefinedTimeName(name);

                    alreadyShown = true;
                }
            }
        });
        drugViewModel.getSelectedTimesIds().observe(this, selectedIds -> {
            if (selectedIds != null) {
                definedTimeAdapter.setDrugTimes(selectedIds.keySet());
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("DIALOG_VISIBLE", dialogVisible);
        if (definedTimesDialog != null) {
            outState.putInt("DIALOG_HOUR", definedTimesDialog.getHour());
            outState.putInt("DIALOG_MINUTE", definedTimesDialog.getMinute());
            outState.putIntegerArrayList("ACTIVE_DAYS", new ArrayList<>(definedTimesDialog.getActiveDays()));
            outState.putString("DEFINED_TIME_NAME", definedTimesDialog.getDefinedTimeName());
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        drugViewModel.getDefinedTimesData();
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
        if (characteristicsUrl == null || characteristicsUrl.isEmpty()) {
            tvCharacteristics.setCompoundDrawableTintList(ColorStateList.valueOf(Color.GRAY));
        }
        leafletUrl = drugDb.getFeaflet();
        if (leafletUrl == null || leafletUrl.isEmpty()) {
            tvLeaflet.setCompoundDrawableTintList(ColorStateList.valueOf(Color.GRAY));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFiles();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_defined_time:
                definedTimesDialog = new DefinedTimesDialog(this);
                definedTimesDialog.setOnDialogButtonClicked(this);
                definedTimesDialog.buildNewDefinedTimeDialog();
                dialogVisible = true;
                return true;
            case R.id.open_defined_time_activity:
                Intent intent = new Intent(this, DefinedTimesActivity.class);
                startActivity(intent);
                return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!contentChanged) {
                onBackPressed();
            } else {
                DialogUtil dialogUtil = new DialogUtil(this);
                dialogUtil.showYestNoDialog(this, "Uwaga!", "Czy chcesz zapisać zmiany?");
                dialogUtil.setButtonListener(new DialogUtil.DialogUtilButtonListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        saveAndExit();
                    }

                    @Override
                    public void onNegativeButtonClicked() {
                        onBackPressed();
                    }
                });
            }

        } else if (item.getItemId() == R.id.action_save_drug) {
            saveAndExit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndExit() {
        drugViewModel.saveDrugTimeData()
                .subscribe(list -> drugViewModel.updateOrSetAlarms(this)
                                .subscribe(definedTimes -> finishAfterTransition()
                                        , Throwable::printStackTrace)
                        , throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_drug_manual_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCheckedChangedCallback(long definedTimeId, boolean isSelected) {
        System.out.println("checkChanged definedTimeId" + definedTimeId + " isSelected " + isSelected);
        drugViewModel.addSelectedTimeForDrug(definedTimeId, isSelected);
        contentChanged = true;
    }

    @OnClick(R.id.tvCharacteristics)
    public void ontvCharacteristicsClicked() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            actionOnRequestPermission = "CHARACTERISTICS";
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.STORAGE_PERMISSIONS);

        } else {
            System.out.println("Storage already permissions granted");
            handleFileDownload(characteristicsUrl);
        }

    }

    @OnClick(R.id.tvInternetSearch)
    public void ontvInternetSearchClicked() {
        String url = "https://www.google.pl/search?q=" + tvName.getText().toString();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.tvLeaflet)
    public void onTvleafletClicked() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            actionOnRequestPermission = "LEAFLET";
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.STORAGE_PERMISSIONS);

        } else {
            System.out.println("Storage already permissions granted");
            handleFileDownload(leafletUrl);
        }


    }

    private void handleExtras(Bundle extras) {
        if (extras != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                drugViewModel.getSelectedTimesIds(drugId);
                drugViewModel.getDrugDbData(drugId).observe(this, this::initializeView);
                getIntent().removeExtra("DRUG_ID");
            }
        }
    }

    private void handleFileProcessing(File file) {

        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            myIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, AUTHORITY, file);
            myIntent.setDataAndType(contentUri, "application/pdf");
        } else {
            myIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        startActivity(myIntent);
    }

    private void handleFileDownload(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Lek nie posiada pliku", Toast.LENGTH_LONG).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Proszę czekać trwa pobieranie pliku");
        drugViewModel.downloadFileByUrl(url)
                .doOnSubscribe(disposable -> progressDialog.show())
                .subscribe(downloadedFile -> {
                            progressDialog.dismiss();
                            handleFileProcessing(downloadedFile);
                            filesToDelete.add(downloadedFile);
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            throwable.printStackTrace();
                        });
    }


    @OnClick(R.id.ivMenu)
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.show();
    }

    @OnClick(R.id.ivExpand)
    public void showReminders(View view) {
        if (remindersArea.getVisibility() == View.VISIBLE) {
            remindersArea.setVisibility(View.GONE);
            ivExpand.setImageResource(R.drawable.ic_expand_more_black_24dp);
        } else {
            remindersArea.setVisibility(View.VISIBLE);
            ivExpand.setImageResource(R.drawable.ic_expand_less_black_24dp);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (definedTimesDialog != null) {
            definedTimesDialog.dismiss();
        }
    }

    private void removeFiles() {
        for (File file : filesToDelete) {
            file.delete();
        }
        System.out.println("Deleted " + filesToDelete.size() + "Files ");
    }


    @Override
    public void onDialogPositiveButtonClicked(DefinedTime definedTime, List<Integer> activeDays) {

        drugViewModel.saveNewDefinedTimesData(definedTime, activeDays)
                .subscribe(definedTimesDays -> drugViewModel.getDefinedTimesData(),
                        //e -> Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show())
                        Throwable::printStackTrace);
        remindersArea.setVisibility(View.VISIBLE);
        dialogVisible = false;
    }

    @Override
    public void onDialogNegativeButtonClicked() {
        dialogVisible = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("RequestPermissionsResult");
        switch (requestCode) {
            case Constants.STORAGE_PERMISSIONS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Camera permissions granted after dialog response");
                    if (actionOnRequestPermission != null && actionOnRequestPermission.equals("LEAFLET")) {
                        handleFileDownload(leafletUrl);
                    } else if (actionOnRequestPermission != null && actionOnRequestPermission.equals("CHARACTERISTICS")) {
                        handleFileDownload(characteristicsUrl);
                    }

                } else {
                    DialogUtil dialogUtil = new DialogUtil(this);
                    dialogUtil.showInfo(this, "Nie można pobrać pliku nie nadano aplikacji odpowiednich uprawnień");
                    System.out.println("Storage permissions denied");
                }

            }

        }
    }
}
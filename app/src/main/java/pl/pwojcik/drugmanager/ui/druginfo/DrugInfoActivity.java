package pl.pwojcik.drugmanager.ui.druginfo;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimeAdapter;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

public class DrugInfoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.initialLetterIcon)
    MaterialLetterIcon initialLetterIcon;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvDrugNameDetails)
    TextView tvDrugNameDetails;

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

    @BindView(R.id.rvDrugTimes)
    RecyclerView rvDrugTimes;
    private DrugInfoViewModel drugInfoViewModel;
    private NewDefinedTimeAdapter newDefinedTimeAdapter;
    private ActiveSubstanceAdapter activeSubstanceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_info);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        newDefinedTimeAdapter = new NewDefinedTimeAdapter();
        rvDrugTimes.setAdapter(newDefinedTimeAdapter);
        rvDrugTimes.setLayoutManager(new LinearLayoutManager(this));

        rvActiveSubstance.setLayoutManager(new LinearLayoutManager(this));
        activeSubstanceAdapter = new ActiveSubstanceAdapter();
        rvActiveSubstance.setAdapter(activeSubstanceAdapter);

        drugInfoViewModel = ViewModelProviders.of(this).get(DrugInfoViewModel.class);
        drugInfoViewModel.getDrugDbData().observe(this, this::initializeView);
        drugInfoViewModel.getDefinedTimeData().observe(this,definedTimesList ->{
            newDefinedTimeAdapter.setDefinedTimes(definedTimesList);
            newDefinedTimeAdapter.notifyDataSetChanged();
        } );

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            long id = extras.getLong("DRUG_ID");
            drugInfoViewModel.getDrugDbData(id);
            drugInfoViewModel.getDefinedTimeData(id);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeView(DrugDb drugDb) {
        initialLetterIcon.setLetter(drugDb.getName());
        tvProducer.setText(drugDb.getProducer());
        tvDrugNameDetails.setText(drugDb.getUsageType());
        tvName.setText(drugDb.getName());
        activeSubstanceAdapter.setActiveSubstances(Misc.getContentsDataFromDrugDb(drugDb));
        activeSubstanceAdapter.notifyDataSetChanged();
    }
}

package pl.pwojcik.drugmanager.ui.druginfo;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pwojcik.pl.archcomponentstestproject.R;

public class AddDrugManualActivity extends AppCompatActivity {

    @BindView(R.id.etDrugName)
    EditText etDrugName;

    @BindView(R.id.etProducer)
    EditText etProducer;

    @BindView(R.id.etDrugNameDetails)
    EditText etDrugNameDetails;

    @BindView(R.id.etDrugNameDetails2)
    EditText etDrugNameDetails2;
    private DrugViewModel drugViewModel;
    private DrugDb drugDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug_manual);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drugDb = new DrugDb();

        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_drug_manual_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_drug:
                saveDrug();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initialize(){
        etDrugName.setText(drugDb.getName());
        etProducer.setText(drugDb.getProducer());
        etDrugNameDetails.setText(drugDb.getUsageType());
        etDrugNameDetails2.setText(drugDb.getPackQuantity());
    }
    private void saveDrug() {
        DrugDb drugDb = new DrugDb();
        if(!validateFields())
            return;
        drugDb.setName(etDrugName.getText().toString());
        drugDb.setProducer(etProducer.getText().toString());
        drugDb.setUsageType(etDrugNameDetails.getText().toString());
        drugDb.setPackQuantity(etDrugNameDetails2.getText().toString());
        drugViewModel.saveDrug(drugDb)
                .subscribe(drugDb1 -> {
                            Toast.makeText(this,
                                    "Pomyślnie zapisano " + drugDb.getName(), Toast.LENGTH_SHORT).show();
                            finish();
                        },
                        e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateFields() {
        if (etDrugName.getText().toString().isEmpty()) {
            etDrugName.setError("Pole obowiązkowe");
            return false;
        }
        return true;
    }
}

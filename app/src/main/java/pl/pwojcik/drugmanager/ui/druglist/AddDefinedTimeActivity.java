package pl.pwojcik.drugmanager.ui.druglist;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.UUIDUtil;
import pwojcik.pl.archcomponentstestproject.R;

public class AddDefinedTimeActivity extends AppCompatActivity {

    @BindView(R.id.etDefinedTimeName)
    EditText definedTimeName;
    @BindView(R.id.timePicker)
    TimePicker timePicker;
    private DefinedTime definedTime;
    private DrugListViewModel drugListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_defined_time);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        timePicker.setIs24HourView(true);
        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);

        Bundle extras = getIntent().getExtras();
        this.definedTime = null;
        if(extras!=null) {
            int requestCode = extras.getInt("REQUEST_CODE", -1);
            if(requestCode != -1){
                drugListViewModel.getDefinedTimeForRequestCodeAsDefTime(requestCode)
                        .filter(definedTime1 -> definedTime1.getTime()!=null && !definedTime1.getTime().isEmpty())
                        .subscribe(
                                definedTime1 -> {
                                    String parts[] = definedTime1.getTime().split(":");
                                    timePicker.setHour(Integer.valueOf(parts[0]));
                                    timePicker.setMinute(Integer.valueOf(parts[1]));
                                    definedTimeName.setText(definedTime1.getName());
                                    definedTime = definedTime1;
                                }
                        ,e -> Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show());
            }
            }
        if(definedTime == null) {
            definedTime = new DefinedTime();
            definedTime.setRequestCode(UUIDUtil.getUUID(this));
            definedTime.setTime(timePicker.getHour()+":"+timePicker.getMinute());
        }

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            definedTime.setTime(hourOfDay+":"+minute);
        });
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
                save();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save(){
        if(!validateField())
            return;
        definedTime.setName(definedTimeName.getText().toString());
        drugListViewModel.insertDefinedTime(definedTime)
                .subscribe(
                        definedTime1 ->{
                            Toast.makeText(this, "Zapisano "+definedTime1.getTime(), Toast.LENGTH_SHORT).show();
                            finish();
                        },
                        throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateField(){
        if (definedTimeName.getText().toString().isEmpty()){
            definedTimeName.setError("Nazwa jest polem obowiązkowym");
            return  false;
        }
        return true;
    }
}

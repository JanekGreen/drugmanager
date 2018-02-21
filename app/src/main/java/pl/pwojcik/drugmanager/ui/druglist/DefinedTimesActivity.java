package pl.pwojcik.drugmanager.ui.druglist;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.ui.adddrug.adapter.DefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimeAdapter;
import pwojcik.pl.archcomponentstestproject.R;

public class DefinedTimesActivity extends AppCompatActivity implements NewDefinedTimeAdapter.OnNewDefinedTimesAdapterItemClick {


    private DrugViewModel drugViewModel;
    private NewDefinedTimeAdapter definedTimeAdapter;
    private List<DefinedTime> definedTimesGlobal;

    @BindView(R.id.rvDefinedTimes)
    RecyclerView rvDefinedTimes;

    @BindView(R.id.fabAddDefinedTimes)
    FloatingActionButton addDefinedTimes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defined_times);
        ButterKnife.bind(this);
        definedTimeAdapter = new NewDefinedTimeAdapter();
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
        drugViewModel.getDefinedTimesData().observe(this, definedTimes -> {
            definedTimesGlobal = new ArrayList<>(definedTimes);
            definedTimeAdapter.setDefinedTimes(definedTimes);
            definedTimeAdapter.notifyDataSetChanged();
        });
        definedTimeAdapter.setOnNewDefinedTimesAdapterItemClick(this);
        rvDefinedTimes.setAdapter(definedTimeAdapter);
        rvDefinedTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvDefinedTimes.setLayoutManager(new LinearLayoutManager(this));

    }

    @OnClick(R.id.fabAddDefinedTimes)
    void onBtnAddDefinedTimesClicked() {
        DefinedTime definedTime = new DefinedTime();
        buildNewDefinedTimeDialog(definedTime);
    }

    private void buildNewDefinedTimeDialog(DefinedTime definedTime) {

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_defined_times_dialog, null);

        EditText etDefinedTimeName = dialogView.findViewById(R.id.etDefined_time_name);
        etDefinedTimeName.setText(definedTime.getName());
        EditText etDefinedTimeTime = dialogView.findViewById(R.id.etDefined_time_time);
        etDefinedTimeTime.setText(definedTime.getTime());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Dodaj nową porę przyjmowania leku")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog1, which) -> {
                    definedTime.setTime(etDefinedTimeTime.getText().toString());
                    definedTime.setName(etDefinedTimeName.getText().toString());
                    drugViewModel.insertDefinedTime(definedTime)
                            .subscribe(
                                    definedTime1 -> {
                                        definedTimeAdapter.notifyDataSetChanged();
                                        definedTimesGlobal.add(definedTime);
                                        definedTimeAdapter.setDefinedTimes(definedTimesGlobal);
                                        definedTimeAdapter.notifyDataSetChanged();
                                    },
                                    throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());

                })
                .setNegativeButton("Anuluj", (dialog12, which) -> {

                })
                .create();

        dialog.show();
    }

    @Override
    public void onDefinedTimeAdapterItemClick(int position) {
        buildNewDefinedTimeDialog(definedTimesGlobal.get(position));
    }
}

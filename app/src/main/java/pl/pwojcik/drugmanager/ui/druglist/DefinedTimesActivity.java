package pl.pwojcik.drugmanager.ui.druglist;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.ui.adddrug.adapter.DefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapterTouchHelper;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimesAdapterTouchHelper;
import pwojcik.pl.archcomponentstestproject.R;

public class DefinedTimesActivity extends AppCompatActivity implements NewDefinedTimeAdapter.OnNewDefinedTimesAdapterItemClick,
        NewDefinedTimesAdapterTouchHelper.RecyclerItemTouchHelperListener {


    private DrugViewModel drugViewModel;
    private NewDefinedTimeAdapter definedTimeAdapter;
    private List<DefinedTime> definedTimesGlobal;

    @BindView(R.id.rvDefinedTimes)
    RecyclerView rvDefinedTimes;

    @BindView(R.id.fabAddDefinedTimes)
    FloatingActionButton addDefinedTimes;

    @BindView(R.id.definedTimesActivityRoot)
    LinearLayout rootLayout;


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
        rvDefinedTimes.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new NewDefinedTimesAdapterTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(rvDefinedTimes);
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
                                        drugViewModel.getDefinedTimesData();
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NewDefinedTimeAdapter.DefinedTimeViewHolder) {
            NewDefinedTimeAdapter definedTimeAdapter = (NewDefinedTimeAdapter) rvDefinedTimes.getAdapter();
            DefinedTime removedItem = definedTimeAdapter.removeItem(position);
            drugViewModel.removeDefinedTime(removedItem)
                    .subscribe(definedTime -> {
                                Snackbar snackbar = Snackbar
                                        .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
                                snackbar.setAction("COFNIJ!", view -> {
                                    restoreItem(removedItem, position);
                                });
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();
                            },
                            e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                restoreItem(removedItem,position);
                            });

        }
    }

    private void restoreItem(DefinedTime removedItem, int position) {
        definedTimeAdapter.restoreItem(removedItem, position);
        drugViewModel.insertDefinedTime(removedItem)
                .subscribe(definedTime1 -> System.out.println("Przywrócono"),
                        e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    @Override
    protected void onStop() {
        super.onStop();
        drugViewModel.updateOrSetAlarms(this)
                .subscribe(definedTimes -> System.out.println("Alarms have been set " + definedTimes.size()),
                        e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

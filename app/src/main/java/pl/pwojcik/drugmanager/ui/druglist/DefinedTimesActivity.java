package pl.pwojcik.drugmanager.ui.druglist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimesAdapterTouchHelper;
import pl.pwojcik.drugmanager.ui.uicomponents.DefinedTimesDialog;
import pl.pwojcik.drugmanager.ui.uicomponents.DialogUtil;
import pwojcik.pl.archcomponentstestproject.R;

public class DefinedTimesActivity extends AppCompatActivity implements NewDefinedTimeAdapter.OnNewDefinedTimesAdapterItemClick,
        NewDefinedTimesAdapterTouchHelper.RecyclerItemTouchHelperListener, DefinedTimesDialog.OnDialogButtonClickedListener {


    private DrugViewModel drugViewModel;
    private NewDefinedTimeAdapter definedTimeAdapter;
    private List<DefinedTime> definedTimesGlobal;


    @BindView(R.id.rvDefinedTimes)
    RecyclerView rvDefinedTimes;

    @BindView(R.id.fabAddDefinedTimes)
    FloatingActionButton addDefinedTimes;

    @BindView(R.id.definedTimesActivityRoot)
    CoordinatorLayout rootLayout;

    private DefinedTimesDialog definedTimesDialog;
    private int definedTimePosition = -1;
    private boolean dialogVisible = false;
    private boolean alreadyShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defined_times);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        definedTimeAdapter = new NewDefinedTimeAdapter(this);
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
        drugViewModel.getDefinedTimesData().observe(this, definedTimes -> {
            definedTimesGlobal = new ArrayList<>(definedTimes);
            definedTimeAdapter.setDefinedTimes(definedTimes);
            definedTimeAdapter.notifyDataSetChanged();

            if (savedInstanceState != null && !alreadyShown) {
                this.dialogVisible = savedInstanceState.getBoolean("DIALOG_VISIBLE", false);
                this.definedTimePosition = savedInstanceState.getInt("DEFINED_TIME_POS", -1);
                if (dialogVisible) {
                    System.out.println("dialog visible!");
                    DefinedTimesDialog definedTimesDialog = new DefinedTimesDialog(this);
                    definedTimesDialog.setOnDialogButtonClicked(this);
                    if (definedTimePosition != -1 && definedTimesGlobal != null && !definedTimesGlobal.isEmpty()) {
                        definedTimesDialog.buildNewDefinedTimeDialog(definedTimesGlobal.get(definedTimePosition));
                    } else {
                        definedTimesDialog.buildNewDefinedTimeDialog();
                    }

                    alreadyShown = true;
                }
            }
        });

        definedTimeAdapter.setOnNewDefinedTimesAdapterItemClick(this);
        rvDefinedTimes.setAdapter(definedTimeAdapter);
        rvDefinedTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvDefinedTimes.setLayoutManager(new LinearLayoutManager(this));
        rvDefinedTimes.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new NewDefinedTimesAdapterTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(rvDefinedTimes);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("DIALOG_VISIBLE", dialogVisible);
        outState.putInt("DEFINED_TIME_POS", definedTimePosition);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @OnClick(R.id.fabAddDefinedTimes)
    void onBtnAddDefinedTimesClicked() {
        definedTimesDialog = new DefinedTimesDialog(this);
        definedTimesDialog.setOnDialogButtonClicked(this);
        definedTimesDialog.buildNewDefinedTimeDialog();
        dialogVisible = true;
    }


    @Override
    public void onDefinedTimeAdapterItemClick(int position) {
        definedTimesDialog = new DefinedTimesDialog(this);
        definedTimesDialog.setOnDialogButtonClicked(this);
        definedTimesDialog.buildNewDefinedTimeDialog(definedTimesGlobal.get(position));
        dialogVisible = true;
        this.definedTimePosition = position;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NewDefinedTimeAdapter.DefinedTimeViewHolder) {
            NewDefinedTimeAdapter definedTimeAdapter = (NewDefinedTimeAdapter) rvDefinedTimes.getAdapter();
            DefinedTime removedItem = definedTimeAdapter.removeItem(position);

            drugViewModel.getDefinedTimesDays(removedItem.getId())
                    .subscribe(definedTimesDays -> {
                                drugViewModel.removeDefinedTime(removedItem, definedTimesDays)
                                        .subscribe(definedTime -> {
                                                    Snackbar snackbar = Snackbar
                                                            .make(rootLayout, removedItem.getName() + " został usunięty!", Snackbar.LENGTH_LONG);
                                                    snackbar.setAction("COFNIJ!", view -> {
                                                        restoreItem(removedItem, position, definedTimesDays);
                                                    });
                                                    snackbar.setActionTextColor(Color.YELLOW);
                                                    snackbar.show();
                                                },
                                                e -> {
                                                    DialogUtil dialogUtil = new DialogUtil(this);
                                                    dialogUtil.showInfo(this, handleError(e));
                                                    //Toast.makeText(this, handleError(e), Toast.LENGTH_SHORT).show();
                                                    restoreItem(removedItem, position, definedTimesDays);
                                                });
                            },
                            Throwable::printStackTrace);
        }
    }

    private void restoreItem(DefinedTime removedItem, int position, List<DefinedTimesDays> definedTimesDays) {
        definedTimeAdapter.restoreItem(removedItem, position);
        drugViewModel.insertDefinedTime(removedItem, definedTimesDays)
                .subscribe(definedTime1 -> {
                            System.out.println("Przywrócono");
                            //drugViewModel.in
                        },
                        e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(definedTimesDialog!=null){
            definedTimesDialog.dismiss();
        }
        drugViewModel.updateOrSetAlarms(this)
                .subscribe(definedTimes -> System.out.println("Alarms have been set " + definedTimes.size()),
                        e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String handleError(Throwable t) {
        if (t instanceof android.database.sqlite.SQLiteConstraintException) {
            return "Nie można usunąć. W bazie występują leki przypisane do tej pory";
        }
        return t.getMessage();
    }


    @Override
    public void onDialogPositiveButtonClicked(DefinedTime definedTime, List<Integer> activeDays) {
        definedTimePosition = -1;
        dialogVisible = false;
        drugViewModel.saveNewDefinedTimesData(definedTime, activeDays)
                .subscribe(definedTimesDays -> drugViewModel.getDefinedTimesData(),
                        //e -> Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show())
                        Throwable::printStackTrace);
    }

    @Override
    public void onDialogNegativeButtonClicked() {
        definedTimePosition = -1;
        dialogVisible = false;
    }
}


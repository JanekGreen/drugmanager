package pl.pwojcik.drugmanager.ui.druglist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
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
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimeAdapter;
import pl.pwojcik.drugmanager.ui.druglist.adapter.NewDefinedTimesAdapterTouchHelper;
import pl.pwojcik.drugmanager.ui.uicomponents.DefinedTimesDialog;
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
    LinearLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defined_times);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
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
         Intent intent = new Intent(this,AddDefinedTimeActivity.class);
         startActivity(intent);
    }


    @Override
    public void onDefinedTimeAdapterItemClick(int position) {
        int requestCode = definedTimesGlobal.get(position).getRequestCode();
        Intent intent = new Intent(this,AddDefinedTimeActivity.class);
        intent.putExtra("REQUEST_CODE",requestCode);
        startActivity(intent);

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
                                Toast.makeText(this, handleError(e), Toast.LENGTH_SHORT).show();
                                restoreItem(removedItem, position);
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

    private String handleError(Throwable t) {
        if (t instanceof android.database.sqlite.SQLiteConstraintException) {
            return "W bazie występują leki przypisane do tej pory, usuń je najpierw";
        }
        return t.getMessage();
    }


    @Override
    public void onDialogPositiveButtonClicked() {
        System.out.println("Listener invoked");
        drugViewModel.getDefinedTimesData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        drugViewModel.getDefinedTimesData();
    }

    @Override
    public void onDialogNegativeButtonClicked() {

    }
}

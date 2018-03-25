package pl.pwojcik.drugmanager.ui.druglist;

import android.animation.TimeInterpolator;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.notification.service.RingtonePlayingService;
import pl.pwojcik.drugmanager.ui.druglist.adapter.DrugListAdapter;
import pl.pwojcik.drugmanager.ui.druglist.viewmodel.DrugListViewModel;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.rvDrugList)
    RecyclerView rvDrugList;
    DrugListAdapter drugListAdapter;
    @BindView(R.id.drugCount)
    TextView tvDrugCount;
    @BindView(R.id.timeName)
    TextView tvTimeName;

    private DrugListViewModel drugListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        drugListViewModel = ViewModelProviders.of(this).get(DrugListViewModel.class);
        rvDrugList.setLayoutManager(new LinearLayoutManager(this));
        rvDrugList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Bundle extras = getIntent().getExtras();
        System.out.println("on Start entered");
        if (extras != null) {

/*            Intent ringtonePlayingIntent = new Intent(this, RingtonePlayingService.class);
            stopService(ringtonePlayingIntent);*/

            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(Constants.INTENT_REQUEST_CODE);


            int requestCode = extras.getInt("REQUEST_CODE", -1);
            System.out.print("Request code" + requestCode);
            if (requestCode != -1) {
                drugListViewModel.getDefinedTimeForRequestCode(requestCode)
                        .filter(list -> list.size() > 0)
                        .map(list -> list.get(0))
                        .defaultIfEmpty("")
                        .map(fullString -> fullString.substring(0, fullString.indexOf(" ")))
                        .doOnSuccess(timeName -> {
                            tvTimeName.setText(timeName);
                            drugListViewModel.getDrugsForTime(timeName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(drugDbs -> {
                                        drugListAdapter = new DrugListAdapter(drugDbs);
                                        rvDrugList.setAdapter(drugListAdapter);
                                        tvDrugCount.setText("Liczba leków do wzięcia: " + drugDbs.size());
                                    });
                        })
                        .subscribe(System.out::println,
                                e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                                        .show());

/*                Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                ringtone.play();*/

            }
        }
    }

}

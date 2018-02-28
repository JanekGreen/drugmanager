package pl.pwojcik.drugmanager.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;

/**
 * Created by pawel on 28.02.18.
 */

public class AlarmStarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        DefinedTimeDao dt = DrugmanagerApplication.getDbInstance(context).getDefinedTimesDao();
        dt.getDefinedTimesForActiveDrugs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(list -> System.out.println("DRUGMANAGER:: Alarms have been set, count: "+list.size()))
                .subscribe(definedTimes -> {
                            AlarmHelper alarmHelper = new AlarmHelper(context);
                            alarmHelper.setOrUpdateAlarms(definedTimes);
                        },
                        e -> System.err.println("error" + e.getMessage()));

    }
}

package pl.pwojcik.drugmanager.ui.uicomponents;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDaysDao;
import pl.pwojcik.drugmanager.utils.UUIDUtil;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 05.03.18.
 */

public class DefinedTimesDialog implements DayPicker.DaySelectionChangedListener {

    private Activity activity;
    private OnDialogButtonClickedListener onDialogButtonClicked;
    private List<Integer> activeDays;
    private AlertDialog dialog;


    public interface OnDialogButtonClickedListener {
        void onDialogPositiveButtonClicked(DefinedTime definedTime, List<Integer> activeDays);

        void onDialogNegativeButtonClicked();
    }

    public DefinedTimesDialog(Activity activity) {
        this.activity = activity;
    }

    public void buildNewDefinedTimeDialog() {
        DefinedTime definedTime = new DefinedTime();
        definedTime.setRequestCode(UUIDUtil.getUUID(activity));
        buildNewDefinedTimeDialog(definedTime);
    }

    public void setOnDialogButtonClicked(OnDialogButtonClickedListener onDialogButtonClicked) {
        this.onDialogButtonClicked = onDialogButtonClicked;
    }

    public void buildNewDefinedTimeDialog(DefinedTime definedTime) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_defined_time, null);

        DefinedTimesDaysDao definedTimesDaysDao = DrugmanagerApplication.getDbInstance(activity).getDefinedTimesDaysDao();
        DefinedTimeDao definedTimeDao = DrugmanagerApplication.getDbInstance(activity).getDefinedTimesDao();

        EditText etDefinedTimeName = dialogView.findViewById(R.id.etDefinedTimeName);
        etDefinedTimeName.setText(definedTime.getName());

        CustomTimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        DayPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
        dayPicker.setDaySelectionChangedListener(this);

        if (definedTime.getId() > 0) {
            definedTimesDaysDao.getDefinedTimeDaysForDefinedTime(definedTime.getId())
                    .subscribeOn(Schedulers.io())
                    .flatMap(definedTimesDays -> io.reactivex.Observable.fromIterable(definedTimesDays)
                            .map(DefinedTimesDays::getDay)
                            .toList()
                            .toMaybe())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(activeDays_ -> {
                        this.activeDays = activeDays_;
                        dayPicker.setActiveDays(activeDays);
                    });
        } else {
            this.activeDays = dayPicker.getActiveDaysList();
        }

        String time = definedTime.getTime();
        if (time != null && !time.isEmpty()) {

            String[] parts = time.split(":");
            if (Build.VERSION.SDK_INT >= 23) {
                timePicker.setHour(Integer.valueOf(parts[0]));
                timePicker.setMinute(Integer.valueOf(parts[1]));
            } else {
                timePicker.setCurrentHour(Integer.valueOf(parts[0]));
                timePicker.setCurrentMinute(Integer.valueOf(parts[1]));
            }

        }

         dialog = new AlertDialog.Builder(activity)
                .setTitle(null)
                .setView(dialogView)
                .setPositiveButton("OK", (dialog1, which) -> {
                })
                .setNegativeButton("Anuluj", (dialog12, which) -> {
                    System.out.println("anuluj!");
                    onDialogButtonClicked.onDialogNegativeButtonClicked();
                })
                .create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (activeDays.size() == 0) {
                Toast.makeText(activity, "Należy zaznaczyć chociaż jeden dzień", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etDefinedTimeName.getText().toString().isEmpty()) {
                Toast.makeText(activity, "Należy podać nazwę pory przyjmowania leku", Toast.LENGTH_SHORT).show();
                return;
            }

            definedTimeDao.getDefinedTimeIdForName(etDefinedTimeName.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .switchIfEmpty(Maybe.just(-1L))
                    .subscribe(id -> {
                        System.out.println("id " + id + " definedtime id " + definedTime.getId());
                        if (id != -1 && id!=definedTime.getId()) {
                            Toast.makeText(activity, "Nazwa powinna być unikalna", Toast.LENGTH_SHORT).show();
                        } else {
                            saveDefinedTime(definedTime, etDefinedTimeName, timePicker, dialog);
                        }
                    });

        });
    }

    public void dismiss(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void saveDefinedTime(DefinedTime definedTime, EditText etDefinedTimeName, CustomTimePicker timePicker, AlertDialog dialog) {
        if (Build.VERSION.SDK_INT >= 23) {
            definedTime.setTime(String.format(Locale.getDefault(), "%02d", timePicker.getHour()) + ":" + String.format(Locale.getDefault(), "%02d", timePicker.getMinute()));
        } else {
            definedTime.setTime(String.format(Locale.getDefault(), "%02d", timePicker.getCurrentHour()) + ":" + String.format(Locale.getDefault(), "%02d", timePicker.getCurrentMinute()));
        }
        definedTime.setName(etDefinedTimeName.getText().toString());

        onDialogButtonClicked.onDialogPositiveButtonClicked(definedTime, activeDays);
        dialog.dismiss();
    }



    @Override
    public void onDaySelectionChanged(List<Integer> activeDays, List<String> selectedDaysNames) {
        this.activeDays = activeDays;
    }

}


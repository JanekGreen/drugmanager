package pl.pwojcik.drugmanager.ui.uicomponents;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.utils.Misc;
import pl.pwojcik.drugmanager.utils.UUIDUtil;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 05.03.18.
 */

public class DefinedTimesDialog {

    private Activity activity;
    private OnDialogButtonClickedListener onDialogButtonClicked;

    public interface OnDialogButtonClickedListener {
        void onDialogPositiveButtonClicked();
        void onDialogNegativeButtonClicked();
    }

    public DefinedTimesDialog(Activity activity) {
        this.activity = activity;
    }

    public void buildNewDefinedTimeDialog(){
        DefinedTime definedTime = new DefinedTime();
        definedTime.setRequestCode(UUIDUtil.getUUID(activity));
        buildNewDefinedTimeDialog(definedTime);
    }

    public void setOnDialogButtonClicked(OnDialogButtonClickedListener onDialogButtonClicked) {
        this.onDialogButtonClicked = onDialogButtonClicked;
    }

    public void buildNewDefinedTimeDialog(DefinedTime definedTime) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_add_defined_time, null);

        EditText etDefinedTimeName = dialogView.findViewById(R.id.etDefinedTimeName);
        etDefinedTimeName.setText(definedTime.getName());

        CustomTimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        String time = definedTime.getTime();
        if (time != null && !time.isEmpty()) {

            String[] parts = time.split(":");
            timePicker.setHour(Integer.valueOf(parts[0]));
            timePicker.setMinute(Integer.valueOf(parts[1]));
        }

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(null)
                .setView(dialogView)
                .setPositiveButton("OK", (dialog1, which) -> {

                })
                .setNegativeButton("Anuluj", (dialog12, which) -> {

                })
                .create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            DefinedTimeDao definedTimeDao = DrugmanagerApplication.getDbInstance(activity).getDefinedTimesDao();

                definedTime.setTime(String.format(Locale.getDefault(),"%02d", timePicker.getHour()) + ":" + String.format(Locale.getDefault(),"%02d", timePicker.getMinute()));
                definedTime.setName(etDefinedTimeName.getText().toString());
                //definedTimeDao.insertDefinedTime(definedTime)
                Observable.just(definedTime)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                definedTime1 -> {
                                    definedTimeDao.insertDefinedTime(definedTime1);
                                    onDialogButtonClicked.onDialogPositiveButtonClicked();
                                },
                                throwable -> Toast.makeText(activity, throwable.getMessage(), Toast.LENGTH_SHORT).show());
                dialog.dismiss();
            });
        }

    }

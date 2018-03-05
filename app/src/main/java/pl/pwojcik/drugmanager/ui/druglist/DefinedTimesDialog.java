package pl.pwojcik.drugmanager.ui.druglist;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        View dialogView = inflater.inflate(R.layout.add_defined_times_dialog, null);

        EditText etDefinedTimeName = dialogView.findViewById(R.id.etDefined_time_name);
        etDefinedTimeName.setText(definedTime.getName());
        EditText etDefinedTimeHour = dialogView.findViewById(R.id.etDefined_time_hour);
        EditText etDefinedTimeMinute = dialogView.findViewById(R.id.etDefined_time_minute);
        String time = definedTime.getTime();

        if (time != null && !time.isEmpty()) {

            String[] parts = time.split(":");
            etDefinedTimeHour.setText(parts[0]);
            etDefinedTimeMinute.setText(parts[1]);
        }

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Dodaj nową porę przyjmowania leku")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog1, which) -> {

                })
                .setNegativeButton("Anuluj", (dialog12, which) -> {

                })
                .create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            DefinedTimeDao definedTimeDao = DrugmanagerApplication.getDbInstance(activity).getDefinedTimesDao();
            if (Misc.parseTimeInput(etDefinedTimeHour.getText().toString(),
                    etDefinedTimeMinute.getText().toString())) {

                definedTime.setTime(etDefinedTimeHour.getText().toString() + ":" + etDefinedTimeMinute.getText().toString());
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
            } else {
                Toast.makeText(activity, "Niepoprawny format godziny",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

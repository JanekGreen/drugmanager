package pl.pwojcik.drugmanager.ui.uicomponents;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by pawel on 06.03.18.
 */

public class DialogUtil {

    public interface DialogUtilButtonListener{

        void onPositiveButtonClicked();
        void onNegativeButtonClicked();

    }

    public void setButtonListener(DialogUtilButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    private DialogUtilButtonListener buttonListener;

    public DialogUtil(Fragment fragment) {
        if(fragment instanceof DialogUtilButtonListener)
            buttonListener = (DialogUtilButtonListener)fragment;

    }
    public DialogUtil(Activity activity) {
        if(activity instanceof DialogUtilButtonListener)
            buttonListener = (DialogUtilButtonListener)activity;
    }

    public void showInfo(Context context, String text) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Uwaga!")
                .setMessage(text)
                .setPositiveButton("OK", (dialog1, which) -> {
                    dialog1.dismiss();
                    if(buttonListener!=null) {
                        buttonListener.onPositiveButtonClicked();
                    }
                })
                .create();

        dialog.show();
    }
    public void showYestNoDialog(Context context,String title, String message) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Tak", (dialog1, which) -> {
                    dialog1.dismiss();
                    buttonListener.onPositiveButtonClicked();
                })
                .setNegativeButton("Nie", (dialog12, which) -> {
                    dialog12.dismiss();
                    buttonListener.onNegativeButtonClicked();
                })
                .create();

        dialog.show();

    }
}

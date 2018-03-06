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


    private Fragment fragment;
    private DialogUtilButtonListener buttonListener;

    public DialogUtil(Fragment fragment) {
        this.fragment = fragment;
        if(fragment instanceof DialogUtilButtonListener)
            buttonListener = (DialogUtilButtonListener)fragment;
        else{
            throw new  IllegalArgumentException("Aktywność nie implementuje interfejsu DialogUtilButtonListener");
        }

    }

    public void showInfo(String text) {

        AlertDialog dialog = new AlertDialog.Builder(fragment.getContext())
                .setTitle(text)
                .setPositiveButton("OK", (dialog1, which) -> {
                    dialog1.dismiss();
                    buttonListener.onPositiveButtonClicked();
                })
                .create();

        dialog.show();

    }
}

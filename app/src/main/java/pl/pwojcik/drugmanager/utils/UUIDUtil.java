package pl.pwojcik.drugmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pawel on 22.02.18.
 */

public class UUIDUtil {
    public static int getUUID(Context context){

        SharedPreferences sp = context.getSharedPreferences("UUID",Context.MODE_PRIVATE);
        int retValue = sp.getInt("uuid",1);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("uuid",retValue+1);
        editor.commit();

        return retValue;
    }
}

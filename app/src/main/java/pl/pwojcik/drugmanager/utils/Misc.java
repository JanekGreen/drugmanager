package pl.pwojcik.drugmanager.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import okhttp3.ResponseBody;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import retrofit2.Response;

/**
 * Created by pawel on 22.02.18.
 */

public class Misc {

    public static boolean parseTimeInput(String hour, String minute) {
        //asserting positive value
        if (hour == null || hour.isEmpty() || minute == null || minute.isEmpty()) {
            return false;
        }

        int hour_ = Integer.valueOf(hour);
        int minute_ = Integer.valueOf(minute);

        return hour_ <= 24 && minute_ <= 59;
    }

    public static ArrayList<String> getContentsDataFromDrugDb(DrugDb drugDb) {
        ArrayList<String> result = new ArrayList<>();
        String activeSubstances[] = drugDb.getActiveSubstance().split(",");
        result.addAll(Arrays.asList(activeSubstances));
        return result;
    }

    public static Drug getSpecificContainterInfo(Drug drug, String ean) {

        String result = null;
        String[] packVariants = drug.getPackQuantity().replaceAll("\\n", "").split(";");

        for (String packVariant : packVariants) {
            if (packVariant.contains(ean)) {
                result = packVariant;
                drug.setCommonName(result);
                break;
            }
        }

        if (result != null) {
            int index = result.indexOf("590");
            if (index != -1) {
                drug.setPackQuantity(result.substring(0, index - 1).replaceAll(",", ""));
            }
        }
        return drug;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static File downloadFile(Response<ResponseBody> response) {
        ResponseBody responseBody = response.body();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        File file = null;
        try {

            file = new File(Environment.getExternalStorageDirectory()+"/"+UUID.randomUUID().toString());
            inputStream = responseBody.byteStream();

            // write the inputStream to a FileOutputStream
            outputStream =
                    new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            System.out.println("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
}

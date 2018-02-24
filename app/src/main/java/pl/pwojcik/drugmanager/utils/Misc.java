package pl.pwojcik.drugmanager.utils;

import java.util.ArrayList;
import java.util.Arrays;

import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.restEntity.Drug;

/**
 * Created by pawel on 22.02.18.
 */

public class Misc {

    public static boolean parseTimeInput(String hour, String minute) {
        //asserting positive value
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
                break;
            }
        }

        if(result!=null){
            int index = result.indexOf("590");
            if(index != -1){
                drug.setPackQuantity(result.substring(0,index-1).replaceAll(",",""));
            }
        }
        return drug;
    }
}

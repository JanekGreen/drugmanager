package pl.pwojcik.drugmanager.utils;

import java.util.ArrayList;

import pl.pwojcik.drugmanager.model.persistence.DrugDb;

/**
 * Created by pawel on 22.02.18.
 */

public class Misc {

    public static boolean parseTimeInput(String hour, String minute){
        //asserting positive value
        int hour_ =Integer.valueOf(hour);
        int minute_ = Integer.valueOf(minute);

        return hour_<= 24 && minute_<=59;
    }

    public static ArrayList<String> getContentsDataFromDrugDb(DrugDb drugDb){
        ArrayList<String> result = new ArrayList<>();
        String activeSubstances[] = drugDb.getActiveSubstance().split(",");
        String dosages[] = drugDb.getDosage().split("\\+");
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<activeSubstances.length;i++){
            if(dosages.length>i){
                stringBuilder.append(activeSubstances[i]).append(" ").append(dosages[i]);
                result.add(stringBuilder.toString());
            }else{
                result.add(activeSubstances[i]);
            }

        }
        return result;
    }
}

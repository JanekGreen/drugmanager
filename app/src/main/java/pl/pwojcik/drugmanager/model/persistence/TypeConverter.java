package pl.pwojcik.drugmanager.model.persistence;

import pl.pwojcik.drugmanager.model.restEntity.Drug;

/**
 * Created by pawel on 15.02.18.
 */

public class TypeConverter {

    public static DrugDb makeDrugDatabaseEntity(Drug drug){
        DrugDb drugDb = new DrugDb();
        drugDb.setActiveSubstance(drug.getActiveSubstance());
        drugDb.setCharacteristics(drug.getCharacteristics());
        drugDb.setDosage(drug.getDosage());
        drugDb.setFeaflet(drug.getFeaflet());
        drugDb.setPackQuantity(drug.getPackQuantity());
        drugDb.setProducer(drug.getProducer());
        drugDb.setUsageType(drug.getUsageType());
        drugDb.setName(drug.getName());
        return drugDb;
    }

    public static Drug makeDrugFromDatabaseEntity(DrugDb drugDb){
        Drug drug = new Drug();
        drug.setActiveSubstance(drugDb.getActiveSubstance());
        drug.setCharacteristics(drugDb.getCharacteristics());
        drug.setDosage(drugDb.getDosage());
        drug.setFeaflet(drugDb.getFeaflet());
        drug.setPackQuantity(drugDb.getPackQuantity());
        drug.setProducer(drugDb.getProducer());
        drug.setUsageType(drugDb.getUsageType());
        drug.setName(drugDb.getName());
        return drug;
    }
}

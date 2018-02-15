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
        drugDb.setName(drug.getName());
        return drugDb;
    }
}

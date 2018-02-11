package pl.pwojcik.drugmanager.model.restEntity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Drug {

@SerializedName("name")
@Expose
private String name;
@SerializedName("commonName")
@Expose
private String commonName;
@SerializedName("dosage")
@Expose
private String dosage;
@SerializedName("producer")
@Expose
private String producer;
@SerializedName("packQuantity")
@Expose
private String packQuantity;
@SerializedName("activeSubstance")
@Expose
private String activeSubstance;
@SerializedName("feaflet")
@Expose
private String feaflet;
@SerializedName("characteristics")
@Expose
private String characteristics;
@SerializedName("usageType")
@Expose
private String usageType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getPackQuantity() {
        return packQuantity;
    }

    public void setPackQuantity(String packQuantity) {
        this.packQuantity = packQuantity;
    }

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public String getFeaflet() {
        return feaflet;
    }

    public void setFeaflet(String feaflet) {
        this.feaflet = feaflet;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }
}
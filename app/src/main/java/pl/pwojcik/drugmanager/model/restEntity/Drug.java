package pl.pwojcik.drugmanager.model.restEntity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Drug implements Parcelable{

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

    protected Drug(Parcel in) {
        name = in.readString();
        commonName = in.readString();
        dosage = in.readString();
        producer = in.readString();
        packQuantity = in.readString();
        activeSubstance = in.readString();
        feaflet = in.readString();
        characteristics = in.readString();
        usageType = in.readString();
    }

    public Drug() {
    }

    public static final Creator<Drug> CREATOR = new Creator<Drug>() {
        @Override
        public Drug createFromParcel(Parcel in) {
            return new Drug(in);
        }

        @Override
        public Drug[] newArray(int size) {
            return new Drug[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(commonName);
        dest.writeString(dosage);
        dest.writeString(producer);
        dest.writeString(packQuantity);
        dest.writeString(activeSubstance);
        dest.writeString(feaflet);
        dest.writeString(characteristics);
        dest.writeString(usageType);
    }
}
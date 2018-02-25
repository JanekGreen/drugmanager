package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "drugs")
public class DrugDb implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "dosage")
    private String dosage;
    @ColumnInfo(name = "producer")
    private String producer;
    @ColumnInfo(name = "pack_quantity")
    private String packQuantity;
    @ColumnInfo(name = "active_substance")
    private String activeSubstance;
    @ColumnInfo(name = "leaflet")
    private String feaflet;
    @ColumnInfo(name = "characteristics")
    private String characteristics;
    @ColumnInfo(name = "usage_type")
    private String usageType;
    @ColumnInfo(name = "note")
    private String note;

    public DrugDb() {
    }

    protected DrugDb(Parcel in) {
        id = in.readLong();
        name = in.readString();
        dosage = in.readString();
        producer = in.readString();
        packQuantity = in.readString();
        activeSubstance = in.readString();
        feaflet = in.readString();
        characteristics = in.readString();
        usageType = in.readString();
        note = in.readString();
    }


    public static final Creator<DrugDb> CREATOR = new Creator<DrugDb>() {
        @Override
        public DrugDb createFromParcel(Parcel in) {
            return new DrugDb(in);
        }

        @Override
        public DrugDb[] newArray(int size) {
            return new DrugDb[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return name+" "+producer+" "+usageType+" "+dosage+" "+packQuantity+" "+activeSubstance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(dosage);
        dest.writeString(producer);
        dest.writeString(packQuantity);
        dest.writeString(activeSubstance);
        dest.writeString(feaflet);
        dest.writeString(characteristics);
        dest.writeString(usageType);
        dest.writeString(note);
    }
}
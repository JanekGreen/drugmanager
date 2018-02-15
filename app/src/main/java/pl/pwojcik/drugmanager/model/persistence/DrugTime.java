package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by pawel on 15.02.18.
 */

@Entity( indices ={@Index(value = "drug_id"),@Index(value = "time_id")}, tableName = "drug_time" , foreignKeys = {
        @ForeignKey(entity = DefinedTime.class, parentColumns = "id", childColumns = "time_id"),
        @ForeignKey(entity = DrugDb.class, parentColumns = "id", childColumns = "drug_id")
})

public class DrugTime {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "drug_id")
    private int drugId;
    @ColumnInfo(name = "time_id")
    private int time_id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDrugId() {
        return drugId;
    }

    public void setDrugId(int drugId) {
        this.drugId = drugId;
    }

    public int getTime_id() {
        return time_id;
    }

    public void setTime_id(int time_id) {
        this.time_id = time_id;
    }
}

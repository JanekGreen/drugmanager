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
    private long drugId;
    @ColumnInfo(name = "time_id")
    private long time_id;

    public DrugTime(long drugId, long time_id) {
        this.drugId = drugId;
        this.time_id = time_id;
    }
    public DrugTime(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDrugId() {
        return drugId;
    }

    public void setDrugId(long drugId) {
        this.drugId = drugId;
    }

    public long getTime_id() {
        return time_id;
    }

    public void setTime_id(long time_id) {
        this.time_id = time_id;
    }
}

package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by pawel on 12.03.18.
 */

@Entity( indices ={@Index(value = "time_id")}, tableName = "defined_time_days" , foreignKeys = {
        @ForeignKey(entity = DefinedTime.class, parentColumns = "id", childColumns = "time_id")})

public class DefinedTimesDays {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "time_id")
    private long definedTimeId;

    @ColumnInfo(name = "day")
    private int day;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDefinedTimeId() {
        return definedTimeId;
    }

    public void setDefinedTimeId(long definedTimeId) {
        this.definedTimeId = definedTimeId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}

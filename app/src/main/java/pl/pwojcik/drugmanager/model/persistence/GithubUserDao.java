package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by pawel on 31.01.18.
 */
@Dao
public interface GithubUserDao {

    @Query("SELECT * from GithubUserDb")
    List<GithubUserDb> getAll();

    @Query("SELECT * from GithubUserDb where login=:login")
    Maybe<GithubUserDb> getUserByLogin(String login);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(GithubUserDb user);

    @Delete
    void  deleteUser(GithubUserDb user);

    @Query("DELETE FROM GithubUserDb")
    void  deleteAll();
}

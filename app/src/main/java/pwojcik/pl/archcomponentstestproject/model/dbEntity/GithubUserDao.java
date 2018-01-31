package pwojcik.pl.archcomponentstestproject.model.dbEntity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import pwojcik.pl.archcomponentstestproject.model.restObject.GithubUser;

/**
 * Created by pawel on 31.01.18.
 */
@Dao
public interface GithubUserDao {

    @Query("SELECT * from GithubUserDb")
    List<GithubUserDb> getAll();

    @Query("SELECT * from GithubUserDb where login=:login")
    GithubUserDb getUserByLogin(String login);

    @Insert
    void addUser(GithubUserDb user);

    @Delete
    void  deleteUser(GithubUserDb user);
}

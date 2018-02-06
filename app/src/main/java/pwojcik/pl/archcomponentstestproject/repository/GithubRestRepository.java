package pwojcik.pl.archcomponentstestproject.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import pwojcik.pl.archcomponentstestproject.model.restEntity.GithubUser;

/**
 * Created by wojci on 29.01.2018.
 */

public interface GithubRestRepository {
    LiveData<GithubUser> getUser(String user);
    LiveData<GithubUser> getData();
    void deleteAll();
}

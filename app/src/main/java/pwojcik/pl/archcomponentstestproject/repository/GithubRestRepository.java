package pwojcik.pl.archcomponentstestproject.repository;

import android.arch.lifecycle.LiveData;

import pwojcik.pl.archcomponentstestproject.model.restObject.GitHubUser;

/**
 * Created by wojci on 29.01.2018.
 */

public interface GithubRestRepository {
    LiveData<GitHubUser> getUser(String user);
    LiveData<GitHubUser> getData();
}

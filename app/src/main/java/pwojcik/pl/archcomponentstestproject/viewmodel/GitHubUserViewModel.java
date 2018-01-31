package pwojcik.pl.archcomponentstestproject.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import pwojcik.pl.archcomponentstestproject.model.restObject.GitHubUser;
import pwojcik.pl.archcomponentstestproject.model.retrofit.GitHubRestService;
import pwojcik.pl.archcomponentstestproject.repository.GitHubRestRepositoryImpl;
import pwojcik.pl.archcomponentstestproject.repository.GithubRestRepository;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubUserViewModel extends ViewModel {
    private GithubRestRepository githubRestRepository;

    public GitHubUserViewModel() {
        githubRestRepository = new GitHubRestRepositoryImpl(GitHubRestService.getGithubService());
    }

    public LiveData<GitHubUser> loadUser(String user) {

         return githubRestRepository.getUser(user);

    }

    public LiveData<GitHubUser> getData() {
        return githubRestRepository.getData();
    }
}

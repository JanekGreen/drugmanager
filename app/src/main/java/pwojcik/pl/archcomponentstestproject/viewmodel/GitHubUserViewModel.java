package pwojcik.pl.archcomponentstestproject.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import pwojcik.pl.archcomponentstestproject.TemplateApplication;
import pwojcik.pl.archcomponentstestproject.model.dbEntity.AppDatabase;
import pwojcik.pl.archcomponentstestproject.model.restObject.GithubUser;
import pwojcik.pl.archcomponentstestproject.model.retrofit.GitHubRestService;
import pwojcik.pl.archcomponentstestproject.repository.GitHubRestRepositoryImpl;
import pwojcik.pl.archcomponentstestproject.repository.GithubRestRepository;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubUserViewModel extends AndroidViewModel {
    private GithubRestRepository githubRestRepository;

    public GitHubUserViewModel(@NonNull Application application) {
        super(application);
        githubRestRepository = new GitHubRestRepositoryImpl(GitHubRestService.getGithubService(),
                TemplateApplication.getInstance(application.getApplicationContext()).GithubUserDbDao(),
                TemplateApplication.getExecutor());
    }


    public LiveData<GithubUser> loadUser(String user) {

         return githubRestRepository.getUser(user);

    }

    public LiveData<GithubUser> getData() {
        return githubRestRepository.getData();
    }
}

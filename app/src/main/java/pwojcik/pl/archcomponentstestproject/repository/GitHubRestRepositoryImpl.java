package pwojcik.pl.archcomponentstestproject.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pwojcik.pl.archcomponentstestproject.Utils.DisposableManager;
import pwojcik.pl.archcomponentstestproject.model.TypesConverter;
import pwojcik.pl.archcomponentstestproject.model.persistence.GithubUserDao;
import pwojcik.pl.archcomponentstestproject.model.persistence.GithubUserDb;
import pwojcik.pl.archcomponentstestproject.model.restEntity.GithubUser;
import pwojcik.pl.archcomponentstestproject.model.retrofit.GithubRestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubRestRepositoryImpl implements GithubRestRepository {
    private GithubRestInterface githubRestInterface;
    private MutableLiveData<GithubUser> data;
    private GithubUserDao githubUserDao;


    public GitHubRestRepositoryImpl(GithubRestInterface githubRestInterface, GithubUserDao githubUserDao) {
        this.githubRestInterface = githubRestInterface;
        this.githubUserDao = githubUserDao;
        data = new MutableLiveData<>();
    }

    @Override
    public LiveData<GithubUser> getUser(final String user) {

                //database

                DisposableManager.getInstance().add(githubUserDao.getUserByLogin(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(TypesConverter::makeGithubUser)
                .subscribe(githubUser -> data.setValue(githubUser)));

                //rest
                DisposableManager.getInstance().add(githubRestInterface.getUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(githubUser -> data.setValue(githubUser)));


        return data;
    }

    @Override
    public LiveData<GithubUser> getData() {
        return data;
    }

    }

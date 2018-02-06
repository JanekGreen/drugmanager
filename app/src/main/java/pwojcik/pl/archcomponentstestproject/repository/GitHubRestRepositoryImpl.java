package pwojcik.pl.archcomponentstestproject.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Handler;

import io.reactivex.Flowable;
import io.reactivex.Observable;
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
import pwojcik.pl.archcomponentstestproject.ui.activity.MainActivity;
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

        DisposableManager.getInstance().add(
                githubUserDao.getUserByLogin(user)
                .map(TypesConverter::makeGithubUser)
                .switchIfEmpty(githubRestInterface.getUser(user))
                .subscribeOn(Schedulers.io())
                .doOnSuccess(githubUser-> githubUserDao.addUser(TypesConverter.makeGithubUserDb(githubUser)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(githubUser -> data.setValue(githubUser),
                        e->System.err.println(e.getMessage()))
        );



        return data;
    }

    @Override
    public LiveData<GithubUser> getData() {
        return data;
    }

    @Override
    public void deleteAll() {
       Thread thread = new Thread(()->githubUserDao.deleteAll());
       thread.start();
       data.setValue(null);

    }

}

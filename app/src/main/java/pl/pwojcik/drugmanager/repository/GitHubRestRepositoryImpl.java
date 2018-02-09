package pl.pwojcik.drugmanager.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.utils.DisposableManager;
import pl.pwojcik.drugmanager.model.TypesConverter;
import pl.pwojcik.drugmanager.model.persistence.GithubUserDao;
import pl.pwojcik.drugmanager.model.restEntity.GithubUser;
import pl.pwojcik.drugmanager.model.retrofit.GithubRestInterface;

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

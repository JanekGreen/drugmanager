package pwojcik.pl.archcomponentstestproject.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import pwojcik.pl.archcomponentstestproject.model.TypesConverter;
import pwojcik.pl.archcomponentstestproject.model.dbEntity.AppDatabase;
import pwojcik.pl.archcomponentstestproject.model.dbEntity.GithubUserDao;
import pwojcik.pl.archcomponentstestproject.model.dbEntity.GithubUserDb;
import pwojcik.pl.archcomponentstestproject.model.restObject.GithubUser;
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
    private static AppDatabase db;


    public GitHubRestRepositoryImpl(GithubRestInterface githubRestInterface, Context context) {
        this.githubRestInterface = githubRestInterface;
        createDatabase(context);
        githubUserDao = db.GithubUserDbDao();
        data = new MutableLiveData<>();
    }

    @Override
    public LiveData<GithubUser> getUser(final String user) {


        GetUserFromDatabaseTask task = new GetUserFromDatabaseTask();
        GithubUserDb githubUserDb = null;
        try {
            githubUserDb = task.execute(user).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (githubUserDb != null) {
            System.out.println("Found user " + user + "in database!");
            data.setValue(TypesConverter.makeGithubUser(githubUserDb));
        } else {
            Call<GithubUser> response = githubRestInterface.getUser(user);
            response.enqueue(new Callback<GithubUser>() {
                @Override
                public void onResponse(Call<GithubUser> call, Response<GithubUser> response) {
                    if (response.isSuccessful()) {
                        AddUserToDatabaseTask addTask = new AddUserToDatabaseTask();
                        addTask.execute(TypesConverter.makeGithubUserDb(response.body()));
                        data.setValue(response.body());

                        System.out.println("User " + user + "Saved into database");
                    } else {
                        data.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<GithubUser> call, Throwable t) {
                }
            });
        }
        return data;
    }

    @Override
    public LiveData<GithubUser> getData() {
        return data;
    }

    private void createDatabase(Context context) {
        if (db == null) {
            db = Room
                    .databaseBuilder(context, AppDatabase.class, "githubDb")
                    .build();
        }
    }

    private class GetUserFromDatabaseTask extends AsyncTask<String, Void, GithubUserDb> {

        @Override
        protected GithubUserDb doInBackground(String... users) {
            return githubUserDao.getUserByLogin(users[0]);
        }
            @Override
            protected void onPostExecute (GithubUserDb githubUser){
                super.onPostExecute(githubUser);
            }
        }

    private class AddUserToDatabaseTask extends AsyncTask<GithubUserDb, Void, Void> {

        @Override
        protected Void doInBackground(GithubUserDb... githubUserDbs) {
            githubUserDao.addUser(githubUserDbs[0]);

            return null;
        }
    }
}

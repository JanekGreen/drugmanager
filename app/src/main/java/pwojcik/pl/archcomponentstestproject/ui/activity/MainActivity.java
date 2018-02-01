package pwojcik.pl.archcomponentstestproject.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pwojcik.pl.archcomponentstestproject.R;
import pwojcik.pl.archcomponentstestproject.viewmodel.GitHubUserViewModel;
import pwojcik.pl.archcomponentstestproject.ui.adapter.GithubUserAdapter;
import pwojcik.pl.archcomponentstestproject.model.restObject.GithubUser;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.etLogin)EditText etLogin;
    @BindView(R.id.btnSearch)Button btnSearch;
    @BindView(R.id.rvMainList) RecyclerView rvMainList;
    private GitHubUserViewModel gitHubUserViewModel;
    private GithubUserAdapter githubUserAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gitHubUserViewModel = ViewModelProviders.of(this).get(GitHubUserViewModel.class);
        subscribeUiGithubUsers();
        setupUi();

    }

    @OnClick(R.id.btnSearch)
    public void onClicked(){
        String input = etLogin.getText().toString();
        if(!input.isEmpty()){
            gitHubUserViewModel.loadUser(input);
        }
    }
    private void setupUi(){
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvMainList.setLayoutManager(llm);
        githubUserAdapter = new GithubUserAdapter();
        rvMainList.setAdapter(githubUserAdapter);
    }

    private void subscribeUiGithubUsers(){

        gitHubUserViewModel.getData().observe(this, new Observer<GithubUser>() {
            @Override
            public void onChanged(@Nullable GithubUser githubUser) {
                if(githubUser == null){
                    Toast.makeText(MainActivity.this,
                            "Nie znaleziono użytkownika o nicku "+etLogin.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }
                githubUserAdapter.setUser(githubUser);
                githubUserAdapter.notifyDataSetChanged();
            }
        });
    }
}

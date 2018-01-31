package pwojcik.pl.archcomponentstestproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pwojcik.pl.archcomponentstestproject.R;
import pwojcik.pl.archcomponentstestproject.model.restObject.GitHubUser;


/**
 * Created by wojci on 29.01.2018.
 */

public class GithubUserAdapter extends RecyclerView.Adapter<GithubUserAdapter.GithubUserViewVolder> {

    private GitHubUser user = new GitHubUser();

    public void setUser(GitHubUser  user){
        this.user = user;
    }
    @Override
    public GithubUserViewVolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.githubuser_list_row, parent, false);

        return new  GithubUserViewVolder(itemView);
    }


    @Override
    public void onBindViewHolder(GithubUserViewVolder holder, int position) {
        holder.tvLogin.setText( user.getLogin());
        holder.tvCreateDate.setText(user.getCreatedAt());
        holder.tvUrl.setText(user.getUrl());
        Picasso
                .with(holder.ivImage.getContext())
                .load(user.getAvatarUrl())
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class GithubUserViewVolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvLogin)  TextView tvLogin;
        @BindView(R.id.tvURL) TextView tvUrl;
        @BindView(R.id.tvCreatedate)TextView tvCreateDate;
        @BindView(R.id.ivImage)ImageView ivImage;

        public GithubUserViewVolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

package pl.pwojcik.drugmanager.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.restEntity.GithubUser;
import pwojcik.pl.archcomponentstestproject.R;


/**
 * Created by wojci on 29.01.2018.
 */

public class GithubUserAdapter extends RecyclerView.Adapter<GithubUserAdapter.GithubUserViewVolder> {

    private GithubUser user;

    public void setUser(GithubUser user){
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
        String date = user.getCreatedAt().substring(0, user.getCreatedAt().indexOf("T"));
        String dateParts[] = date.split("-");
        StringBuilder sb = new StringBuilder();
        date = sb.append("Data utworzenia: ").append(dateParts[2]).append("-").append(dateParts[1]).append("-").append(dateParts[0]).toString();
        holder.tvCreateDate.setText(date);
        holder.tvUrl.setText(user.getHtmlUrl());
        holder.tvRepoCount.setText(String.valueOf(user.getPublicRepos()));
        Picasso
                .with(holder.ivImage.getContext())
                .load(user.getAvatarUrl())
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {

        return user == null?0:1;
    }

    static class GithubUserViewVolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvLogin)  TextView tvLogin;
        @BindView(R.id.tvURL) TextView tvUrl;
        @BindView(R.id.tvRepoCount)TextView tvRepoCount;
        @BindView(R.id.tvCreatedate)TextView tvCreateDate;
        @BindView(R.id.ivImage)ImageView ivImage;

        public GithubUserViewVolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

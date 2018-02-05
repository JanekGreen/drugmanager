package pwojcik.pl.archcomponentstestproject.model;

import pwojcik.pl.archcomponentstestproject.model.persistence.GithubUserDb;
import pwojcik.pl.archcomponentstestproject.model.restEntity.GithubUser;

/**
 * Created by pawel on 31.01.18.
 */

public class TypesConverter {

    public static GithubUser makeGithubUser(GithubUserDb githubUserDb) {
        return new GithubUser(githubUserDb.getId(), githubUserDb.getLogin(), githubUserDb.getAvatarUrl(), githubUserDb.getGravatarId(), githubUserDb.getUrl(), githubUserDb.getHtmlUrl(), githubUserDb.getPublicRepos(), githubUserDb.getPublicGists(), githubUserDb.getFollowers(), githubUserDb.getFollowing(), githubUserDb.getCreatedAt(), githubUserDb.getUpdatedAt());
    }

    public static GithubUserDb makeGithubUserDb(GithubUser githubUser) {
        return new GithubUserDb(githubUser.getId(), githubUser.getLogin(), githubUser.getAvatarUrl(), githubUser.getGravatarId(), githubUser.getUrl(), githubUser.getHtmlUrl(), githubUser.getPublicRepos(), githubUser.getPublicGists(), githubUser.getFollowers(), githubUser.getFollowing(), githubUser.getCreatedAt(), githubUser.getUpdatedAt());
    }

}

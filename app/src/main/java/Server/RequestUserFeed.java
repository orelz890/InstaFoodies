package Server;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Post;
import models.User;
import models.UserAccountSettings;

public class RequestUserFeed implements Serializable {


    @SerializedName("user")
    private User user;

    @SerializedName("account")
    private UserAccountSettings account;

    @SerializedName("posts")
    private List<Post> posts;


    public RequestUserFeed(User user, UserAccountSettings account, List<Post> posts) {
        this.setUser(user);
        this.setAccount(account);
        this.setPosts(posts);
    }

    public RequestUserFeed(){

    }

    public RequestUserFeed(User user, UserAccountSettings account, Post[] posts) {
        this.user = user;
        this.account = account;
        this.posts.addAll(Arrays.asList(posts));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = new User(user);
    }

    public UserAccountSettings getAccount() {
        return account;
    }

    public void setAccount(UserAccountSettings account) {
        this.account = new UserAccountSettings(account);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Post getPost(int pos) {
        return posts.get(pos);
    }

    public void setPosts(List<Post> posts) {
        this.posts = new ArrayList<>();
        this.posts.addAll(posts);
    }

    public void patchPost(int pos, Post post){
        if (this.posts != null){
            this.posts.set(pos,post);
        }
    }

    public void setPosts(Post[] posts) {
        this.posts = new ArrayList<>();
        this.posts.addAll(Arrays.asList(posts));
    }

    public int size(){
        return this.posts.size();
    }

    @Override
    public String toString() {
        return "RequestUserFeed{" +
                "user=" + user +
                ", account=" + account +
                ", posts=" + posts +
                '}';
    }
}

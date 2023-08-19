package Server;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Post;
import models.User;

public class RequestUserAndPosts {

    @SerializedName("posts")
    private List<Post> posts;

    @SerializedName("user")
    private User user;


    public RequestUserAndPosts(List<Post> posts, User user) {
        this.setPosts(posts);
        this.user = user;
    }

    @Override
    public String toString() {
        return "RequestUserAndPosts{" +
                "posts=" + posts +
                ", user=" + user +
                '}';
    }

    public RequestUserAndPosts(){
        posts = new ArrayList<>();

    }

    public RequestUserAndPosts(Post[] posts) {
        this.posts.addAll(Arrays.asList(posts));
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setPosts(Post[] posts) {
        this.posts = new ArrayList<>();
        this.posts.addAll(Arrays.asList(posts));
    }

    public int size(){
        return this.posts.size();
    }
}

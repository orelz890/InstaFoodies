package Server;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Post;
import models.User;
import models.UserAccountSettings;

public class RequestPosts {

    @SerializedName("posts")
    private List<Post> posts;


    public RequestPosts( List<Post> posts) {
        this.setPosts(posts);
    }

    public RequestPosts(){
        posts = new ArrayList<>();

    }

    public RequestPosts(Post[] posts) {
        this.posts.addAll(Arrays.asList(posts));
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

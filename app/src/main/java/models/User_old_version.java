package models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Login.Post;

public class User_old_version {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("passwordHash")
    private String passwordHash;

    @SerializedName("isBusiness")
    private boolean isBusiness;

    @SerializedName("followers")
    private List<String> followers = new ArrayList<>();

    @SerializedName("following")
    private List<String> following = new ArrayList<>();

    @SerializedName("Cart")
    private Map<String, String> Cart = new HashMap<>();

    @SerializedName("Likes")
    private Map<String, String> Likes = new HashMap<>();

    @SerializedName("myPosts")
    private List<Post> myPosts = new ArrayList<>();

    @SerializedName("myRecipePosts")
    private List<Post> myRecipePosts = new ArrayList<>();

    public User_old_version(String name, String email, String password) {
        init();
        this.setName(name);
        this.setEmail(email);
        this.setPasswordHash(passwordHash);
        this.setBusiness(false);
    }


    public User_old_version(String id, String name, String email, String passwordHash, boolean isBusiness,
                            List<String> followers, List<String> following, Map<String, String> cart,
                            Map<String, String> likes, List<Post> myPosts, List<Post> myRecipePosts) {
        init();
        this.setId(id);
        this.setName(name);
        this.setEmail(email);
        this.setPasswordHash(passwordHash);
        this.setBusiness(isBusiness);
        this.setFollowers(followers);
        this.setFollowing(following);
        this.setCart(cart);
        this.setLikes(likes);
        this.setMyPosts(myPosts);
        this.setMyRecipePosts(myRecipePosts);
    }

    private void init(){
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.Cart = new HashMap<>();
        this.Likes = new HashMap<>();
        this.myPosts = new ArrayList<>();
        this.myRecipePosts = new ArrayList<>();
    }

    public HashMap<String, Object> userHash(){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("uid", this.id);
        ans.put("name", this.name);
        ans.put("email", this.email);
        ans.put("password", this.passwordHash);
        ans.put("isBusiness", this.isBusiness);
        ans.put("followers", this.followers);
        ans.put("following", this.following);
        ans.put("Cart", this.Cart);
        ans.put("Likes", this.Likes);
        ans.put("myPosts", this.myPosts);
        ans.put("myRecipePosts", this.myRecipePosts);
        return ans;
    }

//    public void init(){
//        this.followers = new ArrayList<>();
//        this.following = new ArrayList<>();
//        this.Cart = new HashMap<>();
//        this.Likes = new HashMap<>();
//        this.myPosts = new ArrayList<>();
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = new ArrayList<>();
        this.followers.addAll(followers);
    }


    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = new ArrayList<>();
        this.following.addAll(following);
    }

    public Map<String, String> getCart() {
        return Cart;
    }

    public void setCart(Map<String, String> cart) {
        this.Cart = new HashMap<>();
        this.Cart.putAll(cart);
    }

    public Map<String, String> getLikes() {
        return Likes;
    }

    public void setLikes(Map<String, String> likes) {
        Likes = new HashMap<>();
        Likes.putAll(likes);
    }

    public List<Post> getMyPosts() {
        return myPosts;
    }

    public void setMyPosts(List<Post> myRecipes) {
        this.myPosts = new ArrayList<>();
        this.myPosts.addAll(myRecipes);
    }

    public List<Post> getMyRecipePosts() {
        return myRecipePosts;
    }

    public void setMyRecipePosts(List<Post> myRecipePosts) {
        this.myRecipePosts = new ArrayList<>();
        this.myRecipePosts.addAll(myRecipePosts);
    }
}
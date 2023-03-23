package Login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

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


    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.passwordHash = password;
        this.isBusiness = false;
    }

    public User(String id, String name, String email, String passwordHash, boolean isBusiness,
                List<String> followers, List<String> following, Map<String, String> cart,
                Map<String, String> likes, List<Post> myPosts) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isBusiness = isBusiness;
        this.followers = followers;
        this.following = following;
        Cart = cart;
        Likes = likes;
        this.myPosts = myPosts;
    }


    public HashMap<String, Object> userHash(){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("uid", this.id);
        ans.put("email", this.email);
        ans.put("password", this.passwordHash);
        ans.put("isBusiness", this.isBusiness);
        ans.put("followers", this.followers);
        ans.put("following", this.following);
        ans.put("Cart", this.Cart);
        ans.put("Likes", this.Likes);
        ans.put("myPosts", this.myPosts);
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
        this.followers.clear();
        this.followers.addAll(followers);
    }


    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following.clear();
        this.following.addAll(following);
    }

    public Map<String, String> getCart() {
        return Cart;
    }

    public void setCart(Map<String, String> cart) {
        this.Cart.clear();
        this.Cart.putAll(cart);
    }

    public Map<String, String> getLikes() {
        return Likes;
    }

    public void setLikes(Map<String, String> likes) {
        Likes = likes;
    }

    public List<Post> getMyRecipes() {
        return myPosts;
    }

    public void setMyRecipes(List<Post> myRecipes) {
        this.myPosts.clear();
        this.myPosts.addAll(myRecipes);
    }
}
package models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class UserAccountSettings {

    @SerializedName("username")
    private String username;

    @SerializedName("description")
    private String description;

    @SerializedName("display_name")
    private String display_name;

    @SerializedName("profile_photo")
    private String profile_photo;

    @SerializedName("isBusiness")
    private boolean isBusiness;

    @SerializedName("followers")
    private int followers;

    @SerializedName("following")
    private int following;

    @SerializedName("posts")
    private int posts;

    @SerializedName("website")
    private String website;


    public HashMap<String, Object> userAccountHashForServer(String username, String description,
                                                            String display_name,
                                                            String profile_photo,
                                                            boolean isBusiness,
                                                            int followers,
                                                            int following,
                                                            int posts,
                                                            String website,
                                                            String email){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("email", email);
        ans.put("description", description);
        ans.put("display_name", display_name);
        ans.put("followers", followers);
        ans.put("following", following);
        ans.put("isBusiness", isBusiness);
        ans.put("posts", posts);
        ans.put("profile_photo", profile_photo);
        ans.put("username", username);
        ans.put("website", website);
        return ans;
    }

    public HashMap<String, Object> userAccountHashForServer(String email){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("email", email);
        ans.put("description", this.description);
        ans.put("display_name", this.display_name);
        ans.put("followers", this.followers);
        ans.put("following", this.following);
        ans.put("isBusiness", this.isBusiness);
        ans.put("posts", this.posts);
        ans.put("profile_photo", this.profile_photo);
        ans.put("username", this.username);
        ans.put("website", this.website);
        return ans;
    }

    public UserAccountSettings(){
        this.description = "none";
        this.display_name = "none";
        this.followers = 0;
        this.following = 0;
        this.posts = 0;
        this.profile_photo = "none";
        this.username = "none";
        this.website = "none";
        this.isBusiness = false;
    }

    public UserAccountSettings(String username, String description, String display_name,
                               String profile_photo, boolean isBusiness, int followers,
                               int following, int posts, String website) {
        this.username = username;
        this.description = description;
        this.display_name = display_name;
        this.profile_photo = profile_photo;
        this.isBusiness = isBusiness;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
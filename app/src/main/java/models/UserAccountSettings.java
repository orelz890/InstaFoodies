package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAccountSettings implements Serializable {


    @SerializedName("description")
    private String description;


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

    @SerializedName("following_ids")
    private List<String> following_ids;



    public UserAccountSettings(UserAccountSettings settings) {
        this(settings.description, settings.profile_photo, settings.isBusiness, settings.followers,
                settings.following, settings.posts, settings.website, settings.following_ids);
    }


    public HashMap<String, Object> userAccountHashForServer(String description,
                                                            String profile_photo,
                                                            boolean isBusiness,
                                                            int followers,
                                                            int following,
                                                            int posts,
                                                            String website,
                                                            String email,
                                                            List<String> following_ids) {
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("email", email);
        ans.put("description", description);
        ans.put("followers", followers);
        ans.put("following", following);
        ans.put("isBusiness", isBusiness);
        ans.put("posts", posts);
        ans.put("profile_photo", profile_photo);
        ans.put("website", website);
        ans.put("following_ids", following_ids);
        return ans;
    }

    public HashMap<String, Object> userAccountHashForServer() {
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("description", this.description);
        ans.put("followers", this.followers);
        ans.put("following", this.following);
        ans.put("isBusiness", this.isBusiness);
        ans.put("posts", this.posts);
        ans.put("profile_photo", this.profile_photo);
        ans.put("website", this.website);
        ans.put("following_ids", this.following_ids);
        return ans;
    }

    public UserAccountSettings() {
        this.description = "none";
        this.followers = 0;
        this.following = 0;
        this.posts = 0;
        this.profile_photo = "none";
        this.website = "none";
        this.isBusiness = false;
        this.following_ids = new ArrayList<>();
        this.following_ids.add("eVkAc1hVnAOCdX8QCFFGxZqFU3c2");
    }

    public UserAccountSettings(String description, String profile_photo, boolean isBusiness, int followers,
                               int following, int posts, String website, List<String> following_ids) {
        this.description = description;
        this.profile_photo = profile_photo;
        this.isBusiness = isBusiness;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.website = website;
        setFollowing_ids(following_ids);
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getFollowing_ids() {
        return following_ids;
    }

    public void setFollowing_ids(List<String> following_ids) {
        this.following_ids = new ArrayList<>();
        this.following_ids.addAll(following_ids);
    }
}
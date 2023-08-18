package models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAccountSettings implements Parcelable, Serializable {


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


    @SerializedName("followers_ids")
    private List<String> followers_ids;


    public UserAccountSettings(UserAccountSettings settings) {
        this(settings.description, settings.profile_photo, settings.isBusiness, settings.followers,
                settings.following, settings.posts, settings.website, settings.following_ids,settings.followers_ids);
    }


    protected UserAccountSettings(Parcel in) {
        description = in.readString();
        profile_photo = in.readString();
        isBusiness = in.readByte() != 0;
        followers = in.readInt();
        following = in.readInt();
        posts = in.readInt();
        website = in.readString();
        following_ids = in.createStringArrayList();
        followers_ids = in.createStringArrayList();
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public HashMap<String, Object> userAccountHashForServer(String description,
                                                            String profile_photo,
                                                            boolean isBusiness,
                                                            int followers,
                                                            int following,
                                                            int posts,
                                                            String website,
                                                            String email,
                                                            List<String> following_ids,
                                                            List<String> followers_ids) {
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
        ans.put("followers_ids", followers_ids);
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
        ans.put("followers_ids", this.followers_ids);
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
        this.followers_ids = new ArrayList<>();
    }

    public UserAccountSettings(String description, String profile_photo, boolean isBusiness, int followers,
                               int following, int posts, String website, List<String> following_ids,List<String> followers_ids) {
        this.description = description;
        this.profile_photo = profile_photo;
        this.isBusiness = isBusiness;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.website = website;
        setFollowing_ids(following_ids);
        setFollowers_ids(followers_ids);
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


    public boolean getIsBusiness() {
        return isBusiness;
    }


    public void setIsBusiness(boolean business) {
        isBusiness = business;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", isBusiness=" + isBusiness +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", website='" + website + '\'' +
                ", following_ids=" + following_ids +
                ", followers_ids=" + followers_ids +
                '}';
    }

    public int getFollowers() {
        if (followers_ids != null){
            return followers_ids.size();
        }
        return 0;
//        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        if (following_ids != null){
            return following_ids.size();
        }
        return 0;
//        return following;
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

    public List<String> getFollowers_ids() {
        return followers_ids;
    }

    public void setFollowers_ids(List<String> followers_ids) {
        this.followers_ids = new ArrayList<>();
        this.followers_ids.addAll(followers_ids);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.website);
        dest.writeStringList(this.followers_ids);
        dest.writeStringList(this.following_ids);
        dest.writeBoolean(this.isBusiness);
        dest.writeInt(this.posts);
        dest.writeInt(this.following);
        dest.writeInt(this.followers);
        dest.writeString(profile_photo);
    }
}
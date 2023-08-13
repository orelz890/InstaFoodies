package models;
//
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import androidx.annotation.NonNull;
//
//import com.google.gson.Gson;
//import com.google.gson.annotations.SerializedName;
//
//import java.io.Serializable;
//
//public class UserSettings implements Serializable, Parcelable {
//
//    @SerializedName("user")
//    private User user;
//
//    @SerializedName("settings")
//    private UserAccountSettings settings;
//
//    public UserSettings(User user, UserAccountSettings settings) {
//        this.user = new User(user);
//        this.settings = new UserAccountSettings(settings);
//    }
//
//    public UserSettings() {
//
//    }
//
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = new User(user);
//    }
//
//    public UserAccountSettings getSettings() {
//        return settings;
//    }
//
//    public void setSettings(UserAccountSettings settings) {
//        this.settings = new UserAccountSettings(settings);
//    }
//
//    @Override
//    public String toString() {
//        return "UserSettings{" +
//                "user=" + user.toString()+
//                ", settings=" + settings.toString() +
//                '}';
//    }
//    public String toJson() {
//        Gson gson = new Gson();
//        return gson.toJson(this);
//    }
//
//    public static UserSettings fromJson(String json) {
//        Gson gson = new Gson();
//        return gson.fromJson(json, UserSettings.class);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel dest, int flags) {
//
//    }
//}

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserSettings implements Serializable, Parcelable {

    @SerializedName("user")
    private User user;

    @SerializedName("settings")
    private UserAccountSettings settings;

    public UserSettings(User user, UserAccountSettings settings) {
        this.user = new User(user);
        this.settings = new UserAccountSettings(settings);
    }

    public UserSettings() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = new User(user);
    }

    public UserAccountSettings getSettings() {
        return settings;
    }

    public void setSettings(UserAccountSettings settings) {
        this.settings = new UserAccountSettings(settings);
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user.toString() +
                ", settings=" + settings.toString() +
                '}';
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static UserSettings fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UserSettings.class);
    }

    // Parcelable implementation
    protected UserSettings(Parcel in) {
        user = (User) in.readSerializable();
        settings = (UserAccountSettings) in.readSerializable();
    }

    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel in) {
            return new UserSettings(in);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(user);
        dest.writeSerializable(settings);
    }



}

package models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserSettings implements Serializable {

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
                "user=" + user +
                ", settings=" + settings +
                '}';
    }
}

package Server;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.User;
import models.UserAccountSettings;

public class RequestUsersAndAccounts implements Serializable {

    @SerializedName("users")
    private List<User> users;

    @SerializedName("accountSettings")
    private List<UserAccountSettings> accountSettings;

    public RequestUsersAndAccounts(User[] users, UserAccountSettings[] settings) {
        this.users = new ArrayList<>();
        this.accountSettings = new ArrayList<>();

        this.users.addAll(Arrays.asList(users));
        this.accountSettings.addAll(Arrays.asList(settings));
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser(int pos) {
        if (pos < this.users.size()) {
            return users.get(pos);
        }
        return null;
    }
    public void setUsers(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
    }

    public void setUsers(User[] users) {
        this.users.clear();
        this.users.addAll(Arrays.asList(users));
    }

    public List<UserAccountSettings> getAccountSettings() {
        return accountSettings;
    }

    public UserAccountSettings getAccount(int pos) {
        if (pos < this.accountSettings.size()) {
            return accountSettings.get(pos);
        }
        return null;
    }

    public void setAccountSettings(List<UserAccountSettings> accountSettings) {
        this.accountSettings.clear();
        this.accountSettings.addAll(accountSettings);
    }

    public void setAccountSettings(UserAccountSettings[] accountSettings) {
        this.accountSettings.clear();
        this.accountSettings.addAll(Arrays.asList(accountSettings));
    }

    public int size(){
        return this.users.size();
    }
}

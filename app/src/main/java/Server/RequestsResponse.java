package Server;

import com.google.gson.annotations.SerializedName;
import com.nostra13.universalimageloader.utils.L;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.User;
import models.UserAccountSettings;

public class RequestsResponse implements Serializable {

    @SerializedName("users")
    private List<User> users;

    @SerializedName("accountSettings")
    private List<UserAccountSettings> accountSettings;

    @SerializedName("responseTypes")
    private List<String> responseTypes;

    public RequestsResponse(User[] users, UserAccountSettings[] settings, String[] types) {
        this.users = new ArrayList<>();
        this.accountSettings = new ArrayList<>();
        this.responseTypes = new ArrayList<>();

        this.users.addAll(Arrays.asList(users));
        this.accountSettings.addAll(Arrays.asList(settings));
        this.responseTypes.addAll(Arrays.asList(types));

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

    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public String getType(int pos) {
        if (pos < this.responseTypes.size()) {
            return responseTypes.get(pos);
        }
        return null;
    }

    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes.clear();
        this.responseTypes.addAll(responseTypes);
    }

    public void setResponseTypes(String[] responseTypes) {
        this.responseTypes.clear();
        this.responseTypes.addAll(Arrays.asList(responseTypes));
    }

    public void removeItem(int pos){
        int userSize = this.users.size();
        int typesSize = this.responseTypes.size();
        int accountsSize = this.accountSettings.size();
        if (userSize == accountsSize && accountsSize == typesSize && pos < userSize){
            this.users.remove(pos);
            this.accountSettings.remove(pos);
            this.responseTypes.remove(pos);
        }
    }

    public int size(){
        return this.users.size();
    }
}

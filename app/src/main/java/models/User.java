package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

    @SerializedName("passwordHash")
    private String passwordHash;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("email")
    private String email;

    @SerializedName("phone_number")
    private String phone_number;

    @SerializedName("username")
    private String username;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("state")
    private String state;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    @SerializedName("device_token")
    private String device_token;

    public User(User user) {
        this(user.passwordHash, user.user_id, user.email, user.phone_number, user.username, user.full_name, user.state, user.date, user.time);
    }

    public HashMap<String, Object> userHashForServer(String password, String email,
                                                     String phone_number, String username,
                                                     String full_name, String state, String date,
                                                     String time) {
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("password", password);
        ans.put("email", email);
        ans.put("phone_number", phone_number);
        ans.put("username", username);
        ans.put("full_name", full_name);
        ans.put("state", state);
        ans.put("date", date);
        ans.put("time", time);
        return ans;
    }

    public HashMap<String, Object> userMapForServer() {
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("email", email);
        ans.put("user_id", this.user_id);
        ans.put("phone_number", this.phone_number);
        ans.put("username", this.username);
        ans.put("full_name", this.full_name);
        ans.put("state", this.state);
        ans.put("date", this.date);
        ans.put("time", this.time);
        return ans;

    }

    public User() {

    }

    public User(String passwordHash, String user_id, String email, String phone_number,
                String username, String full_name, String state, String date,
                String time) {

        this.passwordHash = passwordHash;
        this.user_id = user_id;
        this.email = email;
        this.phone_number = phone_number;
        this.username = username;
        this.full_name = full_name;
        this.state = state;
        this.date = date;
        this.time = time;
        this.device_token = "";
    }



    public User(String passwordHash, String user_id, String email, String phone_number,
                String username, String full_name) {

        this.passwordHash = passwordHash;
        this.user_id = user_id;
        this.email = email;
        this.phone_number = phone_number;
        this.username = username;
        this.full_name = full_name;
        this.state = "online";
        this.date = "";
        this.time = "";
        this.device_token = "";
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}

package Login;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class User {

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

    public HashMap<String, Object> userHashForServer(String password, String email,
                                                     String phone_number, String username){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("password", password);
        ans.put("email", email);
        ans.put("phone_number", phone_number);
        ans.put("username", username);
        return ans;
    }

    public User(String passwordHash, String user_id, String email, String phone_number,
                String username) {

        this.passwordHash = passwordHash;
        this.user_id = user_id;
        this.email = email;
        this.phone_number = phone_number;
        this.username = username;
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
}

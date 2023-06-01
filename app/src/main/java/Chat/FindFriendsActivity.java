package Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.instafoodies.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Server.RetrofitInterface;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FindFriendsActivity extends AppCompatActivity {
    private MaterialToolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private UserAdapter userAdapter;
    private DatabaseReference UsersRef;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8080";
    private static ServerMethods serverMethods;

    private UserSettings userSettings;
    private String uid;
    private static List<String> friendsUserIds; // List of user IDs you want to display


    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private FirebaseUser currentUser;

    private static Drawable drawable_profile_image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        serverMethods = new ServerMethods(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        drawable_profile_image = getResources().getDrawable(R.drawable.profile_image);

    }

    @Override
    protected void onStart() {
        super.onStart();

        serverMethods.retrofitInterface.getUser(uid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    User user = response.body();
                    if (user != null) {
                        serverMethods.retrofitInterface.getUserAccountSettings(uid).enqueue(new Callback<UserAccountSettings>() {
                            @Override
                            public void onResponse(@NonNull Call<UserAccountSettings> call, @NonNull Response<UserAccountSettings> response) {
                                if (response.code() == 200) {
                                    UserAccountSettings userAccount = response.body();
                                    if (userAccount != null) {
                                        userSettings = new UserSettings(user,userAccount);
                                        System.out.println("getFollowing = " + userAccount.getFollowing());
                                        friendsUserIds = userAccount.getFollowing_ids();
                                        friendsUserIds.add("eVkAc1hVnAOCdX8QCFFGxZqFU3c2");
//                                        friendsUserIds.add("111");

                                        serverMethods.retrofitInterface.getFollowingUsers(friendsUserIds).enqueue(new Callback<User[]>() {
                                            @Override
                                            public void onResponse(@NonNull Call<User[]> call, @NonNull Response<User[]> response) {
                                                if (response.code() == 200) {
                                                    System.out.println("Success!!!");

                                                    User[] users = response.body();
                                                    if (users != null){
                                                        System.out.println("users.get(0).getFull_name() = " + users[0].getFull_name());

                                                        serverMethods.retrofitInterface.getFollowingUsersAccountSettings(friendsUserIds).enqueue(new Callback<UserAccountSettings[]>() {
                                                            @Override
                                                            public void onResponse(@NonNull Call<UserAccountSettings[]> call, @NonNull Response<UserAccountSettings[]> response) {
                                                                if (response.code() == 200) {
                                                                    UserAccountSettings[] usersAccount = response.body();
                                                                    if (usersAccount != null){
                                                                        // Create the adapter and set it to the RecyclerView
                                                                        userAdapter = new UserAdapter(users,usersAccount);
                                                                        FindFriendsRecyclerList.setAdapter(userAdapter);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(@NonNull Call<UserAccountSettings[]> call, @NonNull Throwable t) {

                                                            }
                                                        });
                                                    }
                                                    else{
                                                        System.out.println("users == null");
                                                    }
                                                }
                                                else{
                                                    System.out.println("There was an error");
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<User[]> call, @NonNull Throwable t) {
                                                System.out.println(t.getMessage());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<UserAccountSettings> call, @NonNull Throwable t) {
                                System.out.println(t.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {

            }
        });
    }

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        private List<User> userList;
        private List<UserAccountSettings> settings;

        public UserAdapter(User[] my_users, UserAccountSettings[] usersAccountSettings) {
            userList = new ArrayList<>();
            settings = new ArrayList<>();
            userList.addAll(Arrays.asList(my_users));
            settings.addAll(Arrays.asList(usersAccountSettings));

            // Fetch user data based on the user IDs and populate the userList
            // You can make a database query or fetch data from your data source (e.g., Firebase Firestore)
            // Populate the userList with the retrieved user data
            System.out.println("friendsUserIds = " + friendsUserIds.get(0).toString());

        }

        // Create ViewHolder for each user item
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
            return new ViewHolder(view);
        }

        // Bind data to the views in each item
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = userList.get(position);
            UserAccountSettings userAccountSettings = settings.get(position);

            holder.userName.setText(user.getUsername());
            holder.userStatus.setText(user.getEmail());
            // Load the profile image using a library like Picasso or Glide
            String profile_photo = userAccountSettings.getProfile_photo();
            if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                System.out.println("!profile_photo.isEmpty(): " + profile_photo);
                Picasso.get().load(profile_photo).into(holder.profileImage);
            }
            else {
                holder.profileImage.setImageResource(R.drawable.profile_image);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    System.out.println("position = " + position);
//                    String visit_user_id = friendsUserIds.get(position);
                    UserSettings userSettings = new UserSettings(user,userAccountSettings);

                    Intent intent = new Intent(FindFriendsActivity.this, SettingsActivity.class);
                    intent.putExtra("userSettings", userSettings);
                    startActivity(intent);
                }
            });

        }

        // Get the total number of user items
        @Override
        public int getItemCount() {
            return userList.size();
        }

        // ViewHolder class to hold the views in each item
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView userName,userStatus;
            CircleImageView profileImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                profileImage = itemView.findViewById(R.id.users_profile_image);
            }
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Server.RequestUsersAndAccounts;
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

        createFeed();

    }

    private void createFeed() {
        System.out.println("uid = " + uid);
        serverMethods.retrofitInterface.getFollowingUsersAndAccounts(uid).enqueue(new Callback<RequestUsersAndAccounts>() {
            @Override
            public void onResponse(@NonNull Call<RequestUsersAndAccounts> call, @NonNull Response<RequestUsersAndAccounts> response) {
                if (response.code() == 200) {
                    System.out.println("Find Success!!!");

                    RequestUsersAndAccounts usersAndAccounts = response.body();
                    if (usersAndAccounts != null){
                        userAdapter = new UserAdapter(usersAndAccounts);
                        FindFriendsRecyclerList.setAdapter(userAdapter);
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
            public void onFailure(@NonNull Call<RequestUsersAndAccounts> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        private RequestUsersAndAccounts data;

        public UserAdapter(RequestUsersAndAccounts usersAndAccounts) {
            data = usersAndAccounts;


            // Fetch user data based on the user IDs and populate the userList
            // You can make a database query or fetch data from your data source (e.g., Firebase Firestore)
            // Populate the userList with the retrieved user data

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
            User user = data.getUser(position);
            UserAccountSettings userAccountSettings = data.getAccount(position);

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
                    System.out.println("position = " + position);
                    String visit_user_id = data.getUser(position).getUser_id();
                    UserSettings userSettings = new UserSettings(user,userAccountSettings);

                    Intent intent = new Intent(FindFriendsActivity.this, ChatProfileActivity.class);
                    intent.putExtra("userSettings", (Serializable) userSettings);
                    intent.putExtra("visit_user_id", visit_user_id);
                    startActivity(intent);
                }
            });
        }

        // Get the total number of user items
        @Override
        public int getItemCount() {
            return data.size();
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


}
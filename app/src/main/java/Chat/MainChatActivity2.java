package Chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Login.LoginActivity;
import Server.RequestUserFeed;
import Server.RequestUsersAndAccounts;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainChatActivity2 extends AppCompatActivity
{
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private MaterialToolbar mToolbar;

    private AutoCompleteTextView acSearchView;
    private CustomAutocompleteAdapter autocompleteAdapter;


    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private TextView requestTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String currentUserId;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager2;


    private FriendsAdapter findFriendsAdapter;
    private RecyclerView findFriendsList;

    private RequestUsersAndAccounts usersAndAccounts;

    private ChatsAdapter chatsAdapter;
    private RecyclerView chatsList;



    private DatabaseReference ContactsRef;
    private static ServerMethods serverMethods;
    private static Context context;


    private ProgressDialog loadingBar;


    private String saveState, saveCurrentTime, saveCurrentDate;
    private String checker = "",myUrl="";
    private Uri fileUri;
    private StorageTask uploadTask;

    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat2);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        calendar = Calendar.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(currentUserId);
        RootRef = FirebaseDatabase.getInstance().getReference();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId).getRef();
        context = this;


        serverMethods = new ServerMethods(context);

        acSearchView = findViewById(R.id.ac_searchView);

        findFriendsList = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        findFriendsList.setLayoutManager(linearLayoutManager);

        chatsList = (RecyclerView) findViewById(R.id.ChatsRecyclerView);
        linearLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatsList.setLayoutManager(linearLayoutManager2);

        setTextViews();

        mToolbar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatUp");
    }

    private void setTextViews() {
        requestTextView = (TextView) findViewById(R.id.requestTextView);
        requestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestsActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.main_logout_option:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
            case R.id.main_settings_option:
                SendUserToSettingsActivity();
                break;
            case R.id.main_find_friends_option:
                SendUserToFindFriendsActivity();
                break;
            case R.id.main_create_group_option:
                RequestNewGroup();
                break;



            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainChatActivity2.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameField = new EditText(MainChatActivity2.this);
        groupNameField.setHint("e.g Tommy birthday party");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainChatActivity2.this, "Please enter a group name...", Toast.LENGTH_LONG).show();
                }
                else {
                    CreateNewGroup(groupName);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private void CreateNewGroup(String groupName) {

        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainChatActivity2.this, groupName + " group is Created Successfully!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void createContactsFeed() {
        System.out.println("MainCat2 - im in onStart createFeed()");
        ContactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    serverMethods.retrofitInterface.getContactsUsersAndSettings(currentUserId).enqueue(new Callback<RequestUsersAndAccounts>() {
                        @Override
                        public void onResponse(@NonNull Call<RequestUsersAndAccounts> call, @NonNull Response<RequestUsersAndAccounts> response) {
                            if (response.code() == 200) {
                                System.out.println("Contacts Success!!!");

                                RequestUsersAndAccounts usersAndAccounts = response.body();
                                if (usersAndAccounts != null) {
                                    System.out.println("usersAndAccounts.size = " + usersAndAccounts.size());

                                    chatsAdapter = new ChatsAdapter(usersAndAccounts);
                                    chatsList.setAdapter(chatsAdapter);

                                } else {
                                    System.out.println("users == null");
                                }
                            } else {
                                System.out.println("There was an error");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<RequestUsersAndAccounts> call, @NonNull Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("ContactsFragment - createFeed - onCancelled: " + error.getMessage());
            }
        });

    }

    private void createChatsFeed() {
        serverMethods.retrofitInterface.getFollowingUsersAndAccounts(currentUserId).enqueue(new Callback<RequestUsersAndAccounts>() {
            @Override
            public void onResponse(@NonNull Call<RequestUsersAndAccounts> call, @NonNull Response<RequestUsersAndAccounts> response) {
                if (response.code() == 200) {
                    System.out.println("Find Success!!!");

                    usersAndAccounts = response.body();
                    if (usersAndAccounts != null){
                        List<UserSettings> listUserSettings = new ArrayList<>();
                        for (int i = 0; i < usersAndAccounts.size(); i++){
                            listUserSettings.add(new UserSettings(usersAndAccounts.getUser(i), usersAndAccounts.getAccount(i)));

                        }
                        autocompleteAdapter = new CustomAutocompleteAdapter(context, listUserSettings);

                        findFriendsAdapter = new FriendsAdapter(usersAndAccounts);
                        findFriendsList.setAdapter(findFriendsAdapter);
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


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("\nMainChatActivity2 - onStart");
        if (currentUser == null){
            SendUserToLoginActivity();
        }
//        createContactsFeed();
//        createChatsFeed();
        checkShit();

    }

    private void checkShit() {
        serverMethods.retrofitInterface.getProfileFeedPosts(currentUserId).enqueue(new Callback<RequestUserFeed>() {
            @Override
            public void onResponse(@NonNull Call<RequestUserFeed> call, @NonNull Response<RequestUserFeed> response) {
                if (response.code() == 200) {
                    System.out.println("Find Success!!!");

                    RequestUserFeed feed = response.body();
                    if(feed != null){
                        Log.d("MainChatActivity: ", feed.getPost(0).toString() + "\n" + feed.getAccount().getWebsite() + "\n" + feed.getUser().getFull_name());

                    }
                    else {
                        Log.d("MainChatActivity: ", "feed == null");
                    }

                }else {
                    Log.d("MainChatActivity: ", "Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestUserFeed> call, @NonNull Throwable t) {
                Log.d("MainChatActivity: ", "Error: " + t.getMessage());
            }
        });
    }


    private void SendUserToLoginActivity() {
        Intent Intent = new Intent(MainChatActivity2.this, LoginActivity.class);
//        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Intent);
//        finish();
    }

    private void SendUserToSettingsActivity() {
        Intent Intent = new Intent(MainChatActivity2.this, SettingsActivity.class);
        startActivity(Intent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent Intent = new Intent(MainChatActivity2.this, FindFriendsActivity.class);
        startActivity(Intent);
    }





    @Override
    protected void onPause() {
        super.onPause();
        // Save data or state here
        updateLastSeen(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore state or update UI here
        updateLastSeen(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clean up or release resources here
        updateLastSeen(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Perform cleanup tasks here
        updateLastSeen(false);
    }

    private void updateLastSeen(boolean isOnline){

        Map<String, Object> updates = new HashMap<>();

        if (isOnline){
            updates.put("state", "online");
        }
        else{
            updates.put("state", "offline");
        }

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        updates.put("date", saveCurrentDate);

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        updates.put("time", saveCurrentDate);

        userRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Field update successful
                    System.out.println("Fields updated successfully");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Field update failed
                    System.out.println("Fields update failed: " + e.getMessage());
                }
            });
    }


    public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

        private RequestUsersAndAccounts data;

        public FriendsAdapter(RequestUsersAndAccounts usersAndAccounts) {
            data = usersAndAccounts;


            // Fetch user data based on the user IDs and populate the userList
            // You can make a database query or fetch data from your data source (e.g., Firebase Firestore)
            // Populate the userList with the retrieved user data

        }

        // Create ViewHolder for each user item
        @NonNull
        @Override
        public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_display_layout, parent, false);
            return new FriendsAdapter.ViewHolder(view);
        }

        // Bind data to the views in each item
        @Override
        public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
            User user = data.getUser(position);
            UserAccountSettings userAccountSettings = data.getAccount(position);

            holder.userName.setText(user.getUsername());
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

                    Intent intent = new Intent(MainChatActivity2.this, ChatProfileActivity.class);
                    intent.putExtra("userSettings", userSettings);
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
            TextView userName;
            CircleImageView profileImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.user_profile_name);
                profileImage = itemView.findViewById(R.id.users_profile_image);
            }
        }
    }


    public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

        private RequestUsersAndAccounts data;

        public ChatsAdapter(RequestUsersAndAccounts usersAndAccounts) {
            data = usersAndAccounts;

            // Fetch user data based on the user IDs and populate the userList
            // You can make a database query or fetch data from your data source (e.g., Firebase Firestore)
            // Populate the userList with the retrieved user data

        }

        // Create ViewHolder for each user item
        @NonNull
        @Override
        public ChatsAdapter.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
            return new ChatsAdapter.ChatsViewHolder(view);
        }

        // Bind data to the views in each item
        @Override
        public void onBindViewHolder(@NonNull ChatsAdapter.ChatsViewHolder holder, int position) {

            User user = data.getUser(position);
            UserAccountSettings userAccountSettings = data.getAccount(position);

            if (user != null && userAccountSettings != null) {
                holder.userName.setText(user.getUsername());
                holder.userStatus.setText(user.getEmail());
                // Load the profile image using a library like Picasso or Glide
                String profile_photo = userAccountSettings.getProfile_photo();
                System.out.println(">>>>profile_photo = " + profile_photo);

                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
                    System.out.println("profile_photo = " + profile_photo);
//                System.out.println("!profile_photo.isEmpty(): " + profile_photo);
                    Picasso.get().load(profile_photo).into(holder.profileImage);
                } else {
                    holder.profileImage.setImageResource(R.drawable.profile_image);
                }

                holder.userName.setText(user.getFull_name());
                String state = user.getState();
                String date = user.getDate();
                String time = user.getTime();

                if (state.equals("online")) {
                    holder.userStatus.setText(state);
                    holder.onlineIcon.setVisibility(View.VISIBLE);
                } else if (state.equals("offline")) {
                    holder.userStatus.setText("Last Seen: " + date + " " + time);
                    holder.onlineIcon.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("position = " + position);
                        UserSettings receiverUserSettings = new UserSettings(user, userAccountSettings);

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("receiverUserSettings", receiverUserSettings);
                        startActivity(intent);
                    }
                });
            }
            else {
                holder.userStatus.setText("offline");
                holder.onlineIcon.setVisibility(View.GONE);
            }
        }

        // Get the total number of user items
        @Override
        public int getItemCount() {
            return data.size();
        }

        public class  ChatsViewHolder extends RecyclerView.ViewHolder
        {
            CircleImageView profileImage;
            TextView userStatus, userName;
            ImageView onlineIcon;

            public ChatsViewHolder(@NonNull View itemView)
            {
                super(itemView);

                profileImage = itemView.findViewById(R.id.users_profile_image);
                userStatus = itemView.findViewById(R.id.user_status);
                userName = itemView.findViewById(R.id.user_profile_name);
                onlineIcon = itemView.findViewById(R.id.user_online_status);
            }
        }
    }



    public class CustomAutocompleteAdapter extends ArrayAdapter<UserSettings> {

        private List<UserSettings> data;
        private List<UserSettings> originalData; // To store the original unfiltered data
        private LayoutInflater inflater;

        public CustomAutocompleteAdapter(Context context, List<UserSettings> usersAndAccounts) {
            super(context, 0, usersAndAccounts);
            inflater = LayoutInflater.from(context);
            data = new ArrayList<>(usersAndAccounts);
            originalData = new ArrayList<>(usersAndAccounts);

            System.out.println("CustomAutocompleteAdapter - " + data.get(0).getSettings().getProfile_photo());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_autocomplete, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.profileImage = convertView.findViewById(R.id.autocomplete_image);
                viewHolder.textView = convertView.findViewById(R.id.autocomplete_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            UserSettings item = getItem(position);
            if (item != null) {
                String profile_photo = item.getSettings().getProfile_photo();
                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
                    Picasso.get().load(profile_photo).into(viewHolder.profileImage);
                } else {
                    viewHolder.profileImage.setImageResource(R.drawable.profile_image);
                }

                viewHolder.textView.setText(item.getUser().getFull_name());
            }

            return convertView;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        // No constraint, return the original unfiltered data
                        results.values = originalData;
                        results.count = originalData.size();
                    } else {
                        // Filter the data based on the constraint
                        List<UserSettings> filteredItems = new ArrayList<>();

                        for (UserSettings item : originalData) {
                            if (item.getUser().getFull_name().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                filteredItems.add(item);
                            }
                        }

                        results.values = filteredItems;
                        results.count = filteredItems.size();
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    data.clear();

                    if (results.values != null) {
                        data.addAll((List<UserSettings>) results.values);
                    }

                    notifyDataSetChanged();
                }
            };
        }

        private class ViewHolder {
            ImageView profileImage;
            TextView textView;
        }
    }




}

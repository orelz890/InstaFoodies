package Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instafoodies.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


public class ContactsFragment extends Fragment {
    private View ContactsView;
    private RecyclerView myContactsList;

    private DatabaseReference ContacsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    private ContactsAdapter contactsAdapter;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8080";
    private static ServerMethods serverMethods;

    private UserSettings userSettings;
    private String uid;
    private static List<String> friendsUserIds; // List of user IDs you want to display
    private static Context context;


    public ContactsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ContactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        context = getContext();

        System.out.println("getContext() = " + getContext());
        serverMethods = new ServerMethods(getContext());

        myContactsList = (RecyclerView) ContactsView.findViewById(R.id.contact_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        ContacsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);


        return ContactsView;
    }


    public void createFeed(){
        System.out.println("im in onStart createFeed()");

        serverMethods.retrofitInterface.getContactsUsers(currentUserID).enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(@NonNull Call<User[]> call, @NonNull Response<User[]> response) {
                if (response.code() == 200) {
                    System.out.println("Success!!!");

                    User[] users = response.body();
                    if (users != null) {
                        System.out.println("users.get(0).getFull_name() = " + users[0].getFull_name());
                        System.out.println("users.size = " + users.length);

                        serverMethods.retrofitInterface.getContactsSettings(currentUserID).enqueue(new Callback<UserAccountSettings[]>() {
                            @Override
                            public void onResponse(@NonNull Call<UserAccountSettings[]> call, @NonNull Response<UserAccountSettings[]> response) {
                                if (response.code() == 200) {
                                    UserAccountSettings[] usersAccount = response.body();
                                    if (usersAccount != null) {
                                        // Create the adapter and set it to the RecyclerView
                                        contactsAdapter = new ContactsAdapter(users, usersAccount);
                                        myContactsList.setAdapter(contactsAdapter);

                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<UserAccountSettings[]> call, @NonNull Throwable t) {

                            }
                        });
                    } else {
                        System.out.println("users == null");
                    }
                } else {
                    System.out.println("There was an error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User[]> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        System.out.println("im in onStart ContactsFragment");

        createFeed();

    }

    public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

        private List<User> userList;
        private List<UserAccountSettings> settings;

        public ContactsAdapter(User[] my_users, UserAccountSettings[] usersAccountSettings) {
            userList = new ArrayList<>();
            settings = new ArrayList<>();
            for (User user : my_users) {
                userList.add(user);
            }

            for (UserAccountSettings userAccountSettings : usersAccountSettings) {
                settings.add(userAccountSettings);
            }
            System.out.println("userList size = " + userList.size());
            System.out.println("setting size = " + settings.size());

            // Fetch user data based on the user IDs and populate the userList
            // You can make a database query or fetch data from your data source (e.g., Firebase Firestore)
            // Populate the userList with the retrieved user data

        }

        // Create ViewHolder for each user item
        @NonNull
        @Override
        public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
            return new ContactsViewHolder(view);
        }

        // Bind data to the views in each item
        @Override
        public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
            User user = userList.get(position);
            UserAccountSettings userAccountSettings = settings.get(position);

            holder.userName.setText(user.getUsername());
            holder.userStatus.setText(user.getEmail());
            // Load the profile image using a library like Picasso or Glide
            String profile_photo = userAccountSettings.getProfile_photo();
            if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                System.out.println("!profile_photo.isEmpty(): " + profile_photo);
                Picasso.get().load(profile_photo).into(holder.profileImage);
            } else {
                holder.profileImage.setImageResource(R.drawable.profile_image);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("position = " + position);
                    String visit_user_id = friendsUserIds.get(position);
                    UserSettings userSettings = new UserSettings(user, userAccountSettings);

                    Intent intent = new Intent(context, ChatProfileActivity.class);
                    intent.putExtra("userSettings", userSettings);
                    intent.putExtra("visit_user_id", visit_user_id);
                    startActivity(intent);
                }
            });
        }

        // Get the total number of user items
        @Override
        public int getItemCount() {
            return userList.size();
        }

        public class ContactsViewHolder extends RecyclerView.ViewHolder {
            TextView userName, userStatus;
            CircleImageView profileImage;
            ImageView onlineIcon;


            public ContactsViewHolder(@NonNull View itemView) {
                super(itemView);

                userName = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                profileImage = itemView.findViewById(R.id.users_profile_image);
                onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status);
            }
        }
    }
}
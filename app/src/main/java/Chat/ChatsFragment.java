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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Server.RequestUsersAndAccounts;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatsFragment extends Fragment
{

    private static Context mContext;
    private static ServerMethods serverMethods;

    private View PrivateChatsView;
    private RecyclerView chatsList;

    private ChatsAdapter chatsAdapter;

    private DatabaseReference ContactsRef;
    private DatabaseReference ChatsRef;

    private DocumentReference usersDoc;
    private DocumentReference usersAccountDoc;
    private FirebaseAuth mAuth;
    private String currentUserID="";

    public ChatsFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        mContext = getContext();
        serverMethods = new ServerMethods(mContext);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        usersDoc = FirebaseFirestore.getInstance().collection("users").document(currentUserID);
        usersAccountDoc = FirebaseFirestore.getInstance().collection("users_account_settings").document(currentUserID);

        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(mContext));

        return PrivateChatsView;
    }
    @Override
    public void onStart() {
        super.onStart();

        System.out.println("im in onStart ChatsFragment");

        createFeed();

    }

    private void createFeed() {

        ContactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null){
                    serverMethods.retrofitInterface.getContactsUsersAndSettings(currentUserID).enqueue(new Callback<RequestUsersAndAccounts>() {
                        @Override
                        public void onResponse(@NonNull Call<RequestUsersAndAccounts> call, @NonNull Response<RequestUsersAndAccounts> response) {
                            if (response.code() == 200) {
                                System.out.println("Chats Success!!!");

                                RequestUsersAndAccounts usersAndAccounts = response.body();
                                if (usersAndAccounts != null) {
                                    System.out.println("usersAndAccounts.size = " + usersAndAccounts.size());
                                    chatsAdapter = new ChatsAdapter(usersAndAccounts);
                                    chatsList.setAdapter(chatsAdapter);
                                }
                                else {
                                    System.out.println("users == null");
                                }
                            }
                            else {
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
                System.out.println("ChatFragment - createFeed - onCancelled - error:\n" + error.getMessage());
            }
        });
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
            return new ChatsViewHolder(view);
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
//maybe need to chanfe to parcable
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("receiverUserSettings",(Serializable) receiverUserSettings);
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
}

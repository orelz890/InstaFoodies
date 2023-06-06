package Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import Server.RequestsResponse;
import Server.RetrofitInterface;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserAccountSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RequestsActivity extends AppCompatActivity {

    Context mContext;

    private RecyclerView myRequestsList;
    private RequestsAdapter requestsAdapter;

    private DatabaseReference ChatRequestsRef;
    private DatabaseReference ContactsRef;
    FirebaseFirestore db;
    CollectionReference usersCollection;
    CollectionReference usersSettingsCollection;

    private FirebaseAuth mAuth;
    private String currentUserID;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8080";
    private static ServerMethods serverMethods;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        usersSettingsCollection = db.collection("users_account_settings");


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        mContext = this;


        serverMethods = new ServerMethods(mContext);


        myRequestsList = (RecyclerView) findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("im in onStart RequestsFragment");

        createFeed();

    }

    private void createFeed() {
        System.out.println("\n\nRequests - im in createFeed\n\n");
        DatabaseReference getUserRequestRef = ChatRequestsRef.child(currentUserID).getRef();
        getUserRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    serverMethods.retrofitInterface.getRequests(currentUserID).enqueue(new Callback<RequestsResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RequestsResponse> call, @NonNull Response<RequestsResponse> response) {
                            System.out.println("\n\n\n\nresponse.code = " + response.code() + "\n\n\n\n");
                            if (response.code() == 200) {
                                System.out.println("Requests Success!!!");
                                RequestsResponse requestsResponse = response.body();
                                if (requestsResponse != null) {
                                    System.out.println("Full name = " + requestsResponse.getUsers().get(0).getFull_name());
                                    requestsAdapter = new RequestsAdapter(requestsResponse);
                                    myRequestsList.setAdapter(requestsAdapter);


                                }
                            } else {
                                System.out.println("Failed!\nmessage: " + response.message() + "\n\n" + response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<RequestsResponse> call, @NonNull Throwable t) {
                            System.out.println("RequestsFragment - Failure\nMessage: " + t.getMessage() + "\nError: " + Arrays.toString(t.getStackTrace()));
                        }
                    });
                } else {
                    System.out.println("RequestsFragment - snapshot don't exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("RequestsFragment - Cancelled");
            }
        });

    }


    public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

        private RequestsResponse data;

        public RequestsAdapter(RequestsResponse response) {
            this.data = response;

//            System.out.println("RequestsAdapter userList size = " + userList.size());
//            System.out.println("RequestsAdapter setting size = " + settings.size());

            // Fetch user data based on the user IDs and populate the userList
            // You can make a database query or fetch data from your data source (e.g., Firebase Firestore)
            // Populate the userList with the retrieved user data

        }

        // Create ViewHolder for each user item
        @NonNull
        @Override
        public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
            System.out.println("getItemCount = " + getItemCount());
            return new RequestsViewHolder(view);
        }

        // Bind data to the views in each item
        @Override
        public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {
            holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

            System.out.println("Im in RequestFragment onBindViewHolder!");
            System.out.println("My friend full name is: " + data.getUsers().get(position).getFull_name());

            User currentUser = data.getUser(position);
            UserAccountSettings currentAccount = data.getAccount(position);
            String CurrentRequestUid = currentUser.getUser_id();
            final String requestUserName = currentUser.getFull_name();


            System.out.println("getTypeRef.child(" + CurrentRequestUid + ")\n");
            DatabaseReference getTypeRef = ChatRequestsRef.child(currentUserID).child(CurrentRequestUid).child("request_type").getRef();

            getTypeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        String type = snapshot.getValue().toString();
                        System.out.println("type = " + type);
                        if (type.equals("received")) {

                            System.out.println("RequestFragment - onDataChange - received");

                            displayUserSettings(holder, data.getUser(position), data.getAccount(position));


                            holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onClick(View v) {

                                    System.out.println("Pressed Accept");
                                    ContactsRef.child(currentUserID).child(CurrentRequestUid).child("Contact")
                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ContactsRef.child(CurrentRequestUid).child(currentUserID).child("Contact")
                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            System.out.println("saved the two contacts");
                                                                            ChatRequestsRef.child(currentUserID).child(CurrentRequestUid)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                ChatRequestsRef.child(CurrentRequestUid).child(currentUserID)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    System.out.println("Removed the requests");
                                                                                                                    Toast.makeText(mContext, "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                    removeItem(position);
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                }
                            });

                            holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChatRequestsRef.child(currentUserID).child(CurrentRequestUid)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ChatRequestsRef.child(CurrentRequestUid).child(currentUserID)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            removeItem(position);
                                                                            Toast.makeText(mContext, "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                        else if (type.equals("sent")) {

                            Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                            request_sent_btn.setText("Req Sent");

                            holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                            displayUserSettings(holder, currentUser, currentAccount);

                            holder.userName.setText(requestUserName);
                            holder.userStatus.setText("you have sent a request to " + requestUserName);


                            holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChatRequestsRef.child(currentUserID).child(CurrentRequestUid)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ChatRequestsRef.child(CurrentRequestUid).child(currentUserID)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(mContext, "you have cancelled the chat request.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            });
                        }

                    } else {
                        System.out.println("snapshot exist?: " + snapshot.exists() + "\nSnapshot: " + snapshot.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Error Message: " + error.getMessage() + "\nError: " + error.toString());
                }
            });

        }


        // Get the total number of user items
        @Override
        public int getItemCount() {
            return data.size();
        }

        public void removeItem(int position) {
            data.removeItem(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }

        public class RequestsViewHolder extends RecyclerView.ViewHolder {
            TextView userName, userStatus;
            CircleImageView profileImage;
            Button AcceptButton, CancelButton;


            public RequestsViewHolder(@NonNull View itemView) {
                super(itemView);


                userName = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                profileImage = itemView.findViewById(R.id.users_profile_image);
                AcceptButton = itemView.findViewById(R.id.request_accept_btn);
                CancelButton = itemView.findViewById(R.id.request_cancel_btn);
            }
        }

    }

    private void setHolderButtons(RequestsAdapter.RequestsViewHolder holder) {


    }

    private void displayUserSettings(RequestsAdapter.RequestsViewHolder holder, User user, UserAccountSettings settings) {

        if (user != null && settings != null) {
            holder.userName.setText(user.getFull_name());
            holder.userStatus.setText("wants to connect with you.");
            // Load the profile image using a library like Picasso or Glide
            String profile_photo = settings.getProfile_photo();
            if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                System.out.println("!profile_photo.isEmpty(): " + profile_photo);
                Picasso.get().load(profile_photo).into(holder.profileImage);
            } else {
                holder.profileImage.setImageResource(R.drawable.profile_image);
            }
        }
    }

}

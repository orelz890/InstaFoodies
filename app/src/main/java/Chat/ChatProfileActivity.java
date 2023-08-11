package Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import models.UserSettings;

public class ChatProfileActivity extends AppCompatActivity {

    private UserSettings userSettings;
    private String receiverUserID, senderUserID, Current_State;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton, DeclineMessageRequestButton;
    private DatabaseReference UserRef, ChatRequestRef, ContactsRef, NotificationRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);

        mAuth = FirebaseAuth.getInstance();
//        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        // from FindFriendsActivity.java
        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        userSettings = (UserSettings)getIntent().getSerializableExtra("userSettings");
        senderUserID = mAuth.getCurrentUser().getUid();

        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button) findViewById(R.id.decline_message_request_button);

        Current_State = "new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        if (userSettings != null)
        {
            userProfileName.setText(userSettings.getUser().getFull_name());
//            userProfileStatus.setText();

            // Load the profile image using a library like Picasso or Glide
            String profile_photo = userSettings.getSettings().getProfile_photo();
            if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                System.out.println("!profile_photo.isEmpty(): " + profile_photo);
                Picasso.get().load(userSettings.getSettings().getProfile_photo()).placeholder(R.drawable.profile_image).into(userProfileImage);
            }
            else {
                userProfileImage.setImageResource(R.drawable.profile_image);
            }

            ManageChatRequests();
        }
        else {
            Toast.makeText(this, "Couldn't find this user...", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void ManageChatRequests()
    {
        ChatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
              if(dataSnapshot.hasChild(receiverUserID))
              {
                  String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                  if(request_type.equals("sent"))
                  {
                      Current_State = "request_sent";
                      SendMessageRequestButton.setText("Cancel Chat Request");
                  }
                  else if(request_type.equals("received"))
                  {
                      Current_State = "request_received";
                      SendMessageRequestButton.setText("Accept Chat Request");

                      DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                      DeclineMessageRequestButton.setEnabled(true);

                      DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              CancelChatRequest();
                          }
                      });
                  }
              }
              else
              {
                  ContactsRef.child(senderUserID).addValueEventListener(new ValueEventListener()
                  {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                      {
                          if(dataSnapshot.hasChild(receiverUserID))
                          {
                              Current_State = "friends";
                              SendMessageRequestButton.setText("Remove This Contact");
                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
       if(!senderUserID.equals(receiverUserID))
       {
           SendMessageRequestButton.setOnClickListener(new View.OnClickListener()
           {
               @Override
               public void onClick(View view)
               {
                   System.out.println("Current_State = " + Current_State);
                   SendMessageRequestButton.setEnabled(false);
                   switch (Current_State) {
                       case "new":
                           SendChatRequest();
                           break;
                       case "request_sent":
                           CancelChatRequest();
                           break;
                       case "request_received":
                           AcceptChatRequest();
                           break;
                       case "friends":
                           RemoveSpecificContact();
                           break;
                       default:
                           break;
                   }
               }
           });
       }
    }

    private void RemoveSpecificContact()
    {
        System.out.println("im in RemoveSpecificContact()");

        ContactsRef.child(senderUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    ContactsRef.child(receiverUserID).child(senderUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendMessageRequestButton.setEnabled(true);
                                Current_State = "new";
                                SendMessageRequestButton.setText("Send Message");

                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                DeclineMessageRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptChatRequest()
    {
        System.out.println("im in AcceptChatRequest()");

       ContactsRef.child(senderUserID).child(receiverUserID)
               .child("Contacts").setValue("Saved")
               .addOnCompleteListener(new OnCompleteListener<Void>()
       {
           @Override
           public void onComplete(@NonNull Task<Void> task)
           {
               if(task.isSuccessful())
               {
                   ContactsRef.child(receiverUserID).child(senderUserID)
                           .child("Contacts").setValue("Saved")
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       ChatRequestRef.child(senderUserID).child(receiverUserID)
                                               .removeValue()
                                               .addOnCompleteListener(new OnCompleteListener<Void>()
                                               {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task)
                                                   {
                                                       if(task.isSuccessful())
                                                       {
                                                           ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                   .removeValue()
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                   {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task)
                                                                       {
                                                                           SendMessageRequestButton.setEnabled(true);
                                                                           Current_State = "friends";
                                                                           SendMessageRequestButton.setText("Remove This Contact");

                                                                           DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                           DeclineMessageRequestButton.setEnabled(false);
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

    private void SendChatRequest()
    {
        System.out.println("im in SendChatRequest()");

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserID);
                                                chatNotificationMap.put("type", "request");

                                                NotificationRef.child(receiverUserID).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    SendMessageRequestButton.setEnabled(true);
                                                                    Current_State = "request_sent";
                                                                    SendMessageRequestButton.setText("Cancel Chat Request");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else {
                            System.out.println("receiverUserID = " + receiverUserID + ", senderUserID = " + senderUserID);
                        }
                    }
                });
    }

    private void CancelChatRequest()
    {
        System.out.println("im in CancelChatRequest()");

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                SendMessageRequestButton.setText("Send Message");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}

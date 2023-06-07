package Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity
{
    private static Context context;
    private static ServerMethods serverMethods;

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
    private UserSettings receiverUserSettings;
    private UserSettings senderUserSettings;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private MaterialToolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private ProgressDialog loadingBar;


    private String saveCurrentTime, saveCurrentDate;
    private String checker = "",myUrl="";
    private Uri fileUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        serverMethods = new ServerMethods(this);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        receiverUserSettings = (UserSettings)getIntent().getSerializableExtra("receiverUserSettings");
        User user = receiverUserSettings.getUser();
        UserAccountSettings userAccountSettings = receiverUserSettings.getSettings();

        messageReceiverID = user.getUser_id();
        messageReceiverName = user.getUsername();
        messageReceiverImage = userAccountSettings.getProfile_photo();


        InitializeControllers();

        System.out.println("messageReceiverName = " + messageReceiverName);
        userName.setText(messageReceiverName);
        if (!messageReceiverImage.isEmpty() && !Objects.equals(messageReceiverImage, "none")) {
            Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);
        }
        else{
            userImage.setImageResource(R.drawable.profile_image);
        }

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        DisplayLastSeen();

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]
                        {
                               "Images",
                                "Pdf Files",
                                "MS Word Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0)
                        {
                           checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),443);
                        }
                        if(i==1)
                        {
                            checker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select PDF"),443);
                        }
                        if(i==2)
                        {
                            checker = "docx";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Select MSWORD FILE"),443);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void InitializeControllers() {
        mToolbar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatActivity");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        loadingBar = new ProgressDialog(this);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 443 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait, we are sending....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

           fileUri = data.getData();
           if(!checker.equals("image"))
           {
               StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Chat Images and Files");
               final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
               final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

               DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                       .child(messageSenderID).child(messageReceiverID).push();

               final String messagePushID = userMessageKeyRef.getKey();

               final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

               filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if(task.isSuccessful())
                           {
                               Map<String,String> messageTextBody = new HashMap<>();
                               messageTextBody.put("message", myUrl);
                               messageTextBody.put("name",fileUri.getLastPathSegment());
                               if(checker.equals("pdf"))
                               {
                                   messageTextBody.put("type", checker);
                               }
                               else
                               {
                                   messageTextBody.put("type", checker);
                               }

                               messageTextBody.put("from", messageSenderID);
                               messageTextBody.put("to", messageReceiverID);
                               messageTextBody.put("messageID", messagePushID);
                               messageTextBody.put("time", saveCurrentTime);
                               messageTextBody.put("date", saveCurrentDate);

                               Map<String,Object> messageBodyDetails = new HashMap<>();
                               messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                               messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                               RootRef.updateChildren(messageBodyDetails);
                               loadingBar.dismiss();
                           }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                     loadingBar.dismiss();
                     Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                   }
               }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                       double p = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                       loadingBar.setMessage((int)p + "% Uploading...");
                   }
               });
           }
           else if(checker.equals("image"))
           {
               StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Chat Images and Files");
               final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
               final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

               DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                       .child(messageSenderID).child(messageReceiverID).push();

               final String messagePushID = userMessageKeyRef.getKey();

               final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

               uploadTask = filePath.putFile(fileUri);
               uploadTask.continueWithTask(new Continuation() {
                   @Override
                   public Object then(@NonNull Task task) throws Exception {
                       if(!task.isSuccessful())
                       {
                           throw task.getException();
                       }
                       return filePath.getDownloadUrl();
                   }
               }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task) {
                       Uri downloadUrl = task.getResult();
                       myUrl = downloadUrl.toString();

                       Map<String,String> messageTextBody = new HashMap<>();
                       messageTextBody.put("message", myUrl);
                       messageTextBody.put("name",fileUri.getLastPathSegment());
                       messageTextBody.put("type", checker);
                       messageTextBody.put("from", messageSenderID);
                       messageTextBody.put("to", messageReceiverID);
                       messageTextBody.put("messageID", messagePushID);
                       messageTextBody.put("time", saveCurrentTime);
                       messageTextBody.put("date", saveCurrentDate);

                       Map<String,Object> messageBodyDetails = new HashMap<>();
                       messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                       messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                       RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                           @Override
                           public void onComplete(@NonNull Task task)
                           {
                               if (task.isSuccessful())
                               {
                                   Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                               }
                               else
                               {
                                   Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                               }
                               loadingBar.dismiss();
                               MessageInputText.setText("");
                           }
                       });
                   }
               });

           }
           else
           {
               loadingBar.dismiss();
               Toast.makeText(this,"nothing selected,error",Toast.LENGTH_SHORT).show();
           }
        }
    }


    private void DisplayLastSeen()
    {

        serverMethods.retrofitInterface.getUser(messageReceiverID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        String state = user.getState();
                        String date = user.getDate();
                        String time = user.getTime();

                        if (state.equals("online")) {
                            userLastSeen.setText(state);
                        } else if (state.equals("offline")) {
                            userLastSeen.setText("Last Seen: " + date + " " + time);
                        }
                    } else {
                        userLastSeen.setText("offline");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                System.out.println("DisplayLastSeen - onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);
//                         if (!messagesList.contains(messages)){
//                             messagesList.add(messages);
//                             messageAdapter.notifyDataSetChanged();
//                             userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
//                         }
                        // Check if the message ID already exists in the messagesList
                        boolean isDuplicate = false;
                        for (Messages existingMessage : messagesList) {
                            if (existingMessage.getMessageID().equals(messages.getMessageID())) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        // Check if the message is an image message and already exists in the messagesList
                        if (isDuplicate) {
                            return; // Skip adding the duplicate image message
                        }

                        // Add the message to the list
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
                    {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }

    private void SendMessage()
    {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();


            Map<String, String> messageTextBody = new HashMap<>();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }
}

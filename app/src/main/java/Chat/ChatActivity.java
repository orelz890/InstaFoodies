package Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import Home.HomeActivity;
import Login.LoginActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private MaterialToolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private FirebaseUser currentUser;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatUp");

        myViewPager = (ViewPager) findViewById(R.id.chat_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.chat_tabs);
        myTabLayout.setupWithViewPager(myViewPager);


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
//                SendUserToSettingsActivity();
                break;
            case R.id.main_find_friends_option:
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameField = new EditText(ChatActivity.this);
        groupNameField.setHint("e.g Tommy birthday party");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(ChatActivity.this, "Please enter a group name...", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ChatActivity.this, groupName + " group is Created Successfully!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ChatActivity.this, LoginActivity.class);
//        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
//        finish();
    }

//    private void SendUserToSettingsActivity() {
//        Intent loginIntent = new Intent(ChatActivity.this, SettingsActivity.class);
//        startActivity(loginIntent);
//    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 443 && resultCode == RESULT_OK && data != null && data.getData() != null)
//        {
//            loadingBar.setTitle("Sending File");
//            loadingBar.setMessage("Please wait, we are sending....");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();
//
//            fileUri = data.getData();
//            if(!checker.equals("image"))
//            {
//                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
//                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                        .child(messageSenderID).child(messageReceiverID).push();
//
//                final String messagePushID = userMessageKeyRef.getKey();
//
//                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);
//
//                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if(task.isSuccessful())
//                        {
//                            Map messageTextBody = new HashMap();
//                            messageTextBody.put("message", myUrl);
//                            messageTextBody.put("name",fileUri.getLastPathSegment());
//                            if(checker.equals("pdf"))
//                            {
//                                messageTextBody.put("type", checker);
//                            }
//                            else
//                            {
//                                messageTextBody.put("type", checker);
//                            }
//
//                            messageTextBody.put("from", messageSenderID);
//                            messageTextBody.put("to", messageReceiverID);
//                            messageTextBody.put("messageID", messagePushID);
//                            messageTextBody.put("time", saveCurrentTime);
//                            messageTextBody.put("date", saveCurrentDate);
//
//                            Map messageBodyDetails = new HashMap();
//                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//                            RootRef.updateChildren(messageBodyDetails);
//                            loadingBar.dismiss();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        loadingBar.dismiss();
//                        Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        double p = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                        loadingBar.setMessage((int)p + "% Uploading...");
//
//                    }
//                });
//            }
//            else if(checker.equals("image"))
//            {
//                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
//                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                        .child(messageSenderID).child(messageReceiverID).push();
//
//                final String messagePushID = userMessageKeyRef.getKey();
//
//                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
//
//                uploadTask = filePath.putFile(fileUri);
//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//                        if(!task.isSuccessful())
//                        {
//                            throw task.getException();
//                        }
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        Uri downloadUrl = task.getResult();
//                        myUrl = downloadUrl.toString();
//
//                        Map messageTextBody = new HashMap();
//                        messageTextBody.put("message", myUrl);
//                        messageTextBody.put("name",fileUri.getLastPathSegment());
//                        messageTextBody.put("type", checker);
//                        messageTextBody.put("from", messageSenderID);
//                        messageTextBody.put("to", messageReceiverID);
//                        messageTextBody.put("messageID", messagePushID);
//                        messageTextBody.put("time", saveCurrentTime);
//                        messageTextBody.put("date", saveCurrentDate);
//
//                        Map messageBodyDetails = new HashMap();
//                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//                        messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                            @Override
//                            public void onComplete(@NonNull Task task)
//                            {
//                                if (task.isSuccessful())
//                                {
//                                    Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                }
//                                loadingBar.dismiss();
//                                MessageInputText.setText("");
//                            }
//                        });
//                    }
//                });
//
//            }
//            else
//            {
//                loadingBar.dismiss();
//                Toast.makeText(this,"nothing selected,error",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
}

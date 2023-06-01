package Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import Home.HomeActivity;
import Server.RetrofitInterface;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.UserSettings;
import retrofit2.Retrofit;

public class SettingsActivity extends AppCompatActivity {

//    // Server
//    private Retrofit retrofit;
//    private RetrofitInterface retrofitInterface;
//    private String BASE_URL = "http://10.0.2.2:8080";
//    private static ServerMethods serverMethods;

    // Firebase
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImagesRef;

    // Android
    private String currentUserID;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;
    private MaterialToolbar SettingsToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        // Server
//        serverMethods = new ServerMethods(this);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();
        userName.setVisibility(View.INVISIBLE);

//        RetrieveUserInfo();

    }

    private void RetrieveUserInfo(UserSettings userSettings)
    {
        userName.setText(userSettings.getUser().getFull_name());
        userName.setVisibility(View.VISIBLE);
        userStatus.setText(userSettings.getSettings().getWebsite());
        userStatus.setVisibility(View.VISIBLE);


        // Load the profile image using a library like Picasso or Glide
        String profile_photo = userSettings.getSettings().getProfile_photo();
        if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                System.out.println("!profile_photo.isEmpty(): " + profile_photo);
            Picasso.get().load(profile_photo).into(userProfileImage);
        }
        else {
            userProfileImage.setImageResource(R.drawable.profile_image);
        }
    }

    private void SendUserToHomeActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(mainIntent);
    }

    private void InitializeFields()
    {
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);

        loadingBar = new ProgressDialog(this);

        SettingsToolBar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(SettingsToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("im on profile start");
        UserSettings userSettings = (UserSettings)getIntent().getSerializableExtra("userSettings");
        RetrieveUserInfo(userSettings);

    }
}

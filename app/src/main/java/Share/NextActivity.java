package Share;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import Profile.AccountSettingsActivity;
import Utils.ServerMethods;
import models.Photo;
import models.User;
import models.UserAccountSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ViewPager2 viewPager;
    private EditText caption;
    private ImageAdapter adapter;
    private List<Uri> imageUris;
    private ServerMethods serverMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);


        mAuth = FirebaseAuth.getInstance();

        //Log.d(TAG, "onCreate: got the chosen image: " + getIntent().getStringExtra(getString(R.string.selected_images)));
        serverMethods = new ServerMethods(NextActivity.this);
        caption = findViewById(R.id.caption);

        // Initialize the list of image URIs
        imageUris = new ArrayList<>();
        imageUris = getIntent().getParcelableArrayListExtra(getString(R.string.selected_images));

        // Set up the ViewPager
        viewPager = findViewById(R.id.viewPager);
        adapter = new ImageAdapter(imageUris);
        viewPager.setAdapter(adapter);

        ImageView backArrow = findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                finish();
            }
        });


        //Upload Post
        TextView share = findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: share Post");
                HashMap<String, Object> uploadPost = createPost();
                Call<Void> call = serverMethods.retrofitInterface.uploadNewPhoto(mAuth.getCurrentUser().getUid(), uploadPost);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(NextActivity.this,
                                    "Success Upload Post", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(NextActivity.this,
                                    "Upload Post failed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(NextActivity.this, response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(NextActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

    }

    private HashMap<String, Object> createPost() {
        Photo photo = new Photo();
        return photo.PhotoMapForServer(null,caption.toString(), timeStamp(), imageUris, createHash(), mAuth.getCurrentUser().getUid(), getTags(caption.toString()));
    }

    private String getTags(String caption) {
        if (caption.indexOf("#") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = caption.toCharArray();
            boolean foundWord = false;
            for (char c : charArray) {
                if (c == '#') {
                    foundWord = true;
                    sb.append(c);
                } else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }
        return caption;
    }

    private String createHash() {
        return "post_" + UUID.randomUUID().toString();
    }

    private String timeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Israel/Israel")); // Set the timezone to Israel
        return sdf.format(new Date());
    }




}

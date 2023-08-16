package Share;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import Home.HomeActivity;
import Utils.ServerMethods;
import models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView imageCounterTextView;
    private ViewPager2 viewPager;
    private EditText caption;
    private ImageAdapter adapter;
    private List<Uri> imageUris;
    private ServerMethods serverMethods;

    private ProgressDialog loadingBar;
    private StorageTask uploadTask;
    private boolean illegalUserActionPerformed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);



        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(NextActivity.this);


        //Log.d(TAG, "onCreate: got the chosen image: " + getIntent().getStringExtra(getString(R.string.selected_images)));
        serverMethods = new ServerMethods(NextActivity.this);
        caption = findViewById(R.id.caption);

        // Initialize the list of image URIs
        imageUris = new ArrayList<>();
        Intent intent = getIntent();
        imageUris = intent.getParcelableArrayListExtra(getString(R.string.selected_images));
        illegalUserActionPerformed = intent.getExtras().getBoolean("illegalUserActionPerformedFlag");

        // Initialize the image counter
        imageCounterTextView = findViewById(R.id.imageCounterTextView);
        updateImageCounter(0); // Set the initial counter to 0

        // Set up the ViewPager
        viewPager = findViewById(R.id.viewPager);
        adapter = new ImageAdapter(imageUris);
        viewPager.setAdapter(adapter);

        // Add a page change listener to update the image counter when the current page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateImageCounter(position);
            }
        });


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
                if (caption.getText().toString().isEmpty()) {
                    caption.setError("Cannot be Empty");
                    caption.requestFocus();
                }else{
                    uploadImageToStorageAndUploadPost(imageUris);
                }
            }
        });
    }


    private void updateImageCounter(int position) {
        int totalImages = imageUris.size();
        int currentImageIndex = position + 1;
        String counterText = currentImageIndex + "/" + totalImages;
        imageCounterTextView.setText(counterText);
    }


    private void uploadImageToStorageAndUploadPost(List<Uri> imageUris) {
        loadingBar.setTitle("Upload Post");
        loadingBar.setMessage("Uploading....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        String uuid_post = createHash();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("photos_posts");
        List<Task<Uri>> uploadTasks = new ArrayList<>();

        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
            final StorageReference filePath = storageReference.child(mAuth.getCurrentUser().getUid()).child(uuid_post + i + ".jpg");

            uploadTasks.add(filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }));
        }


        Tasks.whenAllComplete(uploadTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if (task.isSuccessful()) {
                    List<String> downloadUrls = new ArrayList<>();

                    for (Task<?> uploadTask : task.getResult()) {
                        if (uploadTask.isSuccessful()) {
                            Uri downloadUri = (Uri) uploadTask.getResult();
                            downloadUrls.add(downloadUri.toString());
                        } else {
                            // Handle individual upload failures
                            Exception exception = uploadTask.getException();
                            Toast.makeText(NextActivity.this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (!downloadUrls.isEmpty()) {
                        // All images uploaded successfully
                        HashMap<String, Object> uploadPost = createPost(uuid_post, downloadUrls);
                        String uid = mAuth.getCurrentUser().getUid();
                        Call<Void> call = serverMethods.retrofitInterface.uploadNewPost(uid, uploadPost);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    if (illegalUserActionPerformed){
                                        // Report him
                                        reportIllegalAction(uid, uuid_post);
                                        Toast.makeText(NextActivity.this, "Post Uploaded: " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        System.out.println("\n\n=============== Don't Report him - legal action ================\n\n");
                                        Toast.makeText(NextActivity.this, "Post Uploaded: " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(NextActivity.this, HomeActivity.class));
                                    }
                                }
                                else {
                                    Toast.makeText(NextActivity.this, "Upload Post failed" + response.message(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Toast.makeText(NextActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else {
                    // Handle task completion failure
                    Toast.makeText(NextActivity.this, "Failed to upload images: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            }
        });
    }

    private void reportIllegalAction(String uid, String uuid_post) {
        serverMethods.retrofitInterface.reportIllegalPost(uid, uuid_post).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("\n\nreportIllegalAction - =============== Reported him ================\n\n");
                    startActivity(new Intent(NextActivity.this, HomeActivity.class));

                }
                else {
                    System.out.println("\n\nreportIllegalAction - =============== Failed to Reported him ================\n\n");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                System.out.println("\n\nreportIllegalAction - onFailure - =============== Failed to Reported him ================\n\n");
            }
        });
    }


    private HashMap<String, Object> createPost(String post_uid, List<String> post_photos) {
        Post post = new Post();
        return post.PostMapForServer(null, caption.getText().toString(), timeStamp(), post_photos, null, null, null, post_uid, mAuth.getCurrentUser().getUid(), getTags(caption.toString()));
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);

        return dateFormat.format(new Date());
    }


}

package Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Chat.ChatsFragment;
import Chat.ContactsFragment;
import Chat.MainChatActivity;
import Chat.MainChatActivity2;
import Chat.MessageAdapter;
import Login.LoginActivity;
import MLKIT.audio.AudioClassificationActivity;
import MLKIT.helpers.AudioHelperActivity;
import MLKIT.helpers.TextHelperActivity;
import MLKIT.image.FaceDetectionActivity;
import MLKIT.image.ImageClassificationActivity;
import MLKIT.object.ObjectDetectionActivity;
import MLKIT.text.SpamTextDetectionActivity;
import MLKIT.image.ImageClassificationActivity;
import Server.RequestUserFeed;
import Utils.BottomNavigationViewHelper;
import Utils.SectionsPagerAdapter;
import Utils.ServerMethods;
import Utils.UniversalImageLoader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {
    // implements MainFeedListAdapter.OnLoadMoreItemsListener
    private static final String TAG = "HomeActivity";
    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;

    // Server
    private static ServerMethods serverMethods;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;

    private ImageView messenger;

    // Post Displaying
    private PostAdapter postAdapter;
    private RecyclerView postList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");

        serverMethods = new ServerMethods(this);
        postList = (RecyclerView) findViewById(R.id.rvPostList);
        postList.setLayoutManager(new LinearLayoutManager(this));


        setupFirebaseAuth();
        setupImageViews();
        setupBottomNavigationView();

        setupMainFeed();

    }

    private void setupMainFeed() {
        System.out.println("im in onStart setupMainFeed()");

        serverMethods.retrofitInterface.getProfileFeedPosts(uid).enqueue(new Callback<RequestUserFeed>() {
            @Override
            public void onResponse(@NonNull Call<RequestUserFeed> call, @NonNull Response<RequestUserFeed> response) {
                if (response.isSuccessful()){
                    System.out.println(TAG + " - setupMainFeed - response.isSuccessful()");
                    RequestUserFeed requestUserFeed = response.body();
                    if (requestUserFeed != null){
                        System.out.println(TAG + " - setupMainFeed - requestUserFeed != null");
                        postAdapter = new PostAdapter(requestUserFeed);
                        postList.setAdapter(postAdapter);
                    }
                    else {
                        System.out.println(TAG + " - setupMainFeed - requestUserFeed == null");
                    }
                }
                else {
                    System.out.println(TAG + " - setupMainFeed - !!response.isSuccessful()");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestUserFeed> call, @NonNull Throwable t) {
                System.out.println(TAG + " - setupMainFeed - onFailure: " + t.getMessage());
            }
        });

    }

    private void setupImageViews() {
        messenger = (ImageView) findViewById(R.id.iv_messenger);
        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainChatActivity2.class);
                startActivity(intent);            }
        });
    }



    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */
    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
        else{
            uid = user.getUid();
        }
    }

    private void updateToken() {
        Log.d(TAG, " updateToken.");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        // Use the device token as needed
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("token", token);

                        FirebaseFirestore.getInstance().collection("users")
                                .document(uid)
                                .update(tokenMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Token updated successfully");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating token", e);
                                    }
                                });

                        Log.d("FCM Token", token);
                    } else {
                        Log.e("FCM Token", "Error getting token: " + task.getException());
                    }
                });
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //check if the user is logged in
            checkCurrentUser(user);
            if (user != null) {
                updateToken();
                // User is signed in
                this.uid = mAuth.getCurrentUser().getUid();
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("im in onStart HomeActivity");

        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());

        setupMainFeed();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}

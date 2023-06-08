package Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
import Profile.ProfileActivity;
import Server.RequestUserFeed;
import Search.SearchActivity;
import Utils.BottomNavigationViewHelper;
import Utils.SectionsPagerAdapter;
import Utils.ServerMethods;
import Utils.UniversalImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Post;
import models.UserAccountSettings;
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

    private BottomNavigationView bottomNavigationView;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestUserFeed requestUserFeed;

    private ImageView messenger;

    // Post Displaying
    private PostAdapter postAdapter;
    private RecyclerView postList;
    private FirebaseFirestore db;

    private UserAccountSettings userAccountSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");

        mAuth = FirebaseAuth.getInstance();
        serverMethods = new ServerMethods(this);
        postList = (RecyclerView) findViewById(R.id.rvPostList);
        postList.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();



//        setupToolbar();
        setupFirebaseAuth();
        setupImageViews();

        setupMainFeed();

        setupBottomNavigationView();


        // Most be after setupMainFeed()
//        setProfileIconInNevigation();

        setSwipeRefresh();

    }

//    private void setupToolbar() {
//        db.collection("users_account_settings")
//                .document(mAuth.getCurrentUser().getUid())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()){
//                            DocumentSnapshot document1 = task.getResult();
//                            if (document1 != null && document1.exists()) {
//                                UserAccountSettings userAccountSettings1 = document1.toObject(UserAccountSettings.class);
//                                assert userAccountSettings1 != null;
//                                userAccountSettings = new UserAccountSettings(userAccountSettings1);
//                                setupBottomNavigationView();
//
//                                MaterialToolbar MToolbar = findViewById(R.id.chat_page_toolbar);
//                                setSupportActionBar(MToolbar);
//
//                                bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
//                                BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
//                                Menu menu = bottomNavigationView.getMenu();
//                                onCreateOptionsMenu(menu);
//                            }
//                            else {
//                                setupBottomNavigationView();
//                            }
//                        }
//                        else{
//                            setupBottomNavigationView();
//                        }
//                    }
//                });
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        System.out.println("onCreateOptionsMenu - im in");
//
////        getMenuInflater().inflate(R.menu.bottom_navigation_menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.ic_android);
//        View view = MenuItemCompat.getActionView(menuItem);
//
//        CircleImageView profileImage = view.findViewById(R.id.profile_custom_icon);
//
//        if (this.requestUserFeed != null) {
//            UserAccountSettings account = this.requestUserFeed.getAccount();
//            if (account != null) {
//                String profile_photo = account.getProfile_photo();
//                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                    System.out.println("onCreateOptionsMenu - success");
//                    Glide.with(this)
//                            .load(profile_photo)  // Provide the resource ID or URL of the new icon
//                            .into(profileImage);
//                }
//                else{
//                    System.out.println("onCreateOptionsMenu - profile_photo.isEmpty() || !profile_photo.equals(none)");
//                }
//            }
//            else{
//                System.out.println("onCreateOptionsMenu - this.requestUserFeed == null");
//
//            }
//        }
//        else{
//            System.out.println("onCreateOptionsMenu - this.requestUserFeed == null");
//        }
//
//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("im the king");
//                Intent intent = new Intent(mContext, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

    private void setSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.HomeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (postAdapter != null) {
                    setupMainFeed();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void shufflePosts(){
        Collections.shuffle(requestUserFeed.getPosts());
    }

    private void setupMainFeed() {
        System.out.println("im in onStart setupMainFeed()");

        serverMethods.retrofitInterface.getProfileFeedPosts(uid).enqueue(new Callback<RequestUserFeed>() {
            @Override
            public void onResponse(@NonNull Call<RequestUserFeed> call, @NonNull Response<RequestUserFeed> response) {
                if (response.isSuccessful()){
                    System.out.println(TAG + " - setupMainFeed - response.isSuccessful()");
                    requestUserFeed = response.body();
//                    setProfileIconInNevigation();


                    if (requestUserFeed != null){
                        System.out.println(TAG + " - setupMainFeed - requestUserFeed != null");
                        shufflePosts();
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
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();

////        getMenuInflater().inflate(R.menu.bottom_navigation_menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.ic_android);
//        View view = MenuItemCompat.getActionView(menuItem);
//
//        CircleImageView profileImage = view.findViewById(R.id.profile_custom_icon);
//
//        if (this.requestUserFeed != null) {
//            UserAccountSettings account = this.requestUserFeed.getAccount();
//            if (account != null) {
//                String profile_photo = account.getProfile_photo();
//                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
//                    System.out.println("onCreateOptionsMenu - success");
//                    Glide.with(this)
//                            .load(profile_photo)  // Provide the resource ID or URL of the new icon
//                            .into(profileImage);
//                }
//                else{
//                    System.out.println("onCreateOptionsMenu - profile_photo.isEmpty() || !profile_photo.equals(none)");
//                }
//            }
//            else{
//                System.out.println("onCreateOptionsMenu - this.requestUserFeed == null");
//
//            }
//        }
//        else{
//            System.out.println("onCreateOptionsMenu - this.requestUserFeed == null");
//        }
//
//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("im the king");
//                Intent intent = new Intent(mContext, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
        MenuItem menuItem2 = menu.getItem(ACTIVITY_NUM);
        menuItem2.setChecked(true);
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

    private void setProfileIconInNevigation() {
        if (this.requestUserFeed != null) {
            UserAccountSettings account = this.requestUserFeed.getAccount();
            if (account != null) {
                String profile_photo = account.getProfile_photo();
                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
                    Menu menu = bottomNavigationView.getMenu();
                    MenuItem menuItem = menu.findItem(R.id.ic_android);

                }
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}

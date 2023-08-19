package Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Chat.MainChatActivity2;
import Login.LoginActivity;
import Server.RequestPosts;
import Server.RequestUserFeed;
import Utils.BottomNavigationViewHelper;
import Utils.ServerMethods;
import models.Post;
import models.Recipe;
import models.User;
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
    private RequestUserFeed userFeed;

    private ImageView messenger;

    // Post Displaying
    private PostAdapter postAdapter;
    private RecyclerView postList;
    private FirebaseFirestore db;

    private UserAccountSettings userAccountSettings;

    RelativeLayout layout_home;


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

        layout_home = (RelativeLayout) findViewById(R.id.RL_home_activity);



//        setupToolbar();
        setupFirebaseAuth();
        setupImageViews();

//        setupMainFeed();

        setupBottomNavigationView();


        // Most be after setupMainFeed()
//        setProfileIconInNevigation();
//        setProfileIconInNavigation();

        setSwipeRefresh();

//        init_database_with_existing_scraped_data();
    }

    // ======================= Don't delete this function!!! ==========================
//    public boolean init_database_with_existing_scraped_data() {
//
//            String copy_rights = "www.allrecipes.com";
//
//            // signUp as user
////            User user = new User("", copy_rights, "", "", "allrecipes","allrecipes");
////            HashMap<String, Object> stringObjectHashMap = user.userMapForServer();
//
////            serverMethods.retrofitInterface.executeSignup(stringObjectHashMap).enqueue(new Callback<User>() {
////                @Override
////                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
////                    if (response.isSuccessful()){
//                        try {
//                            AssetManager assetManager = getAssets();
//                            String[] files = assetManager.list("scraped_data");
//                            for (String f : files) {
//                                String[] files2 = assetManager.list("scraped_data/" + f);
//                                System.out.println(">>>>>>>>>>>>>>> " + f + " <<<<<<<<<<<<<<<<<<<");
//                                for (String f2 : files2) {
//                                    String[] files3 = assetManager.list("scraped_data/" + f + "/" + f2);
//
//    //                        System.out.println(f2);
//                                    for (String f3 : files3) {
//                                        try {
//                                            String file_path = "scraped_data/" + f + "/" + f2 + "/" + f3;
//                                            InputStream inputStream = getAssets().open(file_path);
//                                            int size = inputStream.available();
//                                            byte[] buffer = new byte[size];
//                                            inputStream.read(buffer);
//                                            String json_str = new String(buffer);
//                                            JsonObject jsonObject = new JsonParser().parse(json_str)
//                                                    .getAsJsonObject();
//
//
//                                            Post post = new Post(jsonObject, copy_rights);
//
//                                            System.out.println(post);
//
//
//                                            serverMethods.retrofitInterface.uploadNewPost(copy_rights, post.PostMapForServer()).enqueue(new Callback<Void>() {
//                                                @Override
//                                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                                                    if (response.isSuccessful()) {
//                                                        Recipe recipe = post.getRecipe();
//                                                        System.out.println(recipe.getMain_category() + " - " + recipe.getCategory() + " - " + recipe.getTitle() + " loaded!");
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                                                    Recipe recipe = post.getRecipe();
//                                                    System.out.println(recipe.getMain_category() + " - " + recipe.getCategory() + " - " + recipe.getTitle() + " failure!");
//
//                                                }
//                                            });
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        break;
//                                    }
//                                    System.out.println("\n=====================================\n");
//                                }
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
////                    }
////                }
////
////                @Override
////                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
////                    System.out.println("\n\nonFailure >>>> Problem signup as " + copy_rights + "<<<<\n\n");
////                }
////            });
//
//        return true;
//    }

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
                    if (uid != null &&!uid.isEmpty()){
                        setupMainFeed();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void shufflePosts(){
        Collections.shuffle(userFeed.getPosts());
    }

    private void setupMainFeed() {
        System.out.println("im in onStart setupMainFeed()");

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        serverMethods.retrofitInterface.getProfileFeedPosts(uid).enqueue(new Callback<RequestUserFeed>() {
        serverMethods.retrofitInterface.getUserAndHisFeedPosts(uid).enqueue(new Callback<RequestUserFeed>() {
            @Override
            public void onResponse(@NonNull Call<RequestUserFeed> call, @NonNull Response<RequestUserFeed> response) {
                if (response.isSuccessful()){
                    System.out.println(TAG + " - setupMainFeed - response.isSuccessful()");
                    userFeed = response.body();

                    System.out.println(userFeed);
//                    setProfileIconInNevigation();

                    if (userFeed != null){
                        System.out.println(TAG + " - setupMainFeed - requestUserFeed != null");
                        shufflePosts();
                        postAdapter = new PostAdapter(userFeed, mContext, layout_home);
                        postList.setAdapter(postAdapter);
                    } else {
                        System.out.println(TAG + " - setupMainFeed - requestUserFeed == null");
                    }
                } else {
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
                startActivity(intent);
            }
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
        MenuItem menuItem2 = menu.getItem(ACTIVITY_NUM);
        menuItem2.setChecked(true);
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        } else {
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
    private void setupFirebaseAuth() {
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
                setupMainFeed();
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

        if (uid != null && !uid.isEmpty()){
            setupMainFeed();
        }
    }

    private void setProfileIconInNevigation(int position) {
        if (this.userFeed != null) {
            Post post = userFeed.getPost(position);
            if (post != null) {
                String profile_photo = post.getProfile_photo();
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

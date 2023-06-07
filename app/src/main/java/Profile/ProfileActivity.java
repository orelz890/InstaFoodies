//package Profile;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.example.instafoodies.R;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.button.MaterialButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
//
//import java.util.ArrayList;
//
//import Login.LoginActivity;
//import Server.RequestUserFeed;
//import Utils.BottomNavigationViewHelper;
//import Utils.FirebaseMethods;
//import Utils.GridImageAdapter;
//import Utils.GridImageStringAdapter;
//import Utils.ServerMethods;
//import Utils.UniversalImageLoader;
//import de.hdodenhof.circleimageview.CircleImageView;
//import models.Post;
//import models.User;
//import models.UserAccountSettings;
//import models.UserSettings;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ProfileFragment extends Fragment {
//
//    private static final String TAG = "ProfileFragment";
//
//    public interface OnGridImageSelectedListener{
//        void onGridImageSelected(Post post, int activityNumber);
//    }
//
//    private OnGridImageSelectedListener mOnGridImageSelectedListener;
//    private View view; // Add this line to declare the view variable
//    private static final int ACTIVITY_NUM = 4;
//    private static final int NUM_GRID_COLUMNS = 3;
//
//    //firebase
//    private FirebaseAuth mAuth;
//    private String uid;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private FirebaseMethods mFirebaseMethods;
//    private ServerMethods serverMethods;
//
//
//    //widgets
//    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
//    private ProgressBar mProgressBar;
//    private CircleImageView mProfilePhoto;
//    private ImageView mBackArrow;
//    private GridView gridView;
//    private Toolbar toolbar;
//    private ImageView profileMenu;
//    private BottomNavigationViewEx bottomNavigationView;
//    private Context mContext;
//
//
//    //vars
//    private int mFollowersCount = 0;
//    private int mFollowingCount = 0;
//    private int mPostsCount = 0;
//    private UserSettings mUserSettings;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_view_profile, container, false);
//        mDisplayName = (TextView) view.findViewById(R.id.tv_display_name);
//        mUsername = (TextView) view.findViewById(R.id.profileName);
//        mWebsite = (TextView) view.findViewById(R.id.tv_website);
//        mDescription = (TextView) view.findViewById(R.id.tv_description);
//        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profilePhoto);
//        mPosts = (TextView) view.findViewById(R.id.tvPost);
//        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
//        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
//        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
//        gridView = (GridView) view.findViewById(R.id.gridView);
//        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
//        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
//        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
//        mContext = getActivity();
//
//        try{
//            mUserSettings = getUserFromBundle();
//            init();
//        }catch (NullPointerException e){
//            Log.e(TAG, "onCreateView: NullPointerException: "  + e.getMessage() );
//            Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
//            getActivity().getSupportFragmentManager().popBackStack();
//        }
//
//        mFirebaseMethods = new FirebaseMethods(getActivity());
//        serverMethods = new ServerMethods(mContext);
//        Log.d(TAG, "onCreateView: stared.");
//
//
//        setupBottomNavigationView();
//        setupFirebaseAuth();
//        setupToolbar();
//        isFollowing();
//        getFollowingCount();
//        getFollowersCount();
//        getPostsCount();
//        setupGridView();
//
//        AppCompatButton editProfile = (AppCompatButton) view.findViewById(R.id.textEditProfile);
//        editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
//                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
//                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
//                startActivity(intent);
//            }
//        });
//
////            mFollow.setOnClickListener(new View.OnClickListener() {
////        @Override
////        public void onClick(View v) {
////            Log.d(TAG, "onClick: now following: " + mUser.getUsername());
////
////            FirebaseDatabase.getInstance().getReference()
////                    .child(getString(R.string.dbname_following))
////                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                    .child(mUser.getUser_id())
////                    .child(getString(R.string.field_user_id))
////                    .setValue(mUser.getUser_id());
////
////            FirebaseDatabase.getInstance().getReference()
////                    .child(getString(R.string.dbname_followers))
////                    .child(mUser.getUser_id())
////                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                    .child(getString(R.string.field_user_id))
////                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
////            setFollowing();
////        }
////    });
////
////
////        mUnfollow.setOnClickListener(new View.OnClickListener() {
////        @Override
////        public void onClick(View v) {
////            Log.d(TAG, "onClick: now unfollowing: " + mUser.getUsername());
////
////            FirebaseDatabase.getInstance().getReference()
////                    .child(getString(R.string.dbname_following))
////                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                    .child(mUser.getUser_id())
////                    .removeValue();
////
////            FirebaseDatabase.getInstance().getReference()
////                    .child(getString(R.string.dbname_followers))
////                    .child(mUser.getUser_id())
////                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                    .removeValue();
////            setUnfollowing();
////        }
////    });
////
////    //setupGridView();
////
////
////        editProfile.setOnClickListener(new View.OnClickListener() {
////        @Override
////        public void onClick(View v) {
////            Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
////            Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
////            intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
////            startActivity(intent);
////            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
////        }
////    });
//
//        return view;
//}
//
//    private void init(){
//
//        //set the profile widgets
//        setProfileWidgets(mUserSettings.getUser(),mUserSettings.getSettings());
//
//    }
//
//    private void isFollowing(){
//        Log.d(TAG, "isFollowing: checking if following this users.");
//        setUnfollowing();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference.child(getString(R.string.dbname_following))
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());
//
//                    setFollowing();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        try{
//            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
//        }catch (ClassCastException e){
//            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
//        }
//        super.onAttach(context);
//    }
//
//    private void setupGridView(){
//        Log.d(TAG, "setupGridView: Setting up image grid.");
//
//        System.out.println("\nsetupGridView: Setting up image grid.\n");
//
//        uid = mAuth.getCurrentUser().getUid();
//
//        if (uid != null) {
//            serverMethods.retrofitInterface.getProfileFeedPosts(uid).enqueue(new Callback<RequestUserFeed>() {
//                @Override
//                public void onResponse(@NonNull Call<RequestUserFeed> call, @NonNull Response<RequestUserFeed> response) {
//                    if (response.isSuccessful()){
//                        Log.d(TAG, "setupGridView: success");
//                        System.out.println("setupGridView: success");
//
//                        RequestUserFeed userFeed = response.body();
//                        if (userFeed != null){
//                            System.out.println("userFeed: userFeed.size() =  " + userFeed.size());
//                            //setup our image grid
//                            int gridWidth = getResources().getDisplayMetrics().widthPixels;
//                            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//                            gridView.setColumnWidth(imageWidth);
//
//                            ArrayList<String> imgUrls = new ArrayList<String>();
//                            for(int i = 0; i < userFeed.size(); i++){
//                                imgUrls.add(userFeed.getPost(i).getImage_paths().get(0));
//                                System.out.println("Image (" + i + ") = " + userFeed.getPost(i).getImage_paths().get(0));
//                            }
//
//                            GridImageStringAdapter adapter = new GridImageStringAdapter(getActivity(),R.layout.layout_grid_image_view,
//                                    "", imgUrls);
//                            gridView.setAdapter(adapter);
//                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    mOnGridImageSelectedListener.onGridImageSelected(userFeed.getPost(position), ACTIVITY_NUM);
//                                }
//                            });
//
//                        }
//                        else {
//                            Log.d(TAG, "setupGridView: userFeed == null");
//                            System.out.println("setupGridView: userFeed == null");
//                        }
//
//                    }
//                    else {
//                        Log.d(TAG, "setupGridView: Error: " + response.message());
//                        System.out.println("setupGridView: Error: " + response.message());
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<RequestUserFeed> call, @NonNull Throwable t) {
//                    Log.d(TAG, "setupGridView: Error: " + t.getMessage());
//                    System.out.println("setupGridView: Error: " + t.getMessage());
//                }
//            });
//        }
//
//
//    }
//
//    private void setProfileWidgets(User user, UserAccountSettings userAccountSettings) {
//        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
//        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
//
//        Glide.with(mContext)
//                .load(userAccountSettings.getProfile_photo())
//                .placeholder(R.drawable.ic_android)
//                .error(R.drawable.ic_android)
//                .fitCenter()
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        // Handle load failed
//                        // Remove the progress bar or perform any necessary actions
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        // Handle resource ready
//                        // Remove the progress bar or perform any necessary actions
//                        return false;
//                    }
//                })
//                .into(mProfilePhoto);
//
//
////        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfilePhoto, null, "");
//
//        mDisplayName.setText(user.getFull_name());
//        mUsername.setText(user.getUsername());
//        mWebsite.setText(userAccountSettings.getWebsite());
//        mDescription.setText(userAccountSettings.getDescription());
//        mPosts.setText(String.valueOf(userAccountSettings.getPosts()));
//        mFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));
//        mFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
//        mProgressBar.setVisibility(View.GONE);
//    }
//
//
//    /**
//     * Responsible for setting up the profile toolbar
//     */
//    private void setupToolbar() {
//
//        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);
//
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to account settings.");
//                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//
//    /**
//     * BottomNavigationView setup
//     */
//    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
//        Menu menu = bottomNavigationView.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
//
//      /*
//    ------------------------------------ Firebase ---------------------------------------------
//     */
//
//    /**
//     * Setup the firebase auth object
//     */
//    private void setupFirebaseAuth() {
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//
//                if (user != null) {
//                    // User is signed in
//                    uid = user.getUid();
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid);
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//
//        retrieveData();
//    }
//
//
//    private void retrieveData() {
//        Call<UserSettings> call = serverMethods.retrofitInterface.getBothUserAndHisSettings(mAuth.getCurrentUser().getUid());
//        call.enqueue(new Callback<UserSettings>() {
//            @Override
//            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {
//
//                UserSettings userSettings = response.body();
//                if (response.code() == 200) {
//                    assert userSettings != null;
//                    if(userSettings.getSettings() != null) {
//                        setProfileWidgets(userSettings.getUser(), userSettings.getSettings());
//                    }
//                } else if (response.code() == 400) {
//                    Toast.makeText(mContext,
//                            "Don't exist", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(mContext, response.message(),
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<UserSettings> call, @NonNull Throwable t) {
//                Toast.makeText(mContext, t.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//        retrieveData();
//
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
//}
//

package Profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import Utils.BottomNavigationViewHelper;
import Utils.GridImageAdapter;
import Utils.UniversalImageLoader;
import Utils.ViewPostFragment;
import models.Post;
import models.User;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener {

    private static final String TAG = "ProfileActivity";

    @Override
    public void onGridImageSelected(Post post, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + post.toString());

        // sets arguments to be passed to the fragment
        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.post), post);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }

    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        init();


    }

    private void init(){
//        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
//            if(intent.hasExtra(getString(R.string.intent_user))){
//                User user = intent.getParcelableExtra(getString(R.string.intent_user));
//                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                    Log.d(TAG, "init: inflating view profile");
//                    ViewProfileFragment fragment = new ViewProfileFragment();
//                    Bundle args = new Bundle();
//                    args.putParcelable(getString(R.string.intent_user),
//                            intent.getParcelableExtra(getString(R.string.intent_user)));
//                    fragment.setArguments(args);
//
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.container, fragment);
//                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
//                    transaction.commit();
//                }else{
//                    Log.d(TAG, "init: inflating Profile");
//                    ProfileFragment fragment = new ProfileFragment();
//                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.container, fragment);
//                    transaction.addToBackStack(getString(R.string.profile_fragment));
//                    transaction.commit();
//                }
//            }else{
//                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
//            }

        }else{
            Log.d(TAG, "init: inflating Profile");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }

    }


}
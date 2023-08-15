package Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.Serializable;
import java.util.ArrayList;

import Profile.ProfileActivity;
import Search.SearchActivity;
import Server.RequestUserFeed;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Post;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private OnGridImageSelectedListener mOnGridImageSelectedListener;
    private View view; // Add this line to declare the view variable

    //firebase
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private ServerMethods serverMethods;


    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
    private Dialog mImageDialog;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private AppCompatButton followButton;



    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;
    private UserSettings mUser;
    private UserSettings currentUser;

    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Post post, int activityNumber);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mWebsite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.view_profilePhoto);
        mPosts = (TextView) view.findViewById(R.id.viewTvPost);
        mFollowers = (TextView) view.findViewById(R.id.viewTvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.viewTvFollowing);
        mProgressBar = (ProgressBar) view.findViewById(R.id.viewProfileProgressBar);
        gridView = (GridView) view.findViewById(R.id.viewGridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBarView);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        followButton = (AppCompatButton) view.findViewById(R.id.follow);
        mContext = getActivity();
        serverMethods = new ServerMethods(mContext);
        Log.d(TAG, "onCreateView: stared.");

        try {
            mUser = getUserFromBundle();
            init();
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
            Toast.makeText(mContext, "something went worng", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();


        }
        setupBottomNavigationView();
        setupToolbar();
        setupFirebaseAuth();
        // setupGridView();

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePopup();
            }
        });

        ///////////SERVER FUNACTION WAS NOT BUILD YET/////////////////////
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: send follow or un follow " + mContext.getString(R.string.edit_profile_fragment));
                if(followButton.getText().equals("Follow")){followUnfollowAction(mAuth.getCurrentUser().getUid(),mUser.getUser(),true);}
                else{followUnfollowAction(mAuth.getCurrentUser().getUid(),mUser.getUser(),false);}

            };
        });

        return view;
    }



    private void init(){
        //set the profile widgets
        setProfileWidgets(mUser.getUser(),mUser.getSettings());
        //get the users profile photos
        setupGridView(mUser);
    }

    private void followUnfollowAction(String currentUid, User personTo, Boolean followOrUnfollow){
        //true current wants to follow personTo
        if (followOrUnfollow) {
            Call<Boolean> call = serverMethods.retrofitInterface.followUnfollow
                    (currentUid, personTo.getUser_id(), true);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    if (response.code() == 200) {
                        Toast.makeText(mContext, personTo.getUsername() + "followed seccessfully: " + response.message(), Toast.LENGTH_LONG).show();
                        followButton.setText("Unfollow");
                    } else {
                        Toast.makeText(mContext, "failed following " + personTo.getUsername()
                                + " response code was not valid: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                    Toast.makeText(mContext, "failed following " + personTo.getUsername()
                            + " response failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            //false current wants to unfollow personTo
        }else{
                Call<Boolean> call = serverMethods.retrofitInterface.followUnfollow
                        (currentUid, personTo.getUser_id(), false);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                        if (response.code() == 200) {
                            Toast.makeText(mContext, personTo.getUsername() +"UnFollowed seccessfully: "+response.message(), Toast.LENGTH_LONG).show();
                            followButton.setText("Follow");
                        } else {
                            Toast.makeText(mContext, "failed UnFollowing  "+personTo.getUsername()
                                    +" response code was not valid: "+ response.message(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                        Toast.makeText(mContext, "failed UnFollowing "+personTo.getUsername()
                                +" response failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        }

    }



    private UserSettings getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments: "+ getArguments());
        Bundle bundle =  this.getArguments();
        System.out.println("IN VIEW PROFILE FRAGMENT THE BUNDLE IS "+bundle.toString()+bundle.getParcelable("intent_user"));
        if (bundle != null){
            System.out.println("1111111111111111111111111\n what profile view got from bundle should be both:"+bundle.getParcelable(getString(R.string.intent_user)));
            return bundle.getParcelable(getString(R.string.intent_user));
            }

        else{return null;}


    }

//    private UserSettings getUserFromBundle() {
//        Log.d(TAG, "getUserFromBundle: arguments: " + getArguments());
//        Bundle bundle = this.getArguments();
//        System.out.println("IN VIEW PROFILE FRAGMENT THE BUNDLE IS " + bundle.toString() + bundle.getString("intent_user_json"));
//
//        if (bundle != null) {
//            Gson gson = new Gson();
//            String userJson = bundle.getString(getString(R.string.intent_user));
//            if (userJson != null) {
//                return gson.fromJson(userJson, UserSettings.class);
//            }
//        }
//
//        return null;
//    }


    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }


    private void setupGridView(UserSettings mUser){
        Log.d(TAG, "setupGridView: Setting up image grid.");

        System.out.println("\nsetupGridView: Setting up image grid.\n");

        uid = mUser.getUser().getUser_id();

        if (uid != null) {
            serverMethods.retrofitInterface.getProfileFeedPosts(uid).enqueue(new Callback<RequestUserFeed>() {
                @Override
                public void onResponse(@NonNull Call<RequestUserFeed> call, @NonNull Response<RequestUserFeed> response) {
                    if (response.isSuccessful()){
                        Log.d(TAG, "setupGridView: success");
                        System.out.println("setupGridView: success");

                        RequestUserFeed userFeed = response.body();
                        if (userFeed != null){
                            System.out.println("userFeed: userFeed.size() =  " + userFeed.size());
                            //setup our image grid
                            int gridWidth = getResources().getDisplayMetrics().widthPixels;
                            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                            gridView.setColumnWidth(imageWidth);

                            ArrayList<String> imgUrls = new ArrayList<String>();
                            for(int i = 0; i < userFeed.size(); i++){
                                imgUrls.add(userFeed.getPost(i).getImage_paths().get(0));
                                System.out.println("Image (" + i + ") = " + userFeed.getPost(i).getImage_paths().get(0));
                            }

                            GridImageStringAdapter adapter = new GridImageStringAdapter(getActivity(),R.layout.layout_grid_image_view,
                                    "", imgUrls);
                            gridView.setAdapter(adapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    mOnGridImageSelectedListener.onGridImageSelected(userFeed.getPost(position), ACTIVITY_NUM);
                                }
                            });

                        }
                        else {
                            Log.d(TAG, "setupGridView: userFeed == null");
                            System.out.println("setupGridView: userFeed == null");
                        }

                    }
                    else {
                        Log.d(TAG, "setupGridView: Error: " + response.message());
                        System.out.println("setupGridView: Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RequestUserFeed> call, @NonNull Throwable t) {
                    Log.d(TAG, "setupGridView: Error: " + t.getMessage());
                    System.out.println("setupGridView: Error: " + t.getMessage());
                }
            });
        }


    }





    private void setProfileWidgets(User user, UserAccountSettings userAccountSettings) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());

        Glide.with(mContext)
                .load(userAccountSettings.getProfile_photo())
                .placeholder(R.drawable.ic_android)
                .error(R.drawable.ic_android)
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failed
                        // Remove the progress bar or perform any necessary actions
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Handle resource ready
                        // Remove the progress bar or perform any necessary actions
                        return false;
                    }
                })
                .into(mProfilePhoto);


//        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(user.getFull_name());
        mUsername.setText(user.getUsername());
        mWebsite.setText(userAccountSettings.getWebsite());
        mDescription.setText(userAccountSettings.getDescription());
        mPosts.setText(String.valueOf(userAccountSettings.getPosts()));
        mFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));
        mFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
        mProgressBar.setVisibility(View.GONE);

        //SERVER FUNACITONALITY WAS NOT BUILD YET//////////////


        Call<UserSettings> call = serverMethods.retrofitInterface.getUserSettings(mAuth.getCurrentUser().getUid());
        call.enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.code() == 200) {
                    currentUser  = response.body();
                    if (currentUser != null) {
                        if (currentUser.getSettings().getFollowing_ids().contains(user.getUser_id())) {
                            followButton.setText("Unfollow");
                        } else {
                            followButton.setText("Follow");
                        }
                    }
                }else {
                    Toast.makeText(mContext, "failed to get current user settings code was not valid: "
                            +response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                Toast.makeText(mContext, "failed to get current user settings response failed: "
                        + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });



    }


    /**
     * Responsible for setting up the profile toolbar
     */
    private void setupToolbar() {

        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
        });
    }



    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }



    private void showImagePopup() {
        mImageDialog = new Dialog(mContext);
        mImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mImageDialog.setContentView(R.layout.dialog_image_zoom);
        mImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mImageDialog.setCanceledOnTouchOutside(true);

        ImageView imageView = mImageDialog.findViewById(R.id.zoomImageView);
        imageView.setImageDrawable(mProfilePhoto.getDrawable());

        mImageDialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
       // retrieveData();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


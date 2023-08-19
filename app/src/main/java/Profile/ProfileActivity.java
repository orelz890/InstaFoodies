package Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;

import Utils.BottomNavigationViewHelper;
import Utils.GridImageAdapter;
import Utils.ServerMethods;
import Utils.UniversalImageLoader;
import Utils.ViewPostFragment;
import Utils.ViewProfileFragment;
import models.Post;
import models.User;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener, ViewProfileFragment.OnGridImageSelectedListener {

    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private FirebaseAuth mAuth;
    ServerMethods serverMethods;


    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        serverMethods = new ServerMethods(mContext);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");
        init();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            // If there are more than 1 fragments in the back stack, pop the current fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there's only one fragment in the back stack, finish the activity
            finish();
        }
    }


     private void init(){

        Log.d(TAG, "init: inflating " +"Profile");
        Intent intent = getIntent();
        System.out.println("IN PROFILE ACTIVITY I GOT THE INTENT USER "+intent.toString()+intent.getParcelableExtra("intent_user"));

        if (intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if (intent.hasExtra(getString(R.string.intent_user))){
                UserSettings bundle = intent.getParcelableExtra(getString(R.string.intent_user));
                String bundleUid = bundle.getUser().getUser_id();
                if (!bundleUid.equals(mAuth.getCurrentUser().getUid()))
                {
                    Log.d(TAG, "init: inflating View Profile");
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment,"ViewProfileFragmentTag");
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();}

            }else{
                Toast.makeText(mContext, "something went wrong:", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG, "init: inflating Profile");
            System.out.println("IN PROFILE ACTIVITY ITS THE USER TRING TO SEARCH THEM SELFS");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment,"ProfileFragmentTag");
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();

        }



    }
    @Override
    public void onGridImageSelected(Post post, int activityNumber) {
        /****working*****/
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + post.toString());
        Call<UserSettings> call = serverMethods.retrofitInterface.getBothUserAndHisSettings(post.getUser_id());
        call.enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {
                if (response.code() == 200) {
                    UserSettings userSettings = response.body();
                    assert userSettings != null;
                    if (userSettings.getSettings() != null && userSettings.getUser() !=null) {
                        // sets arguments to be passed to the fragment
                        ViewPostFragment fragment = new ViewPostFragment();
                        Bundle args = new Bundle();
                        args.putParcelable(getString(R.string.post), post);
                        args.putParcelable("postOwnerUserSettings", userSettings);
                        args.putInt(getString(R.string.activity_number), activityNumber);
                        fragment.setArguments(args);

                        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(getString(R.string.view_post_fragment));
                        transaction.commit();
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(mContext,
                            "Don't exist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserSettings> call, @NonNull Throwable t) {
                Toast.makeText(mContext, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        /*****working*****/


//        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + post.toString());
//        Call<UserSettings> call = serverMethods.retrofitInterface.getBothUserAndHisSettings(mAuth.getCurrentUser().getUid());
//        call.enqueue(new Callback<UserSettings>() {
//            @Override
//            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {
//                if (response.code() == 200) {
//                    UserSettings userSettings = response.body();
//                    assert userSettings != null;
//                    if (userSettings.getSettings() != null && userSettings.getUser() !=null) {
//                        // sets arguments to be passed to the fragment
//                        ViewPostFragment fragment = new ViewPostFragment();
//                        Bundle args = new Bundle();
//                        args.putParcelable(getString(R.string.post), post);
//                        args.putParcelable("currentUserSettings", userSettings);
//                        args.putInt(getString(R.string.activity_number), activityNumber);
//                        fragment.setArguments(args);
//
//                        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.container, fragment);
//                        transaction.addToBackStack(getString(R.string.view_post_fragment));
//                        transaction.commit();
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


//        // sets arguments to be passed to the fragment
//        ViewPostFragment fragment = new ViewPostFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(getString(R.string.post), post);
//        args.putInt(getString(R.string.activity_number), activityNumber);
//
//        fragment.setArguments(args);
//
//        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(getString(R.string.view_post_fragment));
//        transaction.commit();


    }





}
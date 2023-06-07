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
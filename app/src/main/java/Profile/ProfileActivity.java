package Profile;

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

import java.util.ArrayList;

import Utils.BottomNavigationViewHelper;
import Utils.GridImageAdapter;
import Utils.UniversalImageLoader;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




//        setupBottomNavigationView();
//        setupToolbar();
//        setupActivityWidgets();
//        setProfileImage();
//        tempGridSetup();

        init();

    }

    private void init(){
        Log.d(TAG,"init: inflating" + "Profile" );
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack("Profile");
        transaction.commit();
    }

//    private void tempGridSetup(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IoLf");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/00142f61e0d46bee6f643945dd51193d.jpg");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/00000324b863b403921c72a2246d32aa.jpg");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/000003a25ffacce67a9b5c8803a7daae.jpg");
//        imgURLs.add("https://www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IoLf");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/00142f61e0d46bee6f643945dd51193d.jpg");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/00000324b863b403921c72a2246d32aa.jpg");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/000003a25ffacce67a9b5c8803a7daae.jpg");
//        imgURLs.add("https://www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IoLf");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/00142f61e0d46bee6f643945dd51193d.jpg");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/00000324b863b403921c72a2246d32aa.jpg");
//        imgURLs.add("https://cdn.mos.cms.futurecdn.net/000003a25ffacce67a9b5c8803a7daae.jpg");
//
//        setupImageGrid(imgURLs);
//    }
//
//    /**
//     * In the future this method will get the images from firebase
//     * @param imgURLs
//     */
//    private void setupImageGrid(ArrayList<String> imgURLs){
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//
//        // Distribute the grid width evenly
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(imageWidth);
//
//        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_image_view,
//                "", imgURLs);
//        gridView.setAdapter(adapter);
//    }
//
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: setting profile photo.");
//        String imageURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IoLf";
//        UniversalImageLoader.setImage(imageURL, profilePhoto, mProgressBar, "https://");
//    }
//
//    private void setupActivityWidgets(){
//        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
//    }
//    private void setupToolbar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
//        setSupportActionBar(toolbar);
//
//        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
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
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
//        Menu menu = bottomNavigationView.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
//
}
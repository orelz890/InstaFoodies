package Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import Utils.BottomNavigationViewHelper;
import Utils.SectionPagerAdapter;
import Utils.UniversalImageLoader;


public class MainActivity extends AppCompatActivity {
// implements MainFeedListAdapter.OnLoadMoreItemsListener
    private static final String TAG = "MainActivity";
    private Context mContext = MainActivity.this;
    private static final int ACTIVITY_NUM = 0;

    //firebase
//    private  FirebaseAuth mAuth;
//    private FirebaseeAuth.AuthStateListnerr mAuthLisnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");

       // setupFirebaseAuth();
        InitImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }


/*
---------------------------------------------Firebase------------------------------------
 */
//
//    private void checkCurrentUSer(FirebaseUser user){
//        log.d(TAG, "checkCurrentUSer: checking if user is logged in.");
//        if(user == null){
//            Intent intent = new Intent(mContext, LoginActivity.class);
//            startActivity(intent);
//        }
//    }
//    private void setupFirebaseAuth(){
//        log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//        mAuth = FirebaseAuth.getInstance();
//        mAuthLisnter = new FirebaseAuth.AuthStateLisntener(){
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                 checkCurrentUser(user);
//                if(user != null){
//                    //user is signd in
//                    log.d(TAG, "on AtuhStatrChanged: signed in!" + user.getUid());
//                }else{
//                    //user is signd out
//                    log.d(TAG, "onAothStateChanged_ signed out");
//                }
//            }
//        };
//    }
//    @Override
//    public void onStart(){
//        super.onStart();
//        mAuth...
//    }
//    @Override
//    public void onStop(){
//        super.onStop();
//        if(mAuthLisntener != null){
//            mAuth...
//        }
//    }
//-----------------------------------------------------------------------------------
    private void InitImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */

    private void setupViewPager(){
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); // index 0
        adapter.addFragment(new HomeFragment()); // index 1
        adapter.addFragment(new MessagesFragment()); // index 2
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        // Container for all fragments
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // setting the icons
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instafoodies_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_send);

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

}

package Utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import Likes.LikesActivity;
import Home.MainActivity;
import Profile.ProfileActivity;
import com.example.instafoodies.R;
import Search.SearchActivity;
import Share.ShareActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHelper";

//    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationView){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        bottomNavigationView.enableAnimation(false);
//        bottomNavigationView.enableItemShiftingMode(false);
//        bottomNavigationView.enableShiftingMode(false);
//        bottomNavigationView.setTextVisibility(false);
//    }

//    public static void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//    }

    public static void enableNavigation(final Context context, BottomNavigationView view){
        Log.d(TAG, "enableNavigation: enabling navigation");
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, MainActivity.class);// ACTIVITY_NUM 0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_search:
                        Intent intent2 = new Intent(context, SearchActivity.class);// ACTIVITY_NUM 1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_circle:
                        Intent intent3 = new Intent(context, ShareActivity.class);// ACTIVITY_NUM 2
                        context.startActivity(intent3);
                        break;

                    case R.id.ic_alert:
                        Intent intent4 = new Intent(context, LikesActivity.class);// ACTIVITY_NUM 3
                        context.startActivity(intent4);
                        break;

                    case R.id.ic_android:
                        Intent intent5 = new Intent(context, ProfileActivity.class);// ACTIVITY_NUM 4
                        context.startActivity(intent5);
                        break;
                }
                return false;
            }
        });
    }
}

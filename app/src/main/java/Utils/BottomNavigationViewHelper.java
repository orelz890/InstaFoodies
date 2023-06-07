package Utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import Likes.LikesActivity;
import Home.HomeActivity;
import Profile.ProfileActivity;

import com.example.instafoodies.R;

import Search.SearchActivity;
import Share.ShareActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHelper";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }


    public static void enableNavigation(final Context context, BottomNavigationView view) {
        Log.d(TAG, "enableNavigation: enabling navigation");
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, HomeActivity.class);// ACTIVITY_NUM 0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_search:
                        Intent intent2 = new Intent(context, SearchActivity.class);// ACTIVITY_NUM 1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_circle:
                        // Show the popup menu for ic_circle selection
                        PopupMenu popupMenu = new PopupMenu(context, view.findViewById(R.id.ic_circle));
                        popupMenu.getMenuInflater().inflate(R.menu.menu_share, popupMenu.getMenu());

                        // Set item click listener for the popup menu
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                // Handle menu item clicks here
                                switch (item.getItemId()) {
                                    case R.id.post_item:
                                        // Handle menu item 1 click
                                        Intent intent3 = new Intent(context, ShareActivity.class);// ACTIVITY_NUM 2
                                        // 0 == Recipe post
                                        intent3.putExtra("key", 0);
                                        context.startActivity(intent3);
                                        return true;
                                    case R.id.recipe_item:
                                        // Handle menu item 2 click
                                        Intent intent6 = new Intent(context, ShareActivity.class);
                                        //1 == post
                                        intent6.putExtra("key", 1);
                                        context.startActivity(intent6);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });

                        // Show the popup menu
                        popupMenu.show();
                        return true;

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

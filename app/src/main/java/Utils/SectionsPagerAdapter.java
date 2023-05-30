package Utils;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.lifecycle.Lifecycle;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SectionsPagerAdapter extends FragmentStateAdapter {
//
//    private final List<Fragment> mFragmentList = new ArrayList<>();
//
//    public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
//        super(fragmentManager,lifecycle);
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return mFragmentList.get(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mFragmentList.size();
//    }
//
//    public void addFragment(Fragment fragment) {
//        mFragmentList.add(fragment);
//    }
//}


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that stores fragments for tabs
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "SectionsPagerAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }

}
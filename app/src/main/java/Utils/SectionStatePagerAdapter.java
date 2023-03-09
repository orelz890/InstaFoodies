package Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();
    public SectionStatePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        mFragmentList.add(fragment);
        int position = mFragmentList.size() - 1;
        mFragments.put(fragment, position);
        mFragmentNumbers.put(fragmentName, position);
        mFragmentNames.put(position, fragmentName);
    }

    /**
     * Returns the fragment with the name @param
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName){
        if (mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        }
        return null;
    }


    /**
     * Returns the fragment with the name @param
     * @param fragment
     * @return
     */
    public Integer getFragmentNumber(Fragment fragment){
        if (mFragments.containsKey(fragment)){
            return mFragments.get(fragment);
        }
        return null;
    }

    /**
     * Returns the name of the fragment in @param position
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        }
        return null;
    }

}

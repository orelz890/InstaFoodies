package Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new ChatsFragment());
        fragments.add(new GroupsFragment());
        fragments.add(new ContactsFragment());
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    public void resetFragment(int position) {
        Fragment fragment = fragments.get(position);
        // Reset the fragment here
        if (fragment instanceof GroupsFragment){
            GroupsFragment groupsFragment = (GroupsFragment) fragment;
            groupsFragment.refreshFragment();
            fragments.set(position, groupsFragment);
        }

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            default:
                return null;

        }
    }

}

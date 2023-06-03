package Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        switch (i)
        {
            case 0:
                System.out.println("\n\n" + getPageTitle(0) + "\n\n");
                return new ChatsFragment();

            case 1:
                System.out.println("\n\n" + getPageTitle(1) + "\n\n");
                return new GroupsFragment();

            case 2:
                System.out.println("\n\n" + getPageTitle(2) + "\n\n");
                return new ContactsFragment();

            case 3:
                System.out.println("\n\n" + getPageTitle(3) + "\n\n");
                return new RequestsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            case 3:
                return "Requests";

            default:
                return null;

        }
    }
}
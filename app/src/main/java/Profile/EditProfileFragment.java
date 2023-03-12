package Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instafoodies.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

import Utils.UniversalImageLoader;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private ImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mProfilePhoto = (ImageView) view.findViewById(R.id.profilePhoto);

        setProfileImage();
        setupImageViews(view);
        return view;
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile image");
        String imageURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IoLf";
        UniversalImageLoader.setImage(imageURL, mProfilePhoto, null, "https://");

    }

    private void setupImageViews(View view){
        // Back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'.");
                requireActivity().finish();
            }
        });
    }

}

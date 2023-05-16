package Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Objects;

import Login.LoginActivity;
import Utils.FirebaseMethods;
import Utils.ServerMethods;
import Utils.UniversalImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment  {

    private static final String TAG = "EditProfileFragment";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods FirebaseMethods;
    private ServerMethods serverMethods;
    private String userID;


    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;


    //vars
    private UserSettings mUserSettings;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profilePhoto);
        mDisplayName = (EditText) view.findViewById(R.id.etDisplayName);
        mUsername = (EditText) view.findViewById(R.id.etUserName);
        mUsername.setEnabled(false);
        mWebsite = (EditText) view.findViewById(R.id.etWebsite);
        mDescription = (EditText) view.findViewById(R.id.etDescription);
        mEmail = (EditText) view.findViewById(R.id.etEmail);
        mEmail.setEnabled(false);
        mPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        FirebaseMethods = new FirebaseMethods(getActivity());
        serverMethods = new ServerMethods(getActivity());


        //setProfileImage();
        setupFirebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });

        return view;
    }


    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure the username chosen is unique
     */
    private void saveProfileSettings(){
        HashMap<String, Object> updatedUser = new HashMap<>();
        HashMap<String, Object> updatedUserAccountSettings = new HashMap<>();

        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
        final String full_name = mDisplayName.getText().toString();

        if(Long.parseLong(mUserSettings.getUser().getPhone_number()) != phoneNumber){
            updatedUser.put("phone_number", phoneNumber);
        }
        if(!mUserSettings.getUser().getFull_name().equals(full_name)){
            updatedUser.put("full_name", full_name);
        }

        if(!mUserSettings.getSettings().getWebsite().equals(website)){
            //update website
            updatedUserAccountSettings.put("website", website);
        }
        if(!mUserSettings.getSettings().getDescription().equals(description)){
            //update description
            updatedUserAccountSettings.put("description", description);
        }

        Call<Void> call = serverMethods.retrofitInterface.executePatchUserAccountSettings(updatedUserAccountSettings);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Updated UserAccountSettings: " + mAuth.getCurrentUser().getEmail(),
                            Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Wrong Credentials: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        Call<Void> call1 = serverMethods.retrofitInterface.executePatchUser(updatedUser);

        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Updated User: " + mAuth.getCurrentUser().getEmail(),
                            Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Wrong Credentials: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }



    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUser().getEmail());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUser().getPhone_number());

        mUserSettings = userSettings;
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(mUserSettings.getUser().getFull_name());
        mUsername.setText(mUserSettings.getUser().getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));


    }
       /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    userID = user.getUid();
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        Call<UserSettings> call = serverMethods.retrofitInterface.getUserSettings(mAuth.getUid());

        call.enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {

                UserSettings result = response.body();
                if (response.code() == 200) {
                    assert result != null;
                    setProfileWidgets(result);
                } else if (response.code() == 400) {
                    Toast.makeText(getContext(),
                            "Don't exist", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserSettings> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

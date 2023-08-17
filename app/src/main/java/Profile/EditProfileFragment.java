package Profile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instafoodies.R;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import Search.SearchActivity;
import Share.NextRecipeActivity;
import Utils.FirebaseMethods;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods FirebaseMethods;
    private ServerMethods serverMethods;
    private String userID;
    private ActivityResultLauncher<Intent> launcher;


    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;


    //vars
    private UserSettings mUserSettings;
    private ProgressBar mProgressBar;
    private Context mContext;

    private ProgressDialog loadingBar;
    private StorageTask uploadTask;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.edit_profilePhoto);
        mDisplayName = (EditText) view.findViewById(R.id.etDisplayName);
        mUsername = (EditText) view.findViewById(R.id.etUserName);
        mUsername.setEnabled(false);
        mWebsite = (EditText) view.findViewById(R.id.etWebsite);
        mDescription = (EditText) view.findViewById(R.id.etDescription);
        mEmail = (EditText) view.findViewById(R.id.etEmail);
        mEmail.setEnabled(false);
        mPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        mProgressBar = (ProgressBar) view.findViewById(R.id.edit_profileProgressBar);

        FirebaseMethods = new FirebaseMethods(getActivity());
        serverMethods = new ServerMethods(getActivity());
        mContext = getActivity();

        loadingBar = new ProgressDialog(getActivity());
        //setProfileImage();
        setupFirebaseAuth();
        changeProfile();


        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Change Profile photo");
                changeProfile();

            }
        });

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                requireActivity().finish();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        showImageDialog(imageUri);  // Add the imageUri to the list
                    }
                }
            } else if (result.getResultCode() == com.github.drjacky.imagepicker.ImagePicker.RESULT_ERROR) {
                // Use Utils.ImagePicker.Companion.getError(result.getData()) to show an error
            }
        });
    }

    private void changeProfile() {
        mChangeProfilePhoto.setOnClickListener(view -> {
            try {

                com.github.drjacky.imagepicker.ImagePicker.Companion.with(requireActivity())
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .cropFreeStyle()
                        .cropSquare()
                        .setMultipleAllowed(true)
                        .bothCameraGallery()
                        .maxResultSize(1080, 1080, true)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .provider(ImageProvider.BOTH)
                        .createIntentFromDialog((Function1<Intent, Unit>) intent -> {
                            launcher.launch(intent);
                            return Unit.INSTANCE;
                        });
            } catch (Exception ignored) {

            }
        });

    }


    private void showImageDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.preview_dialog, null);

        ImageView imageView = dialogView.findViewById(R.id.imageView);
        TextView textView = dialogView.findViewById(R.id.title_dialog);
        AppCompatButton btnChange = dialogView.findViewById(R.id.btnChange);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btnCancel);


        // Load and display the selected image
        Glide.with(mContext)
                .load(imageUri)
                .placeholder(R.drawable.ic_android)
                .error(R.drawable.ic_android)
                .centerInside()
                .into(imageView);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToStorage(imageUri, dialog);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void uploadImageToStorage(Uri imageUri, Dialog dialog) {

        loadingBar.setTitle("Updating profile image");
        loadingBar.setMessage("Updating....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto");
        final StorageReference filePath = storageReference.child(mAuth.getCurrentUser().getUid()).child("photo_profile" + "." + "jpg");

        uploadTask = filePath.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    Call<Void> call = serverMethods.retrofitInterface.uploadProfilePhoto(mAuth.getCurrentUser().getUid(), downloadUrl.toString());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Change Profile Image: " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                //retrieveData();
                                dialog.dismiss();
                                //TODO need to check if it work appropriate ->>
                                Intent intent = new Intent(getActivity(), ProfileFragment.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Wrong Change Profile Image: " + response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
                dialog.dismiss();
            }
        });
    }


    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure the username chosen is unique
     */
    private void saveProfileSettings() {
        HashMap<String, Object> updatedUser = new HashMap<>();
        HashMap<String, Object> updatedUserAccountSettings = new HashMap<>();

        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String full_name = mDisplayName.getText().toString();
        final String phoneNumber = mPhoneNumber.getText().toString();

        if (!(mUserSettings.getUser().getPhone_number().equals(phoneNumber))) {
            //update phone_number
            updatedUser.put("phone_number", mPhoneNumber.getText().toString());
        }
        if (!mUserSettings.getUser().getFull_name().equals(full_name)) {
            //update full_name
            updatedUser.put("full_name", full_name);
        }

        if (!mUserSettings.getSettings().getWebsite().equals(website)) {
            //update website
            updatedUserAccountSettings.put("website", website);
        }
        if (!mUserSettings.getSettings().getDescription().equals(description)) {
            //update description
            updatedUserAccountSettings.put("description", description);
        }

        Call<Void> call = serverMethods.retrofitInterface.executePatchUser(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), updatedUser);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Updated User: " + mAuth.getCurrentUser().getEmail(),
                            Toast.LENGTH_LONG).show();

                    Call<Void> call2 = serverMethods.retrofitInterface.patchUserAccountSettings(mAuth.getUid(), updatedUserAccountSettings);

                    call2.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                            if (response.code() == 200) {
                                Toast.makeText(getContext(), "Updated UserAccountSettings: " + mAuth.getCurrentUser().getEmail(),
                                        Toast.LENGTH_LONG).show();
                                requireActivity().finish();
                            } else if (response.code() == 404) {
                                Toast.makeText(getContext(), "Wrong Credentials: " + response.message(),
                                        Toast.LENGTH_LONG).show();
                            } else {
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
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Wrong Credentials: " + response.message(),
                            Toast.LENGTH_LONG).show();
                } else {
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


    private void setProfileWidgets(User user, UserAccountSettings userAccountSettings) {
        Glide.with(mContext)
                .load(userAccountSettings.getProfile_photo())
                .placeholder(R.drawable.ic_android)
                .error(R.drawable.ic_android)
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failed
                        // Remove the progress bar or perform any necessary actions
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Handle resource ready
                        // Remove the progress bar or perform any necessary actions
                        return false;
                    }
                })
                .into(mProfilePhoto);

//        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(user.getFull_name());
        mUsername.setText(user.getUsername());
        mWebsite.setText(userAccountSettings.getWebsite());
        mDescription.setText(userAccountSettings.getDescription());
        mEmail.setText(user.getEmail());
        mPhoneNumber.setText(String.valueOf(user.getPhone_number()));

        mProgressBar.setVisibility(View.GONE);
    }
       /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
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
        retrieveData();

//        Call<UserSettings> call = serverMethods.retrofitInterface.getUserSettings(mAuth.getUid());
//
//        call.enqueue(new Callback<UserSettings>() {
//            @Override
//            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {
//
//                UserSettings result = response.body();
//                if (response.code() == 200) {
//                    assert result != null;
//                    setProfileWidgets(result);
//                } else if (response.code() == 400) {
//                    Toast.makeText(getContext(),
//                            "Don't exist", Toast.LENGTH_LONG).show();
//                }
//                else {
//                    Toast.makeText(getContext(), response.message(),
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<UserSettings> call, @NonNull Throwable t) {
//                Toast.makeText(getContext(), t.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });


    }


    private void retrieveData() {

        Call<UserSettings> call = serverMethods.retrofitInterface.getBothUserAndHisSettings(mAuth.getCurrentUser().getUid());
        call.enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {

                UserSettings userSettings = response.body();
                if (response.code() == 200) {
                    assert userSettings != null;
                    if(userSettings.getSettings() != null) {
                        setProfileWidgets(userSettings.getUser(), userSettings.getSettings());
                        mUserSettings = new UserSettings(userSettings.getUser(), userSettings.getSettings());
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(mContext,
                            "Don't exist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserSettings> call, @NonNull Throwable t) {
                Toast.makeText(mContext, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

//    private void animationNavigate(){
//            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();;
//
//            // Set custom animations
//            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out);
//
//            // Replace the fragment container with the second fragment
//            fragmentTransaction.replace(R.id.SecondFragment, new ProfileFragment());
//
//            // Optional: Add the transaction to the back stack
//            fragmentTransaction.addToBackStack(null);
//
//            fragmentTransaction.commit();
//    }

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

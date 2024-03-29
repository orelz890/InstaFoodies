package Profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

import Server.RequestPosts;
import Utils.BottomNavigationViewHelper;
import Utils.FirebaseMethods;
import Utils.GridImageSelection;
import Utils.ServerMethods;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Post;
import models.Recipe;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
//
//    public interface OnGridImageSelectedListener{
//        void onGridImageSelected(Photo photo, int activityNumber);
//    }
//    OnGridImageSelectedListener mOnGridImageSelectedListener;


    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Post post, int activityNumber);
    }

    private OnGridImageSelectedListener mOnGridImageSelectedListener;
    private View view; // Add this line to declare the view variable
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private Context mContext;

    //firebase
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private ServerMethods serverMethods;
    private List<Post> mSelectedItems; // To track selected items


    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription, emptyGrid, myCart, myPosts, myLikedPosts;
    private ImageView profileMenu, delete, exportCart, ivChef;
    private CircleImageView mProfilePhoto;
    private ProgressBar mProgressBar, gridLoadingProgressBar;
    private Toolbar toolbar;
    private GridView gridView;
    private GridImageSelection adapter;

    private Dialog mImageDialog;
    private BottomNavigationViewEx bottomNavigationView;
    private List<Integer> selectedIndexes = new ArrayList<>();
    private List<Recipe> selectedToCart = new ArrayList<>();
    private List<String> selectedToDelete = new ArrayList<>();


    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;
    private UserSettings mCurrentUserSettings = null;

    RequestPosts userFeed;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupProfileFragmentFields();
        setupBottomNavigationView();
        setupToolbar();
        if (mCurrentUserSettings != null) {
            setProfileWidgets(mCurrentUserSettings.getUser(), mCurrentUserSettings.getSettings());
            setupGridViewByOption("myPosts");
        } else {
            setupFirebaseAuth();
            setupGridViewByOption("myPosts");
        }

        setupOnClickListeners();
        return view;
    }

    private void setupProfileFragmentFields() {
        mAuth= FirebaseAuth.getInstance();
        mAuth= FirebaseAuth.getInstance();
        mDisplayName = (TextView) view.findViewById(R.id.tv_display_name);
        mUsername = (TextView) view.findViewById(R.id.profileName);
        mWebsite = (TextView) view.findViewById(R.id.tv_website);
        mDescription = (TextView) view.findViewById(R.id.tv_description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profilePhoto);
        mPosts = (TextView) view.findViewById(R.id.tvPost);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        myCart = (TextView) view.findViewById(R.id.myCart);
        myPosts = (TextView) view.findViewById(R.id.myPosts);
        myLikedPosts = (TextView) view.findViewById(R.id.myLikedPosts);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridViewProfile);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        emptyGrid = view.findViewById(R.id.emptyGrid);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        serverMethods = new ServerMethods(mContext);
        delete = view.findViewById(R.id.deleteButton);
        exportCart = view.findViewById(R.id.exportButton);
        gridLoadingProgressBar = view.findViewById(R.id.gridLoadingProgressBar);
        ivChef = view.findViewById(R.id.ivChef);

        Log.d(TAG, "onCreateView: stared.");
    }

    private void setupOnClickListeners() {
        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePopup();
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGridViewByOption("myPosts");
                if (adapter != null) {
                    adapter.clearImgURLs();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        myCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGridViewByOption("myCart");
                if (adapter != null){
                    adapter.clearImgURLs();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        myLikedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGridViewByOption("myLikedPosts");
                if (adapter != null) {
                    adapter.clearImgURLs();
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void exportCartList(RequestPosts myCart) {
        if (!(adapter.getSelectedIndexes().isEmpty())) {
            for (int index : adapter.getSelectedIndexes()) {
                Post selectedPost = myCart.getPost(index);
                if (selectedPost.getRecipe() != null) {
                    selectedToCart.add(selectedPost.getRecipe());
                }
            }
            sendCartListViaWhatsApp();
        }
    }

    private void sendCartListViaWhatsApp() {
        String list = String.valueOf(createIngredientList());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, list);
        intent.setType("text/plain");
        try {
            startActivity(intent);
        } catch (Exception exception) {
            Toast.makeText(getActivity(), "There Is No Application That Support This Action",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private StringBuilder createIngredientList() {
        StringBuilder ans = new StringBuilder();
        try {
            ArrayList<String> ingredients = new ArrayList<>();
            ArrayList<Integer> amounts = new ArrayList<>();
            ArrayList<String> titles = new ArrayList<>();
            for (Recipe recipe : selectedToCart) {
                titles.add(recipe.getTitle());
                List<String> r_Ingre = recipe.getIngredients();
                for (String ingredient : r_Ingre) {
                    if (contain(ingredients, (ingredient.split(":")[1]))) {
                        amounts.set(ingredients.indexOf(ingredient.split(":")[1]), amounts.get(ingredients.indexOf(ingredient.split(":")[1])) + Integer.parseInt(ingredient.split(":")[0]));
                    } else {
                        amounts.add(Integer.parseInt(ingredient.split(":")[0]));
                        ingredients.add(ingredient.split(":")[1]);
                    }
                }

            }
            ans.append("*Your Weekly Plan Recipes:*\n");
            for (String title : titles) {
                ans.append(title).append("\n");

            }
            ans.append("\n*Your Grocery List:*\n");
            for (int i = 0; i < amounts.size(); i++) {
                ans.append(amounts.get(i)).append("[g]").append(" ").append(ingredients.get(i)).append("\n");
            }
        } catch (Exception e) {
            System.out.println(e);
            Toast.makeText(getActivity(), "You need to get the new recipes' version",
                    Toast.LENGTH_SHORT).show();
        }


        return ans;
    }

    private boolean contain(ArrayList<String> ingredients, String s) {
        for (String ingredient : ingredients) {
            if (ingredient.equals(s)) {
                return true;
            }
        }
        return false;
    }


    private void deleteAllSelectedPosts(RequestPosts myPosts) {
        uid = mAuth.getCurrentUser().getUid();

        if (!adapter.getSelectedIndexes().isEmpty()) {
            for (int index : adapter.getSelectedIndexes()) {
                String selectedPost_ids = myPosts.getPost(index).getPost_id();
                selectedToDelete.add(selectedPost_ids);

            }
        }
//        String[] temp = new String[selectedToDelete.size()];
//        int i = 0;
//        for (String id: selectedToDelete) {
//            temp[i++] = id;
//        }
//        HashMap<Integer, String> indexPostIdMap = new HashMap<>();
//        for (int i = 0; i < selectedToDelete.size(); i++) {
//            indexPostIdMap.put(i, selectedToDelete.get(i));
//        }

        if (uid != null) {
            serverMethods.retrofitInterface.deleteProfilePosts(uid, selectedToDelete).enqueue(new Callback<Boolean>() {
                //            serverMethods.retrofitInterface.deleteProfilePosts(uid, indexPostIdMap).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Boolean ans = response.body();
                        if (Boolean.TRUE.equals(ans)) {
                            setupGridViewByOption("myPosts");
                            int currentPosts = Integer.parseInt(mPosts.getText().toString());
                            int newPosts = currentPosts - selectedToDelete.size();
                            mPosts.setText(String.valueOf(newPosts));
                        } else {
                            Log.d(TAG, "Delete Profile Posts Failed Try Again:");
                            Toast.makeText(getActivity(), "Delete Profile Posts Failed Try Again", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(TAG, "Delete Profile Posts: Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                    Log.d(TAG, "Delete Profile Posts: Error: " + t.getMessage());
                }
            });
        }
    }

    public void setCurrentUserSettings(UserSettings userSettings) {
        this.mCurrentUserSettings = userSettings;
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

    private void setupGridViewByOption(String option) {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        System.out.println("\nsetupGridView: Setting up image grid.\n");

        changeButtonsColor(option);


        switch (option) {
            case "myPosts":
                emptyGrid.setVisibility(View.INVISIBLE);
                gridView.setVisibility(View.VISIBLE);
                exportCart.setVisibility(View.GONE);
                gridLoadingProgressBar.setVisibility(View.VISIBLE);
                GridViewByPosts();
                break;
            case "myCart":
                emptyGrid.setVisibility(View.INVISIBLE);
                gridView.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                gridLoadingProgressBar.setVisibility(View.VISIBLE);
                GridViewByCart();
                break;
            case "myLikedPosts":
                emptyGrid.setVisibility(View.INVISIBLE);
                gridView.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                exportCart.setVisibility(View.GONE);
                gridLoadingProgressBar.setVisibility(View.VISIBLE);
                GridViewByLiked();
                break;
        }
    }

    private void changeButtonsColor(String option) {
        myPosts.setTextColor(Color.parseColor("#808080"));
        myCart.setTextColor(Color.parseColor("#808080"));
        myLikedPosts.setTextColor(Color.parseColor("#808080"));
        switch (option) {
            case "myPosts":
                myPosts.setTextColor(Color.parseColor("#1790D1"));
                break;
            case "myCart":
                myCart.setTextColor(Color.parseColor("#1790D1"));
                break;
            case "myLikedPosts":
                myLikedPosts.setTextColor(Color.parseColor("#1790D1"));
                break;
        }

    }

    private void GridViewByPosts() {



        uid = mAuth.getCurrentUser().getUid();

        if (uid != null) {
            serverMethods.retrofitInterface.getProfileFeedPosts(uid).enqueue(new Callback<RequestPosts>() {
                @Override
                public void onResponse(@NonNull Call<RequestPosts> call, @NonNull Response<RequestPosts> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "setupGridView: success");
                        System.out.println("setupGridView: success");

                        userFeed = response.body();
                        if (userFeed != null && userFeed.size() > 0) {
                            System.out.println("userFeed: userFeed.size() =  " + userFeed.size());
                            //setup our image grid
                            gridLoadingProgressBar.setVisibility(View.GONE);
                            int gridWidth = getResources().getDisplayMetrics().widthPixels;
                            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                            gridView.setColumnWidth(imageWidth);

                            ArrayList<String> imgUrls = new ArrayList<String>();
                            for (int i = 0; i < userFeed.size(); i++) {
                                imgUrls.add(userFeed.getPost(i).getImage_paths().get(0));
                                System.out.println("Image (" + i + ") = " + userFeed.getPost(i).getImage_paths().get(0));
                            }

                            adapter = new GridImageSelection(getActivity(), R.layout.layout_grid_image_view,
                                    "", imgUrls, gridView);
                            gridView.setAdapter(adapter);
                            // Set up the GridView
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (adapter.getSelectedIndexes().size() > 0) {
                                        adapter.toggleSelection(position); // Toggle selection
                                    } else {
                                        mOnGridImageSelectedListener.onGridImageSelected(userFeed.getPost(position), ACTIVITY_NUM);
                                    }
                                    if (adapter.getSelectedIndexes().isEmpty()) {
                                        delete.setVisibility(View.INVISIBLE);
                                        exportCart.setVisibility(View.INVISIBLE);
                                    }
                                }
//                                    mOnGridImageSelectedListener.onGridImageSelected(userFeed.getPost(position), ACTIVITY_NUM);
                            });
                            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    adapter.selectionMode(position);
                                    delete.setVisibility(View.VISIBLE);
                                    return true;
                                }
                            });
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteAllSelectedPosts(userFeed);
                                }
                            });
                        } else {
                            gridLoadingProgressBar.setVisibility(View.GONE);
                            showEmptyGridMessage();
                            Log.d(TAG, "setupGridView: userFeed == null");
                            System.out.println("setupGridView: userFeed == null");
                        }

                    } else {
                        showEmptyGridMessage();
                        Log.d(TAG, "setupGridView: Error: " + response.message());
                        System.out.println("setupGridView: Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RequestPosts> call, @NonNull Throwable t) {
                    Log.d(TAG, "setupGridView: Error: " + t.getMessage());
                    System.out.println("setupGridView: Error: " + t.getMessage());
                }
            });
        }

    }

    private void GridViewByCart() {
        uid = mAuth.getCurrentUser().getUid();

        if (uid != null) {
            serverMethods.retrofitInterface.getCartPosts(uid).enqueue(new Callback<RequestPosts>() {
                @Override
                public void onResponse(@NonNull Call<RequestPosts> call, @NonNull Response<RequestPosts> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "setupGridView: success");
                        System.out.println("setupGridView: success");

                        RequestPosts myCart = response.body();
                        if (myCart != null && myCart.size() > 0) {
                            System.out.println("myCart: myCart.size() =  " + myCart.size());
                            //setup our image grid
                            gridLoadingProgressBar.setVisibility(View.GONE);
                            int gridWidth = getResources().getDisplayMetrics().widthPixels;
                            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                            gridView.setColumnWidth(imageWidth);

                            ArrayList<String> imgUrls = new ArrayList<String>();
                            for (int i = myCart.size() -1; i >= 0; i--) {
                                imgUrls.add(myCart.getPost(i).getImage_paths().get(0));
                                System.out.println("Image (" + i + ") = " + myCart.getPost(i).getImage_paths().get(0));
                            }

                            // Set up the GridView
                            adapter = new GridImageSelection(getActivity(), R.layout.layout_grid_image_view,
                                    "", imgUrls, gridView);
                            gridView.setAdapter(adapter);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (adapter.getSelectedIndexes().size() > 0) {
                                        adapter.toggleSelection(position); // Toggle selection
                                    } else {
                                        mOnGridImageSelectedListener.onGridImageSelected(myCart.getPost(position), ACTIVITY_NUM);
                                    }
                                    if (adapter.getSelectedIndexes().isEmpty()) {
                                        delete.setVisibility(View.INVISIBLE);
                                        exportCart.setVisibility(View.INVISIBLE);
                                    }
                                }
//                                    mOnGridImageSelectedListener.onGridImageSelected(userFeed.getPost(position), ACTIVITY_NUM);
                            });
                            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    adapter.selectionMode(position);
                                    exportCart.setVisibility(View.VISIBLE);
                                    return true;
                                }
                            });
                            exportCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    exportCartList(myCart);
                                }
                            });
                        } else {
                            gridLoadingProgressBar.setVisibility(View.GONE);
                            showEmptyGridMessage();
                            Log.d(TAG, "setupGridView: myCart == null");
                            System.out.println("setupGridView: myCart == null");
                        }

                    } else {
                        showEmptyGridMessage();
                        Log.d(TAG, "setupGridView: Error: " + response.message());
                        System.out.println("setupGridView: Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RequestPosts> call, @NonNull Throwable t) {
                    Log.d(TAG, "setupGridView: Error: " + t.getMessage());
                    System.out.println("setupGridView: Error: " + t.getMessage());
                }
            });
        }

    }

    private void GridViewByLiked() {
        uid = mAuth.getCurrentUser().getUid();
        if (uid != null) {
            serverMethods.retrofitInterface.getLikedPosts(uid).enqueue(new Callback<RequestPosts>() {
                @Override
                public void onResponse(@NonNull Call<RequestPosts> call, @NonNull Response<RequestPosts> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "setupGridView: success");
                        System.out.println("setupGridView: success");
                        RequestPosts myLikedPosts = response.body();
                        if (myLikedPosts != null && myLikedPosts.size() > 0) {
                            System.out.println("myLikedPosts: myLikedPosts.size() =  " + myLikedPosts.size());
                            gridLoadingProgressBar.setVisibility(View.GONE);
                            //setup our image grid
                            int gridWidth = getResources().getDisplayMetrics().widthPixels;
                            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                            gridView.setColumnWidth(imageWidth);

                            ArrayList<String> imgUrls = new ArrayList<String>();
                            for (int i = myLikedPosts.size() -1; i >=0; i--) {
                                imgUrls.add(myLikedPosts.getPost(i).getImage_paths().get(0));
                                System.out.println("Image (" + i + ") = " + myLikedPosts.getPost(i).getImage_paths().get(0));
                            }
                            // Set up the GridView
                            adapter = new GridImageSelection(getActivity(), R.layout.layout_grid_image_view,
                                    "", imgUrls, gridView);
                            gridView.setAdapter(adapter);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (adapter.getSelectedIndexes().size() > 0) {
                                        adapter.toggleSelection(position); // Toggle selection
                                    } else {
                                        mOnGridImageSelectedListener.onGridImageSelected(myLikedPosts.getPost(( myLikedPosts.size() -1 ) - position), ACTIVITY_NUM);
                                    }
                                }
                            });
                            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    adapter.selectionMode(position);
                                    return true;
                                }
                            });
                        } else {
                            gridLoadingProgressBar.setVisibility(View.GONE);
                            showEmptyGridMessage();
                            Log.d(TAG, "setupGridView: Error: " + response.message());
                            System.out.println("setupGridView: Error: " + response.message());
                        }
                    } else {
                        showEmptyGridMessage();
                        Log.d(TAG, "setupGridView: myLikedPosts == null");
                        System.out.println("setupGridView: myLikedPosts == null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RequestPosts> call, @NonNull Throwable t) {
                    Log.d(TAG, "setupGridView: Error: " + t.getMessage());
                    System.out.println("setupGridView: Error: " + t.getMessage());
                }
            });
        }

    }

    private void showEmptyGridMessage() {
        emptyGrid.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);
    }


//    @Override
//    public void onAttach(Context context) {
//        try{
//            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
//        }catch (ClassCastException e){
//            Log.e(TAG, "onAttach: ClassCastException: "+e.getMessage());
//        }
//        super.onAttach(context);
//    }


    private void setProfileWidgets(User user, UserAccountSettings userAccountSettings) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());

        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;

            if (activity.isDestroyed()) {
                // The activity associated with the context has been destroyed
            } else {
                // The activity associated with the context is still active
                if (userAccountSettings.getIsBusiness()){
                    ivChef.setVisibility(View.VISIBLE);
                }
                else {
                    ivChef.setVisibility(View.INVISIBLE);
                }

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


//        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfilePhoto, null, "");

                mDisplayName.setText(user.getFull_name());
                mUsername.setText(user.getUsername());

                // Convert the link text to clickable links
                Linkify.addLinks(mWebsite, Linkify.WEB_URLS);

                mWebsite.setText(userAccountSettings.getWebsite());
                mDescription.setText(userAccountSettings.getDescription());
                mPosts.setText(String.valueOf(userAccountSettings.getPosts()));
                mFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));
                mFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
                mProgressBar.setVisibility(View.GONE);

                // Set an OnClickListener to handle link clicks
                mWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = ((TextView) v).getText().toString();
                        openWebPage(url);
                    }
                });
            }
        }

    }

    // Method to open a web page using an Intent
    private void openWebPage(String url) {
        if (!url.startsWith("https://")){
            url = "https://" + url;
        }
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }


    /**
     * Responsible for setting up the profile toolbar
     */
    private void setupToolbar() {

        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
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
                    // User is signed in
                    uid = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        retrieveData();
    }


    private void retrieveData() {
        Call<UserSettings> call = serverMethods.retrofitInterface.getBothUserAndHisSettings(mAuth.getCurrentUser().getUid());
        call.enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {

                UserSettings userSettings = response.body();
                mCurrentUserSettings = userSettings;
                if (response.code() == 200) {
                    assert userSettings != null;
                    if (userSettings.getSettings() != null) {
                        setProfileWidgets(userSettings.getUser(), userSettings.getSettings());
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

    private void showImagePopup() {
        mImageDialog = new Dialog(mContext);
        mImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mImageDialog.setContentView(R.layout.dialog_image_zoom);
        mImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mImageDialog.setCanceledOnTouchOutside(true);

        ImageView imageView = mImageDialog.findViewById(R.id.zoomImageView);
        imageView.setImageDrawable(mProfilePhoto.getDrawable());

        mImageDialog.show();
    }





    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        retrieveData();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


    }


}

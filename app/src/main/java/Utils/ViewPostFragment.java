
package Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import Home.CommentsAdapter;
import Home.PostAdapter;
import Profile.ProfileActivity;
import Share.ImageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Comment;
import models.Like;
import models.Post;
import models.Recipe;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";


    public interface OnCommentThreadSelectedListener {
        void onCommentThreadSelectedListener(Post post);
    }

    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    // View pager - photos
    private ViewPager2 viewPager;
    private StringImageAdapter adapter;

    ServerMethods serverMethods = new ServerMethods(getActivity());
    private CommentsAdapter commentsAdapter;
    private RecyclerView commentsRecyclerView;
    private Runnable refreshRunnable;
    private PopupWindow popupWindow;
    private Handler handler;
    private int currentPosition;
    private static final long REFRESH_INTERVAL = 3000; // 3 seconds
    private int postMaxLine = 5;
    private String uid;
    private RelativeLayout layout;


    private BottomNavigationView bottomNavigationView;
    public TextView username, imageLikes, postCaption, imageCommentsLink, postTimePosted;
    public CircleImageView profilePhoto;
    private TextView imageCounterTextView;
    public ImageView ivEllipses, imageHeartRed, imageHeart, commentsBubble, imageAddCart, imageAddToCartFill, imageShare;
    ;
    public ViewPager2 postImages;
    public View view;


    //vars
    private Post post;
    private int mActivityNumber;
    private UserSettings postOwnerUserSettings;
    private User postOwnerUser;
    private UserSettings mCurrentUserSettings;
    private String photoUsername = "";
    private String profilePhotoUrl = "";
    private GestureDetector mGestureDetector;

//    private Heart mHeart;

    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";
    private User mCurrentUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_post, container, false);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);

        profilePhoto = view.findViewById(R.id.view_post_profile_photo);
        username = view.findViewById(R.id.view_post_username);

        username = (TextView) view.findViewById(R.id.view_post_username); // <<<
        profilePhoto = (CircleImageView) view.findViewById(R.id.view_post_profile_photo); // <<<
        ivEllipses = (ImageView) view.findViewById(R.id.view_post_ivEllipses);
        postImages = (ViewPager2) view.findViewById(R.id.view_post_post_images_viewpager); // <<<< ///
        imageHeartRed = (ImageView) view.findViewById(R.id.view_post_image_heart_red);
        imageHeart = (ImageView) view.findViewById(R.id.view_post_image_heart);
        imageShare = (ImageView) view.findViewById(R.id.view_post_image_share);
        commentsBubble = (ImageView) view.findViewById(R.id.view_post_speech_bubble);
        imageLikes = (TextView) view.findViewById(R.id.view_post_image_likes);
        postCaption = (TextView) view.findViewById(R.id.view_post_post_caption); // <<<<
        imageCommentsLink = (TextView) view.findViewById(R.id.view_post_image_comments_link);
        postTimePosted = (TextView) view.findViewById(R.id.view_post_post_time_posted); // <<<<
        imageAddCart = view.findViewById(R.id.view_post_add_cart);
        imageAddToCartFill = view.findViewById(R.id.view_post_add_to_cart_fill);
        // Initialize the image counter
        imageCounterTextView = view.findViewById(R.id.imageCounterTextView);

        mAuth = FirebaseAuth.getInstance();
        init();
        setupBottomNavigationView();

        return view;
    }

    private void init() {
        try {
            layout = (RelativeLayout) view.findViewById(R.id.main_view_post);
            uid = mAuth.getCurrentUser().getUid();
            //Get the post from the bundle
            post = getPostFromBundle();
            //Get the current activity number
            mActivityNumber = getActivityNumFromBundle();
            // Get the owner of the post from the bundle
            postOwnerUserSettings = getUserSettingsFromBundle();
/**************************working******************************/
            // Set up the ViewPager
//            adapter = new StringImageAdapter(post.getImage_paths());
//            postImages.setAdapter(adapter);
//
////             Set up the widgets
//            setProfileWidgets(postOwnerUserSettings.getUser(), postOwnerUserSettings.getSettings());
/**************************working******************************/

            // Add a page change listener to update the image counter when the current page changes
            postImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    updateImageCounter(position);
                }
            });
            updateImageCounter(0); // Set the initial counter to 0


            User user = postOwnerUserSettings.getUser();
            UserAccountSettings userAccountSettings = postOwnerUserSettings.getSettings();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            if (post.getRecipe() == null) {
                imageAddCart.setVisibility(View.INVISIBLE);
            }

            // Set the user info & photo
            if (userAccountSettings != null) {
                // Set photo
                String profile_photo = userAccountSettings.getProfile_photo();
                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
                    Picasso.get().load(profile_photo).into(profilePhoto);
                } else {
                    profilePhoto.setImageResource(R.drawable.profile_image);
                }

                // Set username
                username.setText(user.getUsername());
            } else {
                System.out.println("PostAdapter - onBindViewHolder - userAccountSettings == null");
            }


            // Set the visibility of the add_to_cart ImageView based on whether the post is a recipe
            if (post.getRecipe() != null) {
                imageAddCart.setVisibility(View.VISIBLE);
            } else {
                imageAddCart.setVisibility(View.GONE);
            }
            // Set he post pictures
            List<String> image_paths = post.getImage_paths();
            if (image_paths != null && !image_paths.isEmpty()) {
                adapter = new StringImageAdapter(image_paths);
                postImages.setAdapter(adapter);
            }

            // Set the post caption
            postCaption.setText(getTruncatedCaption(captionsForPostOrRecipe(post)));
            postCaption.setOnClickListener(new View.OnClickListener() {
                boolean expanded = false; // Track if content is expanded

                @Override
                public void onClick(View v) {
                    if (expanded) {
                        postCaption.setText(getTruncatedCaption(captionsForPostOrRecipe(post))); // Set truncated caption
                    } else {
                        String[] spilt = captionsForPostOrRecipe(post).split("\n");
                        System.out.println("in post adapter: " + spilt.length);
                        if (spilt.length > postMaxLine) {
                            String tempSetText = captionsForPostOrRecipe(post) + "\n See less";
                            postCaption.setText(tempSetText); // Set full caption with see less
                        } else {
                            postCaption.setText(captionsForPostOrRecipe(post)); // Set full caption
                        }
                        postCaption.setText(captionsForPostOrRecipe(post)); // Set full caption
                    }
                    expanded = !expanded; // Toggle expanded state
                }
            });


            // Set time
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm", Locale.ENGLISH);
            try {
                String inputDateString = post.getDate_created();
                Date date = inputDateFormat.parse(inputDateString);
                String formattedDate = outputDateFormat.format(date);

                postTimePosted.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Set how much likes the post has & the heart color
            if (post.getRecipe() == null) {
                imageLikes.setText(String.format("%s Likes", post.getLikesCount()));
            } else {
                imageLikes.setText(String.format("Recipe: " + post.getRecipe().getTitle() + " has %s Likes", post.getLikesCount()));
            }

            List<String> liked = post.getLiked_list();
            if (liked != null && liked.contains(mAuth.getCurrentUser().getUid())) {
                imageHeart.setImageResource(R.drawable.heart_red);
            } else {
                imageHeart.setImageResource(R.drawable.heart);
            }

            imageHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (imageHeart.getDrawable().getConstantState().equals(getActivity().getResources().getDrawable(R.drawable.heart).getConstantState())) {
                        imageHeart.setImageResource(R.drawable.heart_red);
                    } else {
                        imageHeart.setImageResource(R.drawable.heart);
                    }
                    updatePostLiked(uid, post);
                }
            });

            List<String> cart_list = post.getCart_list();
            if (cart_list != null && cart_list.contains(uid)) {
                imageAddCart.setImageResource(R.drawable.added_to_cart);
            } else {
                imageAddCart.setImageResource(R.drawable.add_to_cart);
            }

            imageAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (imageAddCart.getDrawable().getConstantState().equals(getActivity().getResources().getDrawable(R.drawable.add_to_cart).getConstantState())) {
                        imageAddCart.setImageResource(R.drawable.added_to_cart);


                    } else {
                        imageAddCart.setImageResource(R.drawable.add_to_cart);
                    }
                    updateCartPost(uid, post);
                }
            });

            profilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //navigate to profile activity
                    if (!mAuth.getCurrentUser().getUid().equals(post.getUser_id())) {
                        Intent intent = new Intent(getActivity(), (ProfileActivity.class));
                        intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                        intent.putExtra(getString(R.string.intent_user), (Parcelable) postOwnerUserSettings);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getActivity(), "You are already on your profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareText = createPostShareText(post.getFull_name(), captionsForPostOrRecipe(post), postTimePosted.getText().toString());
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    sendIntent.setType("text/plain");
                    try {
                        getActivity().startActivity(sendIntent);
                        //maybe need to change for    mContext.startActivity(Intent.createChooser(sendIntent, "Share via"));
                        //for better user experience
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "No apps can perform this action.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            commentsBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Call<UserSettings> call = serverMethods.retrofitInterface.getBothUserAndHisSettings(uid);
                    call.enqueue(new Callback<UserSettings>() {
                        @Override
                        public void onResponse(@NonNull Call<UserSettings> call, @NonNull Response<UserSettings> response) {
                            if (response.code() == 200) {
                                UserSettings userSettings = response.body();
                                assert userSettings != null;
                                if (userSettings.getSettings() != null && userSettings.getUser() != null) {

                                    createPopupCommentsWindow(userSettings);
                                }
                            } else if (response.code() == 400) {
                                Toast.makeText(getActivity(),
                                        "Don't exist", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), response.message(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UserSettings> call, @NonNull Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });


        } catch (
                NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

    }


    private void updateImageCounter(int position) {
        int totalImages = post.getImage_paths().size();
        int currentImageIndex = position + 1;
        String counterText = currentImageIndex + "/" + totalImages;
        imageCounterTextView.setText(counterText);
    }

    private String createPostShareText(String userName, String caption, String timeStamp) {
        String shareText = "@InstaFoodies - We Love Food\n\n" + "Shared " + userName + "'s" + " Post";
        shareText += "\n\n" + caption.split("See less...")[0] + "\n\n" + timeStamp;
        for (int i = 0; i < post.getImage_paths().size(); i++) {
            shareText += post.getImage_paths().get(i) + "\n\n";
        }
        return shareText;
    }


    private void createPopupCommentsWindow(UserSettings currentUserSettings) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View popupView = inflater.inflate(R.layout.custom_popup_comments_window, null);


        // Find views in the popupView
        EditText etNewComment = popupView.findViewById(R.id.et_new_comment_text);
        CircleImageView profileImage = popupView.findViewById(R.id.user_profile_image);
        ImageView sendButton = popupView.findViewById(R.id.iv_send);

        // setup RecyclerView
        commentsRecyclerView = popupView.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // Set user photo
        Picasso.get().load(currentUserSettings.getSettings().getProfile_photo()).placeholder(R.drawable.profile_image).into(profileImage);

        // Setup popup abilities
        setupSendMessageButton(sendButton, etNewComment, currentUserSettings);

        // Setup main comment feed
        setupCommentsMainFeed();


        // Get the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (getActivity() != null) {
            WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;


        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, screenWidth, screenHeight / 2, focusable);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);

                // Initialize the handler
                handler = new Handler();

                // Define the refresh runnable
                refreshRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // Perform your refresh logic here
                        refreshPopupContent();
                        // Schedule the runnable to run again after the refresh interval
                        handler.postDelayed(this, REFRESH_INTERVAL);
                    }
                };

                // Start the initial refresh
                handler.post(refreshRunnable);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissPopupWindow();
            }
        });
    }

    private void dismissPopupWindow() {
        // Dismiss the popupWindow
        popupWindow.dismiss();
        // Remove the runnable when the popupWindow is dismissed
        handler.removeCallbacks(refreshRunnable);
    }

    private void refreshPopupContent() {
        // Implement your refresh logic for the popup content here
        // This method will be called every 3 seconds while the popup is displayed
        setupCommentsMainFeed();

    }

    private void setupCommentsMainFeed() {
        serverMethods.retrofitInterface.getPostComments(post.getUser_id(), post.getPost_id()).enqueue(new Callback<Comment[]>() {
            @Override
            public void onResponse(@NonNull Call<Comment[]> call, @NonNull Response<Comment[]> response) {
                if (response.code() == 200) {
                    System.out.println("PostAdapter - setupCommentsMainFeed - Success");
                    Comment[] comments = response.body();
                    if (comments != null && comments.length > 0) {
                        System.out.println("comment = " + comments[0].getComment());
                        commentsAdapter = new CommentsAdapter(comments, getActivity(), post.getUser_id(), post.getPost_id(), serverMethods);
                        commentsRecyclerView.setAdapter(commentsAdapter);
                        // Scroll to the last item in the list
                        int lastPosition = comments.length - 1;
                        if (lastPosition >= 0) {
                            commentsRecyclerView.scrollToPosition(lastPosition);
                        }
                    }
                } else {
                    System.out.println("PostAdapter - setupCommentsMainFeed - Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Comment[]> call, @NonNull Throwable t) {
                System.out.println("PostAdapter - setupCommentsMainFeed - onFailure - " + t.getMessage());

            }
        });

    }

    private String captionsForPostOrRecipe(Post post) {
        String ans = "";
        if (post.getRecipe() == null) {
            ans = post.getCaption();
        } else {
            Recipe recipe = post.getRecipe();
            ans = post.getCaption() + "\n\nRECIPE: ";
            ans += recipe.getTitle() + "\n";

            ans += "Category: " + recipe.getMain_category() + "-" + recipe.getCategory() + "\n";

            ans += "\nNutrition Facts -  \n";
            ans += "    Calories: " + recipe.getCalories() + "  \n";
            ans += "    Fat: " + recipe.getFat() + "  \n";
            ans += "    Carbs: " + recipe.getCarbs() + "  \n";
            ans += "    Protein: " + recipe.getProtein() + "\n";

            ans += "\nServings: " + recipe.getServings() + "\n";

            ans += "\nTimes -  \n";
            ans += "    Prep: " + recipe.getPrepTime() + "  \n";
            ans += "    Cooking: " + recipe.getCookingTime() + "  \n";

            int readyIn = Proper_time_int(recipe.getCookingTime()) + Proper_time_int(recipe.getPrepTime());
            String ansTotal = "";
            if (readyIn / 60 == 0) {
                ansTotal = readyIn + " mins";
            } else {
                if (readyIn % 60 == 0) {
                    ansTotal = readyIn / 60 + " hrs";
                } else {
                    ansTotal = readyIn / 60 + " hrs and " + readyIn % 60 + " mins";
                }
            }
            ans += "    Ready in: " + ansTotal + "\n";

            System.out.println("    Prep: " + recipe.getPrepTime() + "  \n"
                    + "    Cooking: " + recipe.getCookingTime() + "  \n"
                    + "    Ready in: " + recipe.getTotalTime() + "\n");

            ans += "\nIngredients - \n";
            if (!Objects.equals(post.getUser_id(), "www.allrecipes.com")) {

                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    String[] split = recipe.getIngredients().get(i).split(":");


                    Double weight = Double.parseDouble(split[0]);
                    if (weight < 1000) {
                        split[0] = Math.round(weight) + "g";
                    } else {
                        weight = weight / 1000;
                        split[0] = String.format("%.0f", weight) + " kg";
                    }
                    ans += "    " + split[0] + " " + split[1] + "\n";
                }
            } else {
                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ans += recipe.getIngredients().get(i);
                }
            }

            ans += "\nInstructions - \n";
            for (int i = 0; i < recipe.getDirections().size(); i++) {
                ans += "    Step " + (i + 1) + ": " + recipe.getDirections().get(i) + "\n";
            }


        }
        if (ans.split("\n").length > postMaxLine) {
            ans += "\n See less...";
        }
        return ans;
    }

    private String getTruncatedCaption(String post) {
        String fullCaption = post; // Get the full caption

        // Split the caption into lines
        String[] lines = fullCaption.split("\n");

        StringBuilder truncatedCaption = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, postMaxLine); i++) {
            truncatedCaption.append(lines[i]).append("\n");
        }

        // If there are more than 6 lines, add an ellipsis
        if (lines.length > postMaxLine) {
            truncatedCaption.append("See more..."); // Add ellipsis
        }

        return truncatedCaption.toString();
    }

    public static int Proper_time_int(String time) {
        if (time != null) {
            try {
                String[] temp = time.split(" ");
                if (temp.length < 3) {
                    if (temp[1].equals("hrs")) {
                        return ((Integer.parseInt(temp[0])) * 60);
                    }
                    return Integer.parseInt(temp[0]);
                } else {
                    if (!isNumeric(temp[0])) {
                        return ((Integer.parseInt(temp[1]) * 60) + Integer.parseInt(temp[3]));
                    } else {
                        return ((Integer.parseInt(temp[0]) * 60) + Integer.parseInt(temp[2]));
                    }

                }
            } catch (Exception e) {
                System.out.println("error:could not convert- " + time);
            }
        }
        return -1;
    }

    public static boolean isNumeric(String s) {
        if (s == null)
            return false;
        try {
            double d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void setupSendMessageButton(ImageView sendButton, EditText etNewComment, UserSettings currentUserSettings) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = etNewComment.getText().toString();
                etNewComment.setText("");

                if (TextUtils.isEmpty(commentText)) {
                    Toast.makeText(getActivity(), "first write your comment...", Toast.LENGTH_SHORT).show();
                } else {
                    // Add comment to post using the server
                    String comment_id = createHash();


                    serverMethods.retrofitInterface.addCommentToPost(post.getUser_id(),
                            post.getPost_id(), uid, commentText, currentUserSettings.getUser().getFull_name(),
                            currentUserSettings.getSettings().getProfile_photo(),
                            comment_id).enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                            if (response.code() == 200) {
                                System.out.println("PostAdapter - setupSendMessageButton - Success");

                                Comment comment = response.body();
                                if (comment != null) {
                                    post.addComment(comment);
//                                    requestUserFeed.patchPost(position, post);
//                                    changeAdapter();
                                    List<Comment> comments = post.getComments_list();
                                    commentsAdapter = new CommentsAdapter(comments, getActivity(), post.getUser_id(), post.getPost_id(), serverMethods);
                                    commentsRecyclerView.setAdapter(commentsAdapter);

                                    // Scroll to the last item in the list
                                    int lastPosition = comments.size() - 1;
                                    if (lastPosition >= 0) {
                                        commentsRecyclerView.scrollToPosition(lastPosition);
                                    }
                                }
                            } else {
                                System.out.println("PostAdapter - setupSendMessageButton - Failed");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                            System.out.println("PostAdapter - setupSendMessageButton - onFailure");

                        }
                    });
                }
            }
        });
    }


    private String createHash() {
        return "comment_" + UUID.randomUUID().toString();
    }


    private void updatePostLiked(String uid, Post post) {
        serverMethods.retrofitInterface.addOrRemovePostLiked(uid, post.getUser_id(), post.getPost_id()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    System.out.println(getActivity() + " - PostAdapter - updatePostLiked - response.isSuccessful()");
                    Boolean like = response.body();
                    if (like != null) {
                        System.out.println("likesCount = " + like);
                        if (like) {
                            post.addLike(uid);
                            imageLikes.setText(String.format("%s Likes", post.getLikesCount()));
                        } else {
                            post.removeLike(uid);
                            imageLikes.setText(String.format("%s Likes", post.getLikesCount()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                System.out.println(getActivity() + " - PostAdapter - updatePostLiked - onFailure - " + t.getMessage());
            }
        });
    }


    private void updateCartPost(String uid, Post post) {
        serverMethods.retrofitInterface.addOrRemoveCartPost(uid, post.getUser_id(), post.getPost_id()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    System.out.println(getActivity() + " - PostAdapter - updateCartPost - response.isSuccessful()");
                    Boolean like = response.body();
//                    if (like != null) {
//                        System.out.println("likesCount = " + like);
//                        if (like) {
//                            post.addLike(uid);
//                            holder.image_likes.setText(String.format("%s Likes", post.getLikesCount()));
//                        } else {
//                            post.removeLike(uid);
//                            holder.image_likes.setText(String.format("%s Likes", post.getLikesCount()));
//                        }
//                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                System.out.println(getActivity() + " - PostAdapter - updateCartPost - onFailure - " + t.getMessage());
            }
        });
    }


    private UserSettings getUserSettingsFromBundle() {
        Log.d(TAG, "getUserSettingsFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable("postOwnerUserSettings");
        } else {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            init();
        }
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try{
//            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
//        }catch (ClassCastException e){
//            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
//        }
//    }

    private void setProfileWidgets(User user, UserAccountSettings userAccountSettings) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());

        Glide.with(getActivity())
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
                .into(profilePhoto);

        //
//        mDisplayName.setText(user.getFull_name());
//        mUsername.setText(user.getUsername());
//        mWebsite.setText(userAccountSettings.getWebsite());
//        mDescription.setText(userAccountSettings.getDescription());
//        mPosts.setText(String.valueOf(userAccountSettings.getPosts()));
//        mFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));
//        mFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
//        mProgressBar.setVisibility(View.GONE);

    }

//    private void getLikesString(){
//        Log.d(TAG, "getLikesString: getting likes string");
////
////        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
////        Query query = reference
////                .child(getString(R.string.dbname_photos))
////                .child(mPhoto.getPhoto_id())
////                .child(getString(R.string.field_likes));
////        query.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                mUsers = new StringBuilder();
////                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
////
////                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
////                    Query query = reference
////                            .child(getString(R.string.dbname_users))
////                            .orderByChild(getString(R.string.field_user_id))
////                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
////                    query.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
////                                Log.d(TAG, "onDataChange: found like: " +
////                                        singleSnapshot.getValue(User.class).getUsername());
////
////                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
////                                mUsers.append(",");
////                            }
////
////                            String[] splitUsers = mUsers.toString().split(",");
////
////                            if(mUsers.toString().contains(mCurrentUser.getUsername() + ",")){//mitch, mitchell.tabian
////                                mLikedByCurrentUser = true;
////                            }else{
////                                mLikedByCurrentUser = false;
////                            }
////
////                            int length = splitUsers.length;
////                            if(length == 1){
////                                mLikesString = "Liked by " + splitUsers[0];
////                            }
////                            else if(length == 2){
////                                mLikesString = "Liked by " + splitUsers[0]
////                                        + " and " + splitUsers[1];
////                            }
////                            else if(length == 3){
////                                mLikesString = "Liked by " + splitUsers[0]
////                                        + ", " + splitUsers[1]
////                                        + " and " + splitUsers[2];
////
////                            }
////                            else if(length == 4){
////                                mLikesString = "Liked by " + splitUsers[0]
////                                        + ", " + splitUsers[1]
////                                        + ", " + splitUsers[2]
////                                        + " and " + splitUsers[3];
////                            }
////                            else if(length > 4){
////                                mLikesString = "Liked by " + splitUsers[0]
////                                        + ", " + splitUsers[1]
////                                        + ", " + splitUsers[2]
////                                        + " and " + (splitUsers.length - 3) + " others";
////                            }
////                            Log.d(TAG, "onDataChange: likes string: " + mLikesString);
////                            setupWidgets();
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
////                }
////                if(!dataSnapshot.exists()){
////                    mLikesString = "";
////                    mLikedByCurrentUser = false;
////                    setupWidgets();
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//
//    }
//
//    private void getCurrentUser(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.dbname_users))
//                .orderByChild(getString(R.string.field_user_id))
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                    mCurrentUser = singleSnapshot.getValue(User.class);
//                }
//                getLikesString();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//        });
//    }
//
//    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
////        @Override
////        public boolean onDown(MotionEvent e) {
////            return true;
////        }
////
////        @Override
////        public boolean onDoubleTap(MotionEvent e) {
////            Log.d(TAG, "onDoubleTap: double tap detected.");
//
////            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
////            Query query = reference
////                    .child(getString(R.string.dbname_photos))
////                    .child(mPhoto.getPhoto_id())
////                    .child(getString(R.string.field_likes));
////            query.addListenerForSingleValueEvent(new ValueEventListener() {
////                @Override
////                public void onDataChange(DataSnapshot dataSnapshot) {
////                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
////
////                        String keyID = singleSnapshot.getKey();
////
////                        //case1: Then user already liked the photo
////                        if(mLikedByCurrentUser &&
////                                singleSnapshot.getValue(Like.class).getUser_id()
////                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
////
////                            myRef.child(getString(R.string.dbname_photos))
////                                    .child(mPhoto.getPhoto_id())
////                                    .child(getString(R.string.field_likes))
////                                    .child(keyID)
////                                    .removeValue();
///////
////                            myRef.child(getString(R.string.dbname_user_photos))
////                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                                    .child(mPhoto.getPhoto_id())
////                                    .child(getString(R.string.field_likes))
////                                    .child(keyID)
////                                    .removeValue();
////
////                            mHeart.toggleLike();
////                            getLikesString();
////                        }
////                        //case2: The user has not liked the photo
////                        else if(!mLikedByCurrentUser){
////                            //add new like
////                            addNewLike();
////                            break;
////                        }
////                    }
////                    if(!dataSnapshot.exists()){
////                        //add new like
////                        addNewLike();
////                    }
////                }
////
////                @Override
////                public void onCancelled(DatabaseError databaseError) {
////
////                }
////            });
////
////            return true;
////        }
//    }
//
//    private void addNewLike(){
//        Log.d(TAG, "addNewLike: adding new like");
//
////        String newLikeID = myRef.push().getKey();
////        Like like = new Like();
////        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
////
////        myRef.child(getString(R.string.dbname_photos))
////                .child(mPhoto.getPhoto_id())
////                .child(getString(R.string.field_likes))
////                .child(newLikeID)
////                .setValue(like);
////
////        myRef.child(getString(R.string.dbname_user_photos))
////                .child(mPhoto.getUser_id())
////                .child(mPhoto.getPhoto_id())
////                .child(getString(R.string.field_likes))
////                .child(newLikeID)
////                .setValue(like);
////
////        mHeart.toggleLike();
////        getLikesString();
//    }
//
//    private void getPhotoDetails(){
//        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.dbname_user_account_settings))
//                .orderByChild(getString(R.string.field_user_id))
//                .equalTo(mPhoto.getUser_id());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
//                }
//                //setupWidgets();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//        });
//    }
//
//
//
//    private void setupWidgets(){
////        String timestampDiff = getTimestampDifference();
////        if(!timestampDiff.equals("0")){
////            mTimestamp.setText(timestampDiff + " DAYS AGO");
////        }else{
////            mTimestamp.setText("TODAY");
////        }
////        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfileImage, null, "");
////        mUsername.setText(mUserAccountSettings.getUsername());
////        mLikes.setText(mLikesString);
////        mCaption.setText(mPhoto.getCaption());
////
////        if(mPhoto.getComments().size() > 0){
////            mComments.setText("View all " + mPhoto.getComments().size() + " comments");
////        }else{
////            mComments.setText("");
////        }
////
////        mComments.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Log.d(TAG, "onClick: navigating to comments thread");
////
////                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
////
////            }
////        });
////
////        mBackArrow.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Log.d(TAG, "onClick: navigating back");
////                getActivity().getSupportFragmentManager().popBackStack();
////            }
////        });
////
////        mComment.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Log.d(TAG, "onClick: navigating back");
////                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
////
////            }
////        });
////
////        if(mLikedByCurrentUser){
////            mHeartWhite.setVisibility(View.GONE);
////            mHeartRed.setVisibility(View.VISIBLE);
////            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View v, MotionEvent event) {
////                    Log.d(TAG, "onTouch: red heart touch detected.");
////                    return mGestureDetector.onTouchEvent(event);
////                }
////            });
////        }
////        else{
////            mHeartWhite.setVisibility(View.VISIBLE);
////            mHeartRed.setVisibility(View.GONE);
////            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View v, MotionEvent event) {
////                    Log.d(TAG, "onTouch: white heart touch detected.");
////                    return mGestureDetector.onTouchEvent(event);
////                }
////            });
////        }
//
//
//    }
//
//
//    /**
//     * Returns a string representing the number of days ago the post was made
//     * @return
//     */
//    private String getTimestampDifference(){
//        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");
//
//        String difference = "";
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
//        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
//        Date today = c.getTime();
//        sdf.format(today);
//        Date timestamp;
//        final String photoTimestamp = mPhoto.getDate_created();
//        try{
//            timestamp = sdf.parse(photoTimestamp);
//            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
//        }catch (ParseException e){
//            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
//            difference = "0";
//        }
//        return difference;
//    }
//

    /**
     * retrieve the activity number from the incoming bundle from profileActivity interface
     *
     * @return
     */
    private int getActivityNumFromBundle() {
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            return 0;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     *
     * @return
     */
    private Post getPostFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.post));
        } else {
            return null;
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.enableNavigation(getContext(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }
//       /*
//    ------------------------------------ Firebase ---------------------------------------------
//     */
//
//    /**
//     * Setup the firebase auth object
//     */
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//
//        mAuth = FirebaseAuth.getInstance();
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        myRef = mFirebaseDatabase.getReference();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
}
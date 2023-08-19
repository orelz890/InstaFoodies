package Home;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import Server.RequestPosts;


import Server.RequestUserFeed;
import Utils.ServerMethods;
import Utils.StringImageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Comment;
import models.Post;
import models.Recipe;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // Server
    private ServerMethods serverMethods;


    private RequestUserFeed userFeed;

    private User user;

    private FirebaseAuth mAuth;
    private String uid;

    private DatabaseReference usersRef;
    private DocumentReference usersDoc;
    private Context mContext;
    private RelativeLayout layout;

    private CommentsAdapter commentsAdapter;
    private RecyclerView commentsRecyclerView;
    private Runnable refreshRunnable;
    private PopupWindow popupWindow;
    private Handler handler;
    private int currentPosition;
    private static final long REFRESH_INTERVAL = 3000; // 3 seconds
    private int postMaxLine = 5;




    public PostAdapter(RequestUserFeed userFeed, Context context, RelativeLayout layout) {
        this.userFeed = userFeed;
        this.mContext = context;
        this.serverMethods = new ServerMethods(context);
        this.layout = layout;
        this.user = user;
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_main_posts, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();


        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (userFeed != null) {
            Post post = userFeed.getPost(position);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            if (post.getRecipe() == null){
                holder.imageAddCart.setVisibility(View.INVISIBLE);
            }

            // Set the post content
            if (post != null) {
                System.out.println("PostAdapter - onBindViewHolder - post != null\n Post(" + position + "): " + post.toString());
                // Set the visibility of the add_to_cart ImageView based on whether the post is a recipe
                if (post.getRecipe() != null && !Objects.equals(post.getUser_id(), "www.allrecipes.com")) {
                    holder.imageAddCart.setVisibility(View.VISIBLE);
                } else {
                    holder.imageAddCart.setVisibility(View.GONE);
                }

                if (!post.getProfile_photo().isEmpty() && !post.getProfile_photo().equals("none")) {
                    Picasso.get().load(post.getProfile_photo()).into(holder.profilePhoto);
                } else {
                    holder.profilePhoto.setImageResource(R.drawable.profile_image);
                }

                // Set username
                holder.username.setText(post.getFull_name());

                // Set he post pictures
                List<String> image_paths = post.getImage_paths();
                if (image_paths != null && !image_paths.isEmpty()) {
                    holder.adapter = new StringImageAdapter(image_paths);
                    holder.postImages.setAdapter(holder.adapter);
                }

                // Set the post caption
                holder.postCaption.setText(getTruncatedCaption(captionsForPostOrRecipe(post)));
                holder.postCaption.setOnClickListener(new View.OnClickListener() {
                    boolean expanded = false; // Track if content is expanded
                    @Override
                    public void onClick(View v) {
                        if (expanded) {
                            holder.postCaption.setText(getTruncatedCaption(captionsForPostOrRecipe(post))); // Set truncated caption
                        } else {
                            String[] spilt = captionsForPostOrRecipe(post).split("\n");
                            System.out.println("in post adapter: " + spilt.length);
                            if (spilt.length > postMaxLine){
                                String tempSetText = captionsForPostOrRecipe(post)+"\n See less";
                                holder.postCaption.setText(tempSetText); // Set full caption with see less
                            }
                            else {
                                holder.postCaption.setText(captionsForPostOrRecipe(post)); // Set full caption
                            }
                            holder.postCaption.setText(captionsForPostOrRecipe(post)); // Set full caption
                        }
                        expanded = !expanded; // Toggle expanded state
                    }
                });



                // Set time
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm", Locale.ENGLISH);
                try {
                    String  inputDateString = post.getDate_created();
                    Date date = inputDateFormat.parse(inputDateString);
                    String formattedDate = outputDateFormat.format(date);

                    holder.postTimePosted.setText(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Set how much likes the post has & the heart color
                if (post.getRecipe()==null){holder.imageLikes.setText(String.format("%s Likes", post.getLikesCount()));}
                else{holder.imageLikes.setText(String.format("Recipe: "+post.getRecipe().getTitle()+" has %s Likes", post.getLikesCount()));}

                List<String> liked = post.getLiked_list();
                if (liked != null && liked.contains(uid)) {
                    holder.imageHeart.setImageResource(R.drawable.heart_red);
                } else {
                    holder.imageHeart.setImageResource(R.drawable.heart);
                }

                holder.imageHeart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (holder.imageHeart.getDrawable().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.heart).getConstantState())) {
                            holder.imageHeart.setImageResource(R.drawable.heart_red);
                        } else {
                            holder.imageHeart.setImageResource(R.drawable.heart);
                        }
                        updatePostLiked(holder, uid, post);
                    }
                });

                List<String> cart_list = post.getCart_list();
                if (cart_list != null && cart_list.contains(uid)) {
                    holder.imageAddCart.setImageResource(R.drawable.added_to_cart);
                } else {
                    holder.imageAddCart.setImageResource(R.drawable.add_to_cart);
                }

                holder.imageAddCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (holder.imageAddCart.getDrawable().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.add_to_cart).getConstantState())) {
                            holder.imageAddCart.setImageResource(R.drawable.added_to_cart);


                        } else {
                            holder.imageAddCart.setImageResource(R.drawable.add_to_cart);
                        }
                        updateCartPost(uid, post);
                    }
                });
                holder.imageShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String shareText = createPostShareText(post.getFull_name(), captionsForPostOrRecipe(post), holder.postTimePosted.getText().toString());
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        sendIntent.setType("text/plain");
                        try {
                            mContext.startActivity(sendIntent);
                            //maybe need to change for    mContext.startActivity(Intent.createChooser(sendIntent, "Share via"));
                            //for better user experience
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(mContext, "No apps can perform this action.", Toast.LENGTH_SHORT).show();
                        }}
                });

                holder.commentsBubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPopupCommentsWindow(position);
                    }
                });

            } else {
                System.out.println("PostAdapter - onBindViewHolder - post == null");
            }
        } else {
            System.out.println("PostAdapter - onBindViewHolder - userFeed == null");
        }
    }


    private String createPostShareText(String userName, String caption, String timeStamp) {
        String shareText = mAuth.getCurrentUser().getDisplayName()+ " shred "+userName+" post";
        shareText += "\n\n"+caption.split("See less...")[0]+"\n\n"+timeStamp;
        return shareText;
    }



    private void createPopupCommentsWindow(int position) {
        currentPosition = position;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View popupView = inflater.inflate(R.layout.custom_popup_comments_window, null);


        // Find views in the popupView
        EditText etNewComment = popupView.findViewById(R.id.et_new_comment_text);
        CircleImageView profileImage = popupView.findViewById(R.id.user_profile_image);
        ImageView sendButton = popupView.findViewById(R.id.iv_send);

        // setup RecyclerView
        commentsRecyclerView = popupView.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // Set user photo
        String profile_photo = userFeed.getPost(position).getProfile_photo();
        Picasso.get().load(profile_photo).placeholder(R.drawable.profile_image).into(profileImage);

        // Setup popup abilities
        setupSendMessageButton(sendButton, etNewComment, position);

        // Setup main comment feed
        setupCommentsMainFeed(position);


        // Get the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (mContext != null) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
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
        setupCommentsMainFeed(currentPosition);

    }

    private void setupCommentsMainFeed(int position) {
        Post post = userFeed.getPost(position);
        serverMethods.retrofitInterface.getPostComments(post.getUser_id(),post.getPost_id()).enqueue(new Callback<Comment[]>() {
            @Override
            public void onResponse(@NonNull Call<Comment[]> call, @NonNull Response<Comment[]> response) {
                if (response.code() == 200) {
                    System.out.println("PostAdapter - setupCommentsMainFeed - Success");
                    Comment[] comments = response.body();
                    if (comments != null && comments.length > 0) {
                        System.out.println("comment = " + comments[0].getComment());
                        commentsAdapter = new CommentsAdapter(comments, mContext, post.getUser_id(), post.getPost_id(), serverMethods);
                        commentsRecyclerView.setAdapter(commentsAdapter);
                        // Scroll to the last item in the list
                        int lastPosition = comments.length - 1;
                        if (lastPosition >= 0) {
                            commentsRecyclerView.scrollToPosition(lastPosition);
                        }
                    }
                }
                else {
                    System.out.println("PostAdapter - setupCommentsMainFeed - Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Comment[]> call, @NonNull Throwable t) {
                System.out.println("PostAdapter - setupCommentsMainFeed - onFailure - " + t.getMessage());

            }
        });

    }
    private String captionsForPostOrRecipe(Post post ){
        String ans="";
        if (post.getRecipe() == null){ans = post.getCaption();}
        else {
            Recipe recipe = post.getRecipe();
            ans=post.getCaption() + "\n\nRECIPE: ";
            ans += recipe.getTitle() + "\n";

            ans+= "Category: " + recipe.getMain_category() + "-" +recipe.getCategory()+"\n";

            ans += "\nNutrition Facts -  \n";
            ans += "    Calories: " + recipe.getCalories() + "  \n";
            ans += "    Fat: " + recipe.getFat() + "  \n";
            ans += "    Carbs: " + recipe.getCarbs() + "  \n";
            ans += "    Protein: " + recipe.getProtein() + "\n";

            ans += "\nServings: " + recipe.getServings() + "\n";

            ans += "\nTimes -  \n";
            ans += "    Prep: " + recipe.getPrepTime() + "  \n";
            ans += "    Cooking: " + recipe.getCookingTime() + "  \n";

            int readyIn = Proper_time_int(recipe.getCookingTime())+Proper_time_int(recipe.getPrepTime());
            String ansTotal = "";
            if (readyIn/60 == 0){
                ansTotal = readyIn + " mins";
            }else{
                if (readyIn%60 == 0){
                    ansTotal = readyIn/60 + " hrs";}
                else{
                    ansTotal = readyIn/60 + " hrs and "+ readyIn%60 + " mins";
                }
            }
            ans += "    Ready in: " + ansTotal +  "\n";

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
            }
            else {
                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ans += recipe.getIngredients().get(i);
                }
            }

            ans += "\nInstructions - \n";
            for (int i =0 ; i < recipe.getDirections().size(); i++) {
                ans += "    Step "+(i+1) + ": " + recipe.getDirections().get(i) + "\n";
            }


        }
        if (ans.split("\n").length > postMaxLine) {ans +="\n See less...";}
        return ans;
    }

    private String getTruncatedCaption(String post) {
        String fullCaption = post; // Get the full caption

        // Split the caption into lines
        String[] lines = fullCaption.split("\n");

        StringBuilder truncatedCaption = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, postMaxLine); i++) {
            truncatedCaption.append(lines[i]);
            if (i < Math.min(lines.length, postMaxLine) - 1) {
                truncatedCaption.append("\n"); // Add newline except for the last line
            }
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
                }
                else {
                    if (!isNumeric(temp[0])){
                        return ((Integer.parseInt(temp[1]) * 60) + Integer.parseInt(temp[3]));}
                    else{
                        return ((Integer.parseInt(temp[0]) * 60) + Integer.parseInt(temp[2]));}

                }
            } catch (Exception e) {
                System.out.println("error:could not convert- " + time);
            }
        }
        return -1;
    }
    public static boolean isNumeric(String s){
        if (s == null)
            return false;
        try{
            double d = Double.parseDouble(s);
        }
        catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private void setupSendMessageButton(ImageView sendButton, EditText etNewComment, int position) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = etNewComment.getText().toString();
                etNewComment.setText("");

                if (TextUtils.isEmpty(commentText)) {
                    Toast.makeText(mContext, "first write your comment...", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Add comment to post using the server
                    Post post = userFeed.getPost(position);

                    String comment_id = createHash();

                    String fullName = userFeed.getUser().getFull_name();
                    String profile_photo = userFeed.getAccount().getProfile_photo();

                    serverMethods.retrofitInterface.addCommentToPost(post.getUser_id(),
                            post.getPost_id(), uid, commentText, fullName, profile_photo,
                            comment_id).enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                            if (response.code() == 200) {
                                System.out.println("PostAdapter - setupSendMessageButton - Success");

                                Comment comment = response.body();
                                if (comment != null) {
                                    post.addComment(comment);

                                    List<Comment> comments = post.getComments_list();
                                    commentsAdapter = new CommentsAdapter(comments, mContext, post.getUser_id(), post.getPost_id(), serverMethods);
                                    commentsRecyclerView.setAdapter(commentsAdapter);

                                    // Scroll to the last item in the list
                                    int lastPosition = comments.size() - 1;
                                    if (lastPosition >= 0) {
                                        commentsRecyclerView.scrollToPosition(lastPosition);
                                    }
                                }
                            }
                            else {
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

    @Override
    public int getItemCount() {
        System.out.println("getItemCount = " + userFeed.size());
        return userFeed.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView username, imageLikes, postCaption, imageCommentsLink, postTimePosted;
        public CircleImageView profilePhoto;
        public ImageView ivEllipses, imageHeartRed, imageHeart, commentsBubble,imageAddCart,imageAddToCartFill,imageShare;
        public ViewPager2 postImages;
        public StringImageAdapter adapter;
        public View view;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            username = (TextView) itemView.findViewById(R.id.username); // <<<
            profilePhoto = (CircleImageView) itemView.findViewById(R.id.profile_photo); // <<<
            ivEllipses = (ImageView) itemView.findViewById(R.id.ivEllipses);
            postImages = (ViewPager2) itemView.findViewById(R.id.post_images); // <<<< ///
            imageHeartRed = (ImageView) itemView.findViewById(R.id.image_heart_red);
            imageShare = (ImageView) itemView.findViewById(R.id.image_share);
            imageHeart = (ImageView) itemView.findViewById(R.id.image_heart);
            commentsBubble = (ImageView) itemView.findViewById(R.id.speech_bubble);
            imageLikes = (TextView) itemView.findViewById(R.id.image_likes);
            postCaption = (TextView) itemView.findViewById(R.id.post_caption); // <<<<
            imageCommentsLink = (TextView) itemView.findViewById(R.id.image_comments_link);
            postTimePosted = (TextView) itemView.findViewById(R.id.post_time_posted); // <<<<
            imageAddCart = itemView.findViewById(R.id.add_cart);
            imageAddToCartFill = itemView.findViewById(R.id.add_to_cart_fill);
        }
    }

    private void updatePostLiked(PostViewHolder holder, String uid, Post post) {
        serverMethods.retrofitInterface.addOrRemovePostLiked(uid, post.getUser_id(), post.getPost_id()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    System.out.println(mContext + " - PostAdapter - updatePostLiked - response.isSuccessful()");
                    Boolean like = response.body();
                    if (like != null) {
                        System.out.println("likesCount = " + like);
                        if (like) {
                            post.addLike(uid);
                            holder.imageLikes.setText(String.format("%s Likes", post.getLikesCount()));
                        } else {
                            post.removeLike(uid);
                            holder.imageLikes.setText(String.format("%s Likes", post.getLikesCount()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                System.out.println(mContext + " - PostAdapter - updatePostLiked - onFailure - " + t.getMessage());
            }
        });
    }


    private void updateCartPost(String uid, Post post) {
        serverMethods.retrofitInterface.addOrRemoveCartPost(uid, post.getUser_id(), post.getPost_id()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    System.out.println(mContext + " - PostAdapter - updateCartPost - response.isSuccessful()");
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
                System.out.println(mContext + " - PostAdapter - updateCartPost - onFailure - " + t.getMessage());
            }
        });
    }

}

package Home;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import Chat.MessageAdapter;
import Server.RequestUserFeed;
import Utils.ServerMethods;
import Utils.StringImageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Post;
import models.User;
import models.UserAccountSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // Server
    private ServerMethods serverMethods;


    private RequestUserFeed requestUserFeed;


    private FirebaseAuth mAuth;
    private String uid;

    private DatabaseReference usersRef;
    private DocumentReference usersDoc;
    private Context mContext;
    private RelativeLayout layout;
    private UserAccountSettings userAccountSettings;


    public PostAdapter(RequestUserFeed requestUserFeed, Context context, RelativeLayout layout, UserAccountSettings userAccountSettings) {
        this.requestUserFeed = requestUserFeed;
        this.mContext = context;
        this.serverMethods = new ServerMethods(context);
        this.layout = layout;
        this.userAccountSettings = userAccountSettings;
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
        if (requestUserFeed != null) {
            User user = requestUserFeed.getUser();
            UserAccountSettings userAccountSettings = requestUserFeed.getAccount();
            Post post = requestUserFeed.getPost(position);

            // Set the user info & photo
            if (userAccountSettings != null) {
                System.out.println("PostAdapter - onBindViewHolder - userAccountSettings != null\n Post(" + position + "): " + userAccountSettings.toString());
                // Set photo
                String profile_photo = userAccountSettings.getProfile_photo();
                if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
                    Picasso.get().load(profile_photo).into(holder.profile_photo);
                } else {
                    holder.profile_photo.setImageResource(R.drawable.profile_image);
                }

                // Set username
                holder.username.setText(user.getUsername());
            } else {
                System.out.println("PostAdapter - onBindViewHolder - userAccountSettings == null");
            }

            // Set the post content
            if (post != null) {
                System.out.println("PostAdapter - onBindViewHolder - post != null\n Post(" + position + "): " + post.toString());

                // Set the post pictures
                List<String> image_paths = post.getImage_paths();
                if (image_paths != null && !image_paths.isEmpty()) {
                    holder.adapter = new StringImageAdapter(image_paths);
                    holder.post_images.setAdapter(holder.adapter);
                }

                // Set the post caption
                holder.post_caption.setText(post.getCaption());

                // Set time
                holder.post_time_posted.setText(post.getDate_created());

                // Set how much likes the post has & the heart color
                holder.image_likes.setText(String.format("%s Likes", post.getLikesCount()));

                List<String> liked = post.getLiked();
                if (liked != null && liked.contains(uid)) {
                    holder.image_heart.setImageResource(R.drawable.heart_red);
                } else {
                    holder.image_heart.setImageResource(R.drawable.heart);
                }

                holder.image_heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (holder.image_heart.getDrawable().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.heart).getConstantState())) {
                            holder.image_heart.setImageResource(R.drawable.heart_red);
                        } else {
                            holder.image_heart.setImageResource(R.drawable.heart);
                        }
                        updatePostLiked(holder, uid, post);
                    }
                });

                holder.speech_bubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPopupCommentsWindow();
                    }
                });


            } else {
                System.out.println("PostAdapter - onBindViewHolder - post == null");
            }
        } else {
            System.out.println("PostAdapter - onBindViewHolder - requestUserFeed == null");
        }
    }

    private void createPopupCommentsWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View popupView = inflater.inflate(R.layout.custom_popup_comments_window, null);

        // Find views in the popupView
        EditText etNewComment = popupView.findViewById(R.id.et_new_comment_text);
        RecyclerView commentsRecyclerView = popupView.findViewById(R.id.commentsRecyclerView);
        CircleImageView profileImage = popupView.findViewById(R.id.user_profile_image);
        ImageView sendButton = popupView.findViewById(R.id.iv_send);

        // Set user photo
        Picasso.get().load(userAccountSettings.getProfile_photo()).placeholder(R.drawable.profile_image).into(profileImage);

        // Setup popup abilities
        setupSendMessageButton(sendButton, etNewComment);


        // Get the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (mContext != null) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
//        int width = ViewGroup.LayoutParams.MATCH_PARENT;
//        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, screenWidth, screenHeight / 2, focusable);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            }
        });

    }

    private void setupSendMessageButton(ImageView sendButton, EditText etNewComment) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = etNewComment.getText().toString();

                if (TextUtils.isEmpty(commentText)) {
                    Toast.makeText(mContext, "first write your comment...", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Add comment to post using the server

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        System.out.println("getItemCount = " + requestUserFeed.size());
        return requestUserFeed.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView username, image_likes, post_caption, image_comments_link, post_time_posted;
        public CircleImageView profile_photo;
        public ImageView ivEllipses, image_heart_red, image_heart, speech_bubble;
        public ViewPager2 post_images;
        public StringImageAdapter adapter;
        public View view;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            username = (TextView) itemView.findViewById(R.id.username); // <<<
            profile_photo = (CircleImageView) itemView.findViewById(R.id.profile_photo); // <<<
            ivEllipses = (ImageView) itemView.findViewById(R.id.ivEllipses);
            post_images = (ViewPager2) itemView.findViewById(R.id.post_images); // <<<< ///
            image_heart_red = (ImageView) itemView.findViewById(R.id.image_heart_red);
            image_heart = (ImageView) itemView.findViewById(R.id.image_heart);
            speech_bubble = (ImageView) itemView.findViewById(R.id.speech_bubble);
            image_likes = (TextView) itemView.findViewById(R.id.image_likes);
            post_caption = (TextView) itemView.findViewById(R.id.post_caption); // <<<<
            image_comments_link = (TextView) itemView.findViewById(R.id.image_comments_link);
            post_time_posted = (TextView) itemView.findViewById(R.id.post_time_posted); // <<<<
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
                            holder.image_likes.setText(String.format("%s Likes", post.getLikesCount()));
                        } else {
                            post.removeLike(uid);
                            holder.image_likes.setText(String.format("%s Likes", post.getLikesCount()));
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

}

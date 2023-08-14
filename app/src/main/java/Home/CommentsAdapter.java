package Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import Server.RequestUserFeed;
import Utils.ServerMethods;
import Utils.StringImageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Comment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private ServerMethods serverMethods;
    private Context context;
    private String postOwner, postId;
    private List<Comment> comments;
    private static DecimalFormat decimalFormat;


    public CommentsAdapter(Comment[] comments, Context context, String postOwner, String postId, ServerMethods serverMethods) {
        this.comments = new ArrayList<>();
        this.comments.addAll(Arrays.asList(comments));
        this.context = context;
        this.postOwner = postOwner;
        this.postId = postId;
        this.serverMethods = serverMethods;
        this.decimalFormat = new DecimalFormat("#.0");
    }

    public CommentsAdapter(List<Comment> comments, Context context, String postOwner, String postId, ServerMethods serverMethods) {
        this.comments = new ArrayList<>();
        this.comments.addAll(comments);
        this.context = context;
        this.postOwner = postOwner;
        this.postId = postId;
        this.serverMethods = serverMethods;
        this.decimalFormat = new DecimalFormat("#.0");
    }

    @NonNull
    @Override
    public CommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comments_display_layout, viewGroup, false);

        return new CommentsAdapter.CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentsViewHolder holder, int position) {
        if (comments != null) {
            Comment comment = comments.get(position);

            // Set the commenter profile photo
            String profile_photo = comment.getPhoto();
            if (!profile_photo.isEmpty() && !profile_photo.equals("none")) {
                Picasso.get().load(profile_photo).into(holder.profilePhoto);
            } else {
                holder.profilePhoto.setImageResource(R.drawable.profile_image);
            }

            // Set user name
            holder.tvUserName.setText(comment.getName());

            // Set the comment date
            holder.tvTimeStamp.setText(getTimeAgo(comment.getDate()));

            // Set actual comment
            holder.etComment.setText(comment.getComment());

            System.out.println("Time past = " + getTimeAgo(comment.getDate()));
            // Set like count
            holder.tvLikeCount.setText(getLikesCountString(comment.getLikeCount()));

            // Set heart image
            List<String> liked = comment.getLiked();
            String uid = comment.getUid();
            if (liked != null && liked.contains(uid)) {
                holder.ivImageHeart.setImageResource(R.drawable.heart_red);
            } else {
                holder.ivImageHeart.setImageResource(R.drawable.heart);
            }

            holder.ivImageHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.ivImageHeart.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.heart).getConstantState())) {
                        holder.ivImageHeart.setImageResource(R.drawable.heart_red);
                    } else {
                        holder.ivImageHeart.setImageResource(R.drawable.heart);
                    }
                    updatePostLiked(holder, uid, position);
                }


            });
        }
    }

    private void updatePostLiked(CommentsViewHolder holder, String uid, int position) {
        serverMethods.retrofitInterface.addOrRemoveLikeToPostComment(postOwner,postId,uid, position).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.code() == 200) {
                    System.out.println("CommentsAdapter - updatePostLiked - Success");
                    Boolean like = response.body();

                    Comment comment = comments.get(position);
                    if (Boolean.TRUE.equals(like)){
                        comments.get(position).addLike(uid);
                        holder.tvLikeCount.setText(getLikesCountString(comments.get(position).getLikeCount()));
                    }
                    else {
                        comments.get(position).removeLike(uid);
                        holder.tvLikeCount.setText(getLikesCountString(comments.get(position).getLikeCount()));
                    }
                }
                else {
                    System.out.println("CommentsAdapter - updatePostLiked - Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                System.out.println("CommentsAdapter - updatePostLiked - onFailure - " + t.getMessage());

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profilePhoto;
        public ImageView ivImageHeart;
        public TextView tvUserName, tvTimeStamp, tvLikeCount, etComment;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = (CircleImageView) itemView.findViewById(R.id.user_profile_image); // <<<
            tvUserName = (TextView) itemView.findViewById(R.id.user_profile_name);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tv_comment_time_stamp);
            etComment = (TextView) itemView.findViewById(R.id.tv_user_comment);
            ivImageHeart = (ImageView) itemView.findViewById(R.id.iv_heart);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tv_like_count);
        }
    }

    public static String getTimeAgo(String pastDateText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
        try {
            Date pastDate = dateFormat.parse(pastDateText);
            long currentTimeMillis = System.currentTimeMillis(); // Current time in milliseconds
            long timeDiffMillis = Math.abs(pastDate.getTime() - currentTimeMillis);
            long seconds = Math.abs(TimeUnit.MILLISECONDS.toSeconds(timeDiffMillis));
            long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(timeDiffMillis));
            long hours = Math.abs(TimeUnit.MILLISECONDS.toHours(timeDiffMillis));
            long days = Math.abs(TimeUnit.MILLISECONDS.toDays(timeDiffMillis));

            if (days > 365) {
                return days + " Y";
            } else if (days > 30) {
                return days / 30 + " M";
            } else if (days > 7) {
                return days / 7 + " W";
            } else if (days > 0) {
                return days + " d";
            } else if (hours > 0) {
                return hours + " h";
            } else if (minutes > 0) {
                return minutes + " m";
            } else {
                return seconds + " s";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }

    public static String getLikesCountString(int likeCount) {

        if (likeCount >= 1000000000){
            return decimalFormat.format(likeCount/1000000000) + "B";
        } else if(likeCount >= 1000000){
            return decimalFormat.format(likeCount/1000000) + "M";
        } else if(likeCount >= 1000){
            return decimalFormat.format(likeCount/1000) + "K";
        } else {
            return likeCount + "";
        }
    }

}

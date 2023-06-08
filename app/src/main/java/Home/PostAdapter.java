package Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import Utils.StringImageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Post;
import models.User;
import models.UserAccountSettings;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{


    private RequestUserFeed requestUserFeed;


    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DocumentReference usersDoc;


    public PostAdapter(RequestUserFeed requestUserFeed) {
        this.requestUserFeed = requestUserFeed;
    }





    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_main_posts, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (requestUserFeed != null) {
            User user = requestUserFeed.getUser();
            UserAccountSettings userAccountSettings = requestUserFeed.getAccount();
            Post post = requestUserFeed.getPost(position);

            // Set the user info & photo
            if (userAccountSettings != null){
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
            }
            else{
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

            }
            else{
                System.out.println("PostAdapter - onBindViewHolder - post == null");
            }
        }
        else{
            System.out.println("PostAdapter - onBindViewHolder - requestUserFeed == null");
        }

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

}

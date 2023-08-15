package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public class Post implements Parcelable, Serializable {

    @SerializedName("caption")
    private String caption;

    @SerializedName("recipe")
    private Recipe recipe;

    @SerializedName("date_created")
    private String date_created;

    @SerializedName("image_paths")
    private List<String> image_paths;

    @SerializedName("post_id")
    private String post_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("tags")
    private String tags;

    @SerializedName("liked_list")
    private List<String> liked_list;

    @SerializedName("cart_list")
    private List<String> cart_list;

    @SerializedName("comments_list")
    private List<Comment> comments_list;


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


    public Post() {

    }


    public HashMap<String, Object> PostMapForServer(Recipe recipe, String caption,
                                                    String date_created, List<String> image_paths,
                                                    List<String> liked_list, List<String> cart_list,
                                                    List<Comment> comments_list, String photo_id,
                                                    String user_id, String tags){
        HashMap<String, Object> ans = new HashMap<>();
        if(recipe != null) {
            ans.put("recipe", recipe);
            ans.put("caption", caption);
            ans.put("date_created", date_created);
            ans.put("image_paths", image_paths);
            ans.put("liked_list", liked_list);
            ans.put("cart_list", cart_list);
            ans.put("comments_list", comments_list);
            ans.put("post_id", photo_id);
            ans.put("user_id", user_id);
            ans.put("tags", tags);
        }else{
            ans.put("caption", caption);
            ans.put("date_created", date_created);
            ans.put("image_paths", image_paths);
            ans.put("liked_list", liked_list);
            ans.put("cart_list", cart_list);
            ans.put("comments_list", comments_list);
            ans.put("post_id", photo_id);
            ans.put("user_id", user_id);
            ans.put("tags", tags);
        }
        return ans;
    }

    public Post(List<String> cart_list) {
        this.cart_list = cart_list;
    }

    public List<Comment> getComments_list() {
        return comments_list;
    }

    public void setComments_list(List<Comment> comments_list) {
        this.comments_list.clear();
        this.comments_list.addAll(comments_list);

    }


    public Post(Recipe recipe, String caption, String date_created, List<String> image_paths,
                List<String> liked_list, List<String> cart_list, List<Comment> comments_list, String post_id,
                String user_id, String tags) {
        this.recipe = recipe;
        this.caption = caption;
        this.date_created = date_created;
        this.image_paths = image_paths;
        if (liked_list != null){
            this.liked_list = liked_list;
        }
        else {
            this.liked_list = new ArrayList<>();
        }
        if (cart_list != null){
            this.cart_list = cart_list;
        }
        else {
            this.cart_list = new ArrayList<>();
        }
        if (comments_list != null){
            this.comments_list = comments_list;
        }
        else {
            this.comments_list = new ArrayList<>();
        }
        this.post_id = post_id;
        this.user_id = user_id;
        this.tags = tags;
    }

    protected Post(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        image_paths = in.createStringArrayList();
        liked_list = in.createStringArrayList();
        cart_list = in.createStringArrayList();
        comments_list = in.createTypedArrayList(Comment.CREATOR); // Read comments using createTypedArrayList
        post_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public List<String> getImage_paths() {
        return image_paths;
    }


    public void setImage_path(String[] image_paths) {
        this.image_paths.clear();
        this.image_paths.addAll(Arrays.asList(image_paths));
    }

    public void setImage_paths(List<String> image_paths) {
        this.image_paths.clear();
        this.image_paths.addAll(image_paths);
    }

//    public void setImage_paths_(List<String> image_paths) {
//        this.image_paths.clear();
//        this.image_paths.addAll(image_paths);
//    }


    public List<String> getLiked_list() {
        return liked_list;
    }

    public void setLiked_list(List<String> liked_list) {
        this.liked_list.clear();
        this.liked_list.addAll(liked_list);
    }

    public List<String> getCart_list() {
        return cart_list;
    }

    public void setCart_list(List<String> cart_list) {
        this.cart_list.clear();
        this.cart_list.addAll(cart_list);
    }

    public void addLike(String uid) {
        if (this.liked_list == null) {
            this.liked_list = new ArrayList<>();
        }
        this.liked_list.add(uid);
    }

    public void removeLike(String uid) {
        if (this.liked_list == null) {
            return;
        }
        this.liked_list.remove(uid);
    }

    public void setLiked(String[] liked_list) {
        this.liked_list.clear();
        this.liked_list.addAll(Arrays.asList(liked_list));
    }

    public int getLikesCount(){
        if (this.liked_list == null){
            return 0;
        }
        return this.liked_list.size();
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public void removeComment(Comment comment) {
        this.comments_list.remove(comment);
    }

    public void addComment(Comment comment) {
        if (this.comments_list == null){
            this.comments_list = new ArrayList<>();
        }
        this.comments_list.add(comment);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Post{" +
                "caption='" + caption + '\'' +
                ", recipe=" + recipe +
                ", date_created='" + date_created + '\'' +
                ", image_paths=" + image_paths +
                ", post_id='" + post_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                ", liked_list=" + liked_list +
                ", cart_list=" + cart_list +
                ", comments_list=" + comments_list +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(date_created);
        dest.writeStringList(image_paths);
        dest.writeStringList(liked_list);
        dest.writeStringList(cart_list);
        dest.writeTypedList(comments_list);
        dest.writeString(post_id);
        dest.writeString(user_id);
        dest.writeString(tags);
    }
}

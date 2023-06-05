package models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;



public class Post implements Parcelable {

    @SerializedName("caption")
    private String caption;

    @SerializedName("recipe")
    private Recipe recipe;

    @SerializedName("date_created")
    private String date_created;

    @SerializedName("image_paths")
    private List<Uri> image_paths;

    @SerializedName("post_id")
    private String post_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("tags")
    private String tags;


    public Post() {

    }

    public HashMap<String, Object> PostMapForServer(Recipe recipe, String caption, String date_created, List<Uri> image_path, String photo_id, String user_id, String tags){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("recipe", recipe);
        ans.put("caption", caption);
        ans.put("date_created", date_created);
        ans.put("image_paths", image_path);
        ans.put("post_id", photo_id);
        ans.put("user_id", user_id);
        ans.put("tags", tags);

        return ans;
    }

    public Post(Recipe recipe,String caption, String date_created, List<Uri> image_path, String photo_id, String user_id, String tags) {
        this.recipe = recipe;
        this.caption = caption;
        this.date_created = date_created;
        this.image_paths = image_path;
        this.post_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
    }

    protected Post(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        image_paths = in.createTypedArrayList(Uri.CREATOR);
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

    public List<Uri> getImage_paths() {
        return image_paths;
    }

    public void setImage_paths(List<Uri> image_paths) {
        this.image_paths = image_paths;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
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
                ",recipe=" + recipe.toString() + '\'' +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_paths='" + image_paths + '\'' +
                ", post_id='" + post_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
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
        dest.writeTypedList(image_paths);
        dest.writeString(post_id);
        dest.writeString(user_id);
        dest.writeString(tags);
    }
}

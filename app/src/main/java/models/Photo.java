package models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;



public class Photo implements Parcelable {

    @SerializedName("caption")
    private String caption;

    @SerializedName("recipe")
    private Recipe recipe;

    @SerializedName("date_created")
    private String date_created;

    @SerializedName("image_path")
    private List<Uri> image_path;

    @SerializedName("photo_id")
    private String photo_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("tags")
    private String tags;


    public Photo() {

    }

    public HashMap<String, Object> PhotoMapForServer(Recipe recipe, String caption, String date_created, List<Uri> image_path, String photo_id, String user_id, String tags){
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("recipe", recipe);
        ans.put("caption", caption);
        ans.put("date_created", date_created);
        ans.put("image_path", image_path);
        ans.put("photo_id", photo_id);
        ans.put("user_id", user_id);
        ans.put("tags", tags);

        return ans;
    }

    public Photo(Recipe recipe,String caption, String date_created, List<Uri> image_path, String photo_id, String user_id, String tags) {
        this.recipe = recipe;
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
    }

    protected Photo(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        image_path = in.createTypedArrayList(Uri.CREATOR);
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
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

    public List<Uri> getImage_path() {
        return image_path;
    }

    public void setImage_path(List<Uri> image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
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
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
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
        dest.writeTypedList(image_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeString(tags);
    }
}

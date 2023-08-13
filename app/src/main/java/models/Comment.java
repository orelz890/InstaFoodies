package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment implements Parcelable {

    @SerializedName("date")
    private String date;

    @SerializedName("uid")
    private String uid;

    @SerializedName("name")
    private String name;

    @SerializedName("liked")
    private List<String> liked;

    @SerializedName("photo")
    private String photo;

    @SerializedName("comment")
    private String comment;

    @SerializedName("commentId")
    private String commentId;


    public Comment(String date, String uid, String name, List<String> liked, String photo, String comment, String commentId) {
        this.date = date;
        this.uid = uid;
        this.name = name;
        if (liked == null){
            liked = new ArrayList<>();
        }
        else{
            this.liked = liked;
        }
        this.photo = photo;
        this.comment = comment;
        this.commentId = commentId;
    }

    public String getDate() {
        return date;
    }

    public List<String> getLiked() {
        return liked;
    }

    public void setLiked(List<String> liked) {
        this.liked.clear();
        this.liked.addAll(liked);
    }

    public void removeLike(String uid) {
        this.liked.remove(uid);
    }

    public void addLike(String uid) {
        this.liked.add(uid);
    }

    public int getLikeCount() {
        if (liked == null) {
            this.liked = new ArrayList<>();
            return 0;
        }
        return liked.size();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    // Constructor that reads from a Parcel
    protected Comment(Parcel in) {
        // Read data from the Parcel and populate your fields
        date = in.readString();
        uid = in.readString();
        name = in.readString();
        liked = in.createStringArrayList();
        photo = in.readString();
        comment = in.readString();
        commentId = in.readString();
    }

    // Parcelable creator
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write your data to the Parcel
        dest.writeString(date);
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeStringList(liked);
        dest.writeString(photo);
        dest.writeString(comment);
        dest.writeString(commentId);
    }

}

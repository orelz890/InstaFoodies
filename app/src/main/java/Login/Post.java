package Login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Post {

    @SerializedName("authorId")
    private String authorId;

    @SerializedName("postNumber")
    private int postNumber;

    @SerializedName("personalNotes")
    private String personalNotes;

    @SerializedName("likes")
    private int likes;

    @SerializedName("shares")
    private int shares;

    @SerializedName("views")
    private int views;

    @SerializedName("images")
    private List<String> images = new ArrayList<>();

    @SerializedName("comments")
    private HashMap<String, String> comments = new HashMap<>();

    @SerializedName("recipe")
    private Recipe recipe = null;

    public Post() {
        this.images = new ArrayList<>();
        this.comments = new HashMap<>();
        this.recipe = null;
    }

    public Post(String authorId, int postNumber, String personalNotes, int likes, int shares,
                int views, List<String> images, HashMap<String, String> comments, Recipe recipe) {
        this.authorId = authorId;
        this.postNumber = postNumber;
        this.personalNotes = personalNotes;
        this.likes = likes;
        this.shares = shares;
        this.views = views;
        this.images = images;
        this.comments = comments;
        this.recipe = recipe;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public int getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(int postNumber) {
        this.postNumber = postNumber;
    }

    public String getPersonalNotes() {
        return personalNotes;
    }

    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    public HashMap<String, String> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, String> comments) {
        this.comments.clear();
        this.comments.putAll(comments);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

}

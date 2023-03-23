package Login;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Recipe {

    //  Must haves:
    @SerializedName("copy_rights")
    private String copy_rights;

    @SerializedName("title")
    private String title;

    @SerializedName("mainCategory")
    private String main_category;

    @SerializedName("category")
    private String category;

    @SerializedName("ingredients")
    private List<String> ingredients;

    @SerializedName("directions")
    private List<String> directions;

    @SerializedName("prepTime")
    private String prepTime;

    @SerializedName("cookingTime")
    private String cookingTime;

    @SerializedName("totalTime")
    private String totalTime;

    @SerializedName("servings")
    private String servings;

    @SerializedName("protein")
    private String protein;

    @SerializedName("fat")
    private String fat;

    @SerializedName("carbs")
    private String carbs;

    @SerializedName("calories")
    private String calories;

    //  Nice to have additions:
    @SerializedName("stars")
    private double stars;

    @SerializedName("numOfStarGivers")
    private int numOfStarGivers;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("comments")
    private HashMap<String, String> comments;

    public Recipe() {
        init();
    } // recipe(<empty>)

    public Recipe(String title, String main_category, String category,
                  List<String> ingredients, List<String> directions,
                  String prepTime, String cookingTime, String servings, String protein, String calories,
                  String fat, String carbs, double stars, List<String> images, int numOfStarGivers,
                  HashMap<String, String> comments, String copy_rights) {
        init();
        this.setTitle(title);
        this.setMain_category(main_category);
        this.setCategory(category);
        this.setIngredients(ingredients);
        this.setDirections(directions);
        this.setImages(images);
        this.setPrepTime(prepTime);
        this.setCookingTime(cookingTime);
        this.setTotalTime(this.prepTime + "(prep) + " + this.cookingTime + "(cooking)");
        this.setServings(servings);
        this.setProtein(protein);
        this.setCarbs(carbs);
        this.setFat(fat);
        this.setStars(stars);
        this.setNumOfStarGivers(numOfStarGivers);
        this.setComments(comments);
        this.calories = calories;
        this.setCopy_rights(copy_rights);

    } // recipe(<data>)

    public Recipe(Recipe r){
        this(r.title,r.main_category,r.category,r.ingredients,r.directions,r.prepTime,
                r.cookingTime,r.servings,r.protein,r.calories,r.fat,r.carbs,r.stars,r.images,
                r.numOfStarGivers,r.comments,r.copy_rights);
        this.totalTime = r.totalTime;
        this.setIngredientsFromList(r.ingredients);
        this.setImagesFromList(r.images);
        this.setDirectionsFromList(r.directions);
    }

    public Recipe(JsonObject data, String copy_rights) {
        init();
        this.setTitle(data.get("title").toString().replace("\"", ""));
        this.setMain_category(data.get("main category").toString().replace("\"", ""));
        this.setCategory(data.get("Category").toString().replace("\"", ""));
        this.setIngredientsFromList(convert_json_array_to_str_list(data.get("Ingredients").getAsJsonArray()));
        this.setDirections(convert_json_array_to_str_list(data.get("Directions").getAsJsonArray()));
        this.setImages(convert_json_array_to_str_list(data.get("Images").getAsJsonArray()));
        this.set_details(data.get("Details").getAsJsonArray());
        this.set_nutritions(data.get("Nutrition Facts").getAsJsonArray());
        this.setStars(0);
        this.setNumOfStarGivers(0);
        this.setCopy_rights(copy_rights);

    } // recipe(<json>)

    private void init() {
        this.title = "";
        this.ingredients = new ArrayList<>();
        this.directions = new ArrayList<>();
        this.comments = new HashMap<>();
        this.images = new ArrayList<>();
    } // init

    @Override
    public boolean equals(Object other) {
        if (other instanceof Recipe) {
            Recipe otherElement = (Recipe) other;
            // Define your comparison criteria here
            return this.title.equals(otherElement.title);
        }
        return false;
    }

    private List<String> convert_json_array_to_str_list(JsonArray arr) {
        JsonArray ingredients = arr.getAsJsonArray();
        int arr_size = ingredients.size();
        List<String> str_list = new ArrayList<>();
        for (int i = 0; i < arr_size; i++){
            JsonObject ing = ingredients.get(i).getAsJsonObject();
            for (String key :ing.keySet()) {
                str_list.add(ing.get(key).toString().replace("\"", ""));
            }
        }
        return str_list;
    } // convert_json_array_to_str_list

    private void set_details(JsonArray details){
        int size = details.size();
        for (int i = 0; i < size; i++) {
            JsonObject detail = details.get(i).getAsJsonObject();
            for (String key :detail.keySet()) {
                switch (key) {
                    case "Prep Time":
                        this.prepTime = detail.get(key).toString().replace("\"", "");
                        break;
                    case "Cook Time":
                        this.cookingTime = detail.get(key).toString().replace("\"", "");
                        break;
                    case "Servings":
                        this.servings = detail.get(key).toString().replace("\"", "");
                        break;
                }
            }
        }
        this.setTotalTime(this.prepTime + "(prep) + " + this.cookingTime + "(cooking)");
    } // set_details

    private void set_nutritions(JsonArray nutrition){
        for (int i = 0; i < nutrition.size(); i++) {
            JsonObject fact = nutrition.get(i).getAsJsonObject();
            for (String key : fact.keySet()) {
                switch (key) {
                    case "Carbs":
                        this.carbs = fact.get(key).toString().replace("\"", "");
                        break;
                    case "Fat":
                        this.fat = fact.get(key).toString().replace("\"", "");
                        break;
                    case "Protein":
                        this.protein = fact.get("Protein").toString().replace("\"", "");
                        break;
                    case "Calories":
                        this.calories = fact.get(key).toString().replace("\"", "");
                        break;
                }
            }
        }
    } // set_nutritions

    public int getNumOfStarGivers() {
        return numOfStarGivers;
    }

    public void setNumOfStarGivers(int numOfStarGivers) {
        this.numOfStarGivers = numOfStarGivers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.replace("\"", "");
    }

    public String getMain_category() {
        return main_category;
    }

    public void setMain_category(String main_category) {
        this.main_category = main_category.replace("\"", "");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category.replace("\"", "");
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredientsFromList(List<String> ingredients) {
        this.ingredients.clear();
        for (String curr_str : ingredients) {
            String curr = curr_str.trim();
            this.ingredients.add(curr);

        }
    } // setIngredients

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    } // setIngredients

    public List<String> getDirections() {
        return directions;
    }

    public void setDirectionsFromList(List<String> directions) {
        this.directions.clear();
        for (String curr_str : directions) {
            String curr = curr_str.trim();
            this.directions.add(curr);
        }
    }
    public void setDirections(List<String> directions) {
        this.directions = directions;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> imgs) {
        this.images = imgs;
    }
    public void setImagesFromList(List<String> imgs) {
        this.images.clear();
        this.images.addAll(imgs);
    }

    public void addImage(String img) {
        this.images.add(img);
    }

    public void delImage(String img) {
        this.images.remove(img);
    }

    public HashMap<String, String> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, String> comments) {
        this.comments = comments;
    }
    public void setCommentsFromMap(HashMap<String, String> comments) {
        comments.clear();
        for (String key: comments.keySet()) {
            this.comments.put(key,comments.get(key));
        }
    }

    public void delComment(String name, String comment) {
        for (int i = 0; i < this.comments.size(); i++){
            if (Objects.equals(this.comments.get(name), comment)){
                this.comments.remove(name);
                break;
            }
        }
    }

    public void addComment(String name, String comment) {
        this.comments.put(name, comment);
    }

    public String getCopy_rights() {
        return copy_rights;
    }

    public void setCopy_rights(String copy_rights) {
        this.copy_rights = copy_rights;
    }

    @Override
    public String toString() {
        return "recipe{" +
                "title='" + title + '\'' +
                ", main_category='" + main_category + '\'' +
                ", category='" + category + '\'' +
                ", ingredients=" + ingredients.toString() +
                ", directions=" + directions.toString() +
                ", prepTime=" + prepTime +
                ", cookingTime=" + cookingTime +
                ", totalTime=" + totalTime +
                ", servings=" + servings +
                ", protein=" + protein +
                ", fat=" + fat +
                ", carbs=" + carbs +
                ", stars=" + stars +
                ", numOfStarGivers=" + numOfStarGivers +
                ", images=" + images +
                ", comments=" + comments.toString() +
                '}';
    } // toString
}

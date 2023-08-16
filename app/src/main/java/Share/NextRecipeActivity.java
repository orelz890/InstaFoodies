package Share;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import Home.HomeActivity;
import Search.SearchActivity;
import Utils.ServerMethods;
import models.Post;
import models.Recipe;
import models.UserAccountSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NextRecipeActivity extends AppCompatActivity {

    private List<Uri> imageUris;
    FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private ImageAdapter adapter;
    private ViewPager2 viewPagerImages;
    private TextView imageCounterTextView;
    private ServerMethods serverMethods;

    // Text inputs from the user
    TextInputEditText etTitle;
    TextView etIngredients;
    ImageButton addIngredients;
    TextInputEditText etDirections;
    TextInputEditText etPostDescription;

    // Main & sub categories as the user wish
    TextInputEditText category;
    TextInputEditText subCategory;

    // Number inputs from the user
    NumberPicker npCookingTime;
    NumberPicker npPrepTime;
    NumberPicker npServings;
    NumberPicker npCarbs;
    NumberPicker npProtein;
    NumberPicker npFat;
    NumberPicker npCalories;

    // Submit recipe button
    Button btnSubmit;
    FloatingActionButton fab;
    List<String> uploadedImages;
    List<ImageView> imageViews;
    List<ImageButton> deleteImageButtons;

    AutoCompleteTextView autoCompleteSearchView;
    MaterialButton submit;
    ArrayAdapter<String> arraySearchAdapter;

    ArrayList<String> ingredientNamesList;
    static ArrayList<String> selectedIngredients;
    static ListView listView;
    static ListIngredientsAdapter adapterIngredients;

    private boolean illegalUserActionPerformed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_recipe);

        mAuth = FirebaseAuth.getInstance();
        serverMethods = new ServerMethods(NextRecipeActivity.this);
        loadingBar = new ProgressDialog(NextRecipeActivity.this);

        // Initialize the list of image URIs
        imageUris = new ArrayList<>();
        Intent intent = getIntent();
        imageUris = intent.getParcelableArrayListExtra(getString(R.string.selected_images));
        illegalUserActionPerformed = intent.getExtras().getBoolean("illegalUserActionPerformedFlag");

        // Initialize the image counter
        imageCounterTextView = findViewById(R.id.imageCounterNextRecipe);
        updateImageCounter(0); // Set the initial counter to 0

        // Set up the ViewPager
        viewPagerImages = findViewById(R.id.viewPager_next_recipe);
        adapter = new ImageAdapter(imageUris);
        viewPagerImages.setAdapter(adapter);

        // Add a page change listener to update the image counter when the current page changes
        viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateImageCounter(position);
            }
        });

        etIngredients = findViewById(R.id.etIngredients);
        addIngredients = findViewById(R.id.ib_add_ingredient);
        imageViews = new ArrayList<>();
        deleteImageButtons = new ArrayList<>();
        //    ========================= Get data from user =============================================
        this.createAllNumberPickers();
        this.createAllButtons();
        addIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngredientDialog();
            }
        });
    }


    private void updateImageCounter(int position) {
        int totalImages = imageUris.size();
        int currentImageIndex = position + 1;
        String counterText = currentImageIndex + "/" + totalImages;
        imageCounterTextView.setText(counterText);
    }


    public static String ingredients(ArrayList<String> ingredients) {
        String ingredient = "";
        if (ingredients != null) {
            for (int i = 0; i < ingredients.size(); i++) {
                ingredient = ingredient + ingredients.get(i).replace(":", " ") + "\n";
            }
            return ingredient;
        }
        return " There Are No Ingredients ";
    }


    private void createAllButtons() {
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(view -> {
            System.out.println("=============================");
            submitRecipe();
            System.out.println("============================");
        });
    }


    private void createAllNumberPickers() {
        category = findViewById(R.id.category);
        subCategory = findViewById(R.id.sub_category);
        etPostDescription = findViewById(R.id.etPostDescription);
        etTitle = findViewById(R.id.etTitle);
        etDirections = findViewById(R.id.etDirections);
        npCookingTime = findViewById(R.id.npCookTime);
        npCookingTime.setValue(-1);
        npCookingTime.setMaxValue(2000);
        npCookingTime.setMinValue(0);
        npCookingTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npCookingTime.setValue(newValue);
            }
        });
        npPrepTime = findViewById(R.id.npPrepTime);
        npPrepTime.setValue(-1);
        npPrepTime.setMaxValue(2000);
        npPrepTime.setMinValue(0);
        npPrepTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npPrepTime.setValue(newValue);
            }
        });
        npServings = findViewById(R.id.npServings);
        npServings.setValue(-1);
        npServings.setMaxValue(2000);
        npServings.setMinValue(0);
        npServings.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npServings.setValue(newValue);
            }
        });
        npCarbs = findViewById(R.id.npCarbs);
        npCarbs.setValue(-1);
        npCarbs.setMaxValue(2000);
        npCarbs.setMinValue(0);
        npCarbs.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npCarbs.setValue(newValue);
            }
        });
        npProtein = findViewById(R.id.npProtein);
        npProtein.setValue(-1);
        npProtein.setMaxValue(2000);
        npProtein.setMinValue(0);
        npProtein.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npProtein.setValue(newValue);
            }
        });
        npFat = findViewById(R.id.npFat);
        npFat.setValue(-1);
        npFat.setMaxValue(2000);
        npFat.setMinValue(0);
        npFat.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npFat.setValue(newValue);
            }
        });
        npCalories = findViewById(R.id.npCalories);
        npCalories.setValue(-1);
        npCalories.setMaxValue(2000);
        npCalories.setMinValue(0);
        npCalories.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                npCalories.setValue(newValue);
            }
        });
    }


    private void submitRecipe() {
        String title = etTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title cannot be empty");
            etTitle.requestFocus();
        }
        String caption = etPostDescription.getText().toString();
        if (TextUtils.isEmpty(caption)) {
            etPostDescription.setError("Description cannot be empty");
            etPostDescription.requestFocus();
        }

//                    List<String> ingredientsArray = new ArrayList<>(Arrays.asList(ingredients_list));
        List<String> ingredientsArray = new ArrayList<>(selectedIngredients);
        if (selectedIngredients.isEmpty()) {
            etIngredients.setError("Ingredients cannot be empty");
            etIngredients.requestFocus();
        }

        String directions = etDirections.getText().toString();
        String[] directions_list = directions.split("\n");
        if (TextUtils.isEmpty(directions)) {
            etDirections.setError("Directions cannot be empty");
            etDirections.requestFocus();
        } else if (category.getText().toString().isEmpty()) {
            category.setError("Category cannot be empty");
            category.requestFocus();
        } else if (subCategory.getText().toString().isEmpty()) {
            subCategory.setError("Sub category cannot be empty");
            subCategory.requestFocus();
        } else if (npPrepTime.getValue() == 0 || npCookingTime.getValue() == 0 ||
                npServings.getValue() == 0 || npProtein.getValue() == 0 ||
                npFat.getValue() == 0 || npCarbs.getValue() == 0 || npCalories.getValue() == 0) {
            Toast.makeText(getApplicationContext(), "All bottom half must be filled too!",
                    Toast.LENGTH_SHORT).show();
        } else {
            List<String> directionsArray = new ArrayList<>(Arrays.asList(directions_list));
            Recipe r = new Recipe(title, category.getText().toString(), subCategory.getText().toString(), new ArrayList<>(), directionsArray,
                    calcTime(npPrepTime.getValue()),
                    calcTime(npCookingTime.getValue()) + "",
                    npServings.getValue() + "", npProtein.getValue() + "",
                    npCalories.getValue() + "", npFat.getValue() + "",
                    npCarbs.getValue() + "", "");
            r.setIngredients(ingredientsArray);
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                r.setCopy_rights(currentUser.getUid());
            }

            uploadImageToStorageAndUploadPost(imageUris, r);
            //            ServerTry(imageUris,r);
        }
    }


    private void uploadImageToStorageAndUploadPost(List<Uri> imageUris, Recipe r) {
        loadingBar.setTitle("Upload Post");
        loadingBar.setMessage("Uploading....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        String uuid_post = createHash();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("photos_posts");
        List<Task<Uri>> uploadTasks = new ArrayList<>();


        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
            final StorageReference filePath = storageReference.child(mAuth.getCurrentUser().getUid()).child(uuid_post + i + ".jpg");

            uploadTasks.add(filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }));
        }


        Tasks.whenAllComplete(uploadTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if (task.isSuccessful()) {
                    List<String> downloadUrls = new ArrayList<>();

                    for (Task<?> uploadTask : task.getResult()) {
                        if (uploadTask.isSuccessful()) {
                            Uri downloadUri = (Uri) uploadTask.getResult();
                            downloadUrls.add(downloadUri.toString());
                        } else {
                            // Handle individual upload failures
                            Exception exception = uploadTask.getException();
                            Toast.makeText(NextRecipeActivity.this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (!downloadUrls.isEmpty()) {
                        // All images uploaded successfully
                        HashMap<String, Object> uploadPost = createPost(r, uuid_post, downloadUrls);
                        String uid = mAuth.getCurrentUser().getUid();
                        Call<Void> call = serverMethods.retrofitInterface.uploadNewPost(uid, uploadPost);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    if (illegalUserActionPerformed) {
                                        // Report him
                                        reportIllegalAction(uid, uuid_post);
                                        Toast.makeText(NextRecipeActivity.this, "Post Uploaded: " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        System.out.println("\n\n=============== Don't Report him - legal action ================\n\n");
                                        Toast.makeText(NextRecipeActivity.this, "Post Uploaded: " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(NextRecipeActivity.this, HomeActivity.class));
                                    }

                                    selectedIngredients.clear();
//                                    startActivity(new Intent(NextRecipeActivity.this, HomeActivity.class));
                                } else {
                                    Toast.makeText(NextRecipeActivity.this, "Upload Post failed" + response.message(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Toast.makeText(NextRecipeActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else {
                    // Handle task completion failure
                    Toast.makeText(NextRecipeActivity.this, "Failed to upload images: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            }
        });
    }

    private void reportIllegalAction(String uid, String uuid_post) {
        serverMethods.retrofitInterface.reportIllegalPost(uid, uuid_post).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("\n\nreportIllegalAction - =============== Reported him ================\n\n");
                    startActivity(new Intent(NextRecipeActivity.this, HomeActivity.class));
                }
                else {
                    System.out.println("\n\nreportIllegalAction - =============== Failed to Reported him ================\n\n");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                System.out.println("\n\nreportIllegalAction - onFailure - =============== Failed to Reported him ================\n\n");
            }
        });
    }

//    private void ServerTry(List<Uri> imageUris,Recipe r){
//        String uuid_post = createHash();
//        List<String> avi = new ArrayList<>();
//        avi.add("nnn");
//        avi.add("aaa");
//        Post post = new Post();
//        HashMap<String, Object> uploadPost = post.PostMapForServer(r,etPostDescription.getText().toString(), timeStamp(), avi, "kkkk", mAuth.getCurrentUser().getUid(), getTags(etPostDescription.getText().toString()));
//        Call<Void> call = serverMethods.retrofitInterface.uploadNewPost(mAuth.getCurrentUser().getUid(), uploadPost);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(NextRecipeActivity.this, "Post Uploaded: " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
//                    selectedIngredients.clear();
//                } else {
//                    Toast.makeText(NextRecipeActivity.this, "Upload Post failed" + response.message(), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                Toast.makeText(NextRecipeActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }


    private HashMap<String, Object> createPost(Recipe r, String post_uid, List<String> post_photos) {
        Post post = new Post();
        return post.PostMapForServer(r, etPostDescription.getText().toString(), timeStamp(), post_photos, null, null, null, post_uid, mAuth.getCurrentUser().getUid(), getTags(etPostDescription.getText().toString()));
    }


    private String getTags(String caption) {
        if (caption.indexOf("@") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = caption.toCharArray();
            boolean foundWord = false;
            for (char c : charArray) {
                if (c == '@') {
                    foundWord = true;
                    sb.append(c);
                } else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("@", ",@");
            return s.substring(1, s.length());
        }
        return caption;
    }


    private String createHash() {
        return "post_" + UUID.randomUUID().toString();
    }


    private void moveToActivity(String title) {
        Toast.makeText(getApplicationContext(), title + "> was uploaded!", Toast.LENGTH_LONG).show();
        selectedIngredients.clear();
        startActivity(new Intent(NextRecipeActivity.this, SearchActivity.class));
    }


    private String timeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);

        return dateFormat.format(new Date());
    }


    private String calcTime(int min) {
        if (min < 60) {
            return min + " mins";
        } else {
            return (int) min / 60 + " hrs " + min % 60 + " mins";
        }
    }

    public void addIngredientDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.insert_ingre_page);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        submit = dialog.findViewById(R.id.submitIngre);
        listView = dialog.findViewById(R.id.product_list);
        autoCompleteSearchView = dialog.findViewById(R.id.searchView);
        dialog.show();
        ingredientNamesList = new ArrayList<>();

        if (selectedIngredients == null || selectedIngredients.size() == 0) {
            selectedIngredients = new ArrayList<>();
        }
//        InitAutoCompleteSearchView(dialog);
        loadIngredients(dialog);
        adapterIngredients = new ListIngredientsAdapter(dialog.getContext(), selectedIngredients, "add");
        listView.setAdapter(adapterIngredients);

        submit.setOnClickListener(view -> {
            String data = ingredients(selectedIngredients);
            System.out.println(data);
            etIngredients.setText(data);
            dialog.dismiss();
        });


    }

//    // Set the filter names list + set adapter for the autoCompleteSearchView - Ingredients
//    private void InitAutoCompleteSearchView(Dialog d) {
//
//        Call<String[]> call = serverMethods.retrofitInterface.getIngredients();
//
//        call.enqueue(new Callback<String[]>() {
//            @Override
//            public void onResponse(@NonNull Call<String[]> call, @NonNull Response<String[]> response) {
//                String[] arrIngredients = response.body();
//                if (response.code() == 200) {
//                    if(arrIngredients != null) {
//                        Toast.makeText(NextRecipeActivity.this,
//                                "Success load ingredients", Toast.LENGTH_LONG).show();
//                        ingredientNamesList = new ArrayList<String>(Arrays.asList(arrIngredients));
//                        arraySearchAdapter = new ArrayAdapter<>(NextRecipeActivity.this, android.R.layout.simple_list_item_activated_1, ingredientNamesList);
//                        autoCompleteSearchView.setAdapter(arraySearchAdapter);
//                        autoCompleteSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
//                                String ingredient = parent.getItemAtPosition(pos).toString();
//                                addIngredientAmountDialog(ingredient, d);
//                                autoCompleteSearchView.setText("");
//
//                            }
//                        });
//                        autoCompleteSearchView.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
//                            @Override
//                            public void onDismiss() {
//                                // Hide my keyboard
//                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(d.getCurrentFocus().getApplicationWindowToken(), 0);
//                            }
//                        });
//                    }
//                } else if (response.code() == 400) {
//                    Toast.makeText(NextRecipeActivity.this,
//                            "failed load ingredients", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(NextRecipeActivity.this, response.message(),
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String[]> call, @NonNull Throwable t) {
//                Toast.makeText(NextRecipeActivity.this, t.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }


    private void loadIngredients(Dialog d) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("utils").document("ingredients");

        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> ingredients = (List<String>) document.get("ingredients");
                        if (ingredients != null) {
                            Toast.makeText(NextRecipeActivity.this, "Success load ingredients", Toast.LENGTH_LONG).show();
                            ingredientNamesList = new ArrayList<>(ingredients);
                            arraySearchAdapter = new ArrayAdapter<>(NextRecipeActivity.this, android.R.layout.simple_list_item_activated_1, ingredientNamesList);
                            autoCompleteSearchView.setAdapter(arraySearchAdapter);
                            autoCompleteSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    String ingredient = parent.getItemAtPosition(pos).toString();
                                    addIngredientAmountDialog(ingredient, d);
                                    autoCompleteSearchView.setText("");
                                }
                            });
                            autoCompleteSearchView.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    // Hide the keyboard
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(d.getCurrentFocus().getApplicationWindowToken(), 0);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(NextRecipeActivity.this, "Failed to load ingredients: Document does not exist", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NextRecipeActivity.this, "Failed to fetch ingredients: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void addIngredientAmountDialog(String ingredient, Dialog d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NextRecipeActivity.this, androidx.appcompat.R.style.Base_V7_Theme_AppCompat_Dialog);
        EditText editTextGrams = new EditText(this);
        editTextGrams.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextGrams.setTextColor(Color.rgb(255, 255, 255));
        builder.setView(editTextGrams);
        builder.setCancelable(false);
        builder.setTitle("The " + ingredient + "'s Amount in [g]");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
//                System.out.println("category- " + category + "\tsubCategory- " + subCategory);
                if (editTextGrams.getText().toString().isEmpty() || editTextGrams.getText().toString().charAt(0) == '0') {
                    Toast.makeText(builder.getContext(), "Please try again", Toast.LENGTH_LONG).show();
                    addIngredientAmountDialog(ingredient, d);
                } else {
                    addItem(editTextGrams.getText().toString(), ingredient);
                    Toast.makeText(builder.getContext(), ingredient + " added successfully", Toast.LENGTH_LONG).show();

                }
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, which) -> {
//            InitAutoCompleteSearchView(d);
            loadIngredients(d);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void removeItem(int i) {
        selectedIngredients.remove(i);
        listView.setAdapter(adapterIngredients);
    }


    private static void addItem(String amount, String ingredient) {
        selectedIngredients.add(amount + ":" + ingredient);
        listView.setAdapter(adapterIngredients);
        adapterIngredients.notifyDataSetChanged();
    }


}


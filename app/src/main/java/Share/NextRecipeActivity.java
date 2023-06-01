package Share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.instafoodies.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import Utils.ServerMethods;

public class NextRecipeActivity extends AppCompatActivity {

    private List<Uri> imageUris;
    FirebaseAuth mAuth;
    private ImageAdapter adapter;
    private ViewPager2 viewPagerImages;
    private ServerMethods serverMethods;


    // These strings role is to help us sync between what the user see in the sub category
    // AutoCompleteTextView and his choice of main category
    String[] categoriesList;
    String category = "";
    String subCategory = "";
    boolean flag;

    // Text inputs from the user
    TextInputEditText etTitle;
    TextView etIngredients;
    ImageButton addIngredients;
    TextInputEditText etDirections;
    // Main & sub categories as the user wish
    AutoCompleteTextView autoCompleteCategory;
    ArrayAdapter<String> adapterCategories;
    AutoCompleteTextView autoCompleteSubCategory;
    ArrayAdapter<String> adapterSubCategories;
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
    ImageButton submit;
    ArrayAdapter<String> arraySearchAdapter;

    ArrayList<String> ingredientNamesList;
    static ArrayList<String> selectedIngredients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_recipe);

        mAuth = FirebaseAuth.getInstance();
        serverMethods = new ServerMethods(NextRecipeActivity.this);

        // Initialize the list of image URIs
        imageUris = new ArrayList<>();
        imageUris = getIntent().getParcelableArrayListExtra(getString(R.string.selected_images));

        // Set up the ViewPager
        viewPagerImages = findViewById(R.id.viewPager_next_recipe);
        adapter = new ImageAdapter(imageUris);
        viewPagerImages.setAdapter(adapter);

    }
}
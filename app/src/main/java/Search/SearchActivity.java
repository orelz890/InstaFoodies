package Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.instafoodies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Profile.ProfileActivity;
import Utils.BottomNavigationViewHelper;
import Utils.ServerMethods;
import Utils.UserListAdapter;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;


public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private Context mContext = SearchActivity.this;
    private static final int ACTIVITY_NUM = 1;
    private ServerMethods serverMethods;
    private FirebaseFirestore db;
    private CollectionReference usersCollection;


    //Widgets
    private EditText mSearchParam;
    private ListView mListView;

    //Vars
    private List<UserSettings> mUserList;
    private UserListAdapter mAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchParam = (EditText) findViewById(R.id.search);
        mListView = (ListView)findViewById(R.id.listView);
        Log.d(TAG, "onCreate: started.");

        serverMethods = new ServerMethods(mContext);
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        hideSoftKeyboard();
        setupBottomNavigationView();
        initTextListener();
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");
        mUserList = new ArrayList<>();
        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mSearchParam.getText().toString();
                try {
                    searchForMatch(text);
                } catch (Exception e) {
                    e.printStackTrace();
                    //System.out.println("text from search bar was:"+text+"faild on func search for match");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                String text = mSearchParam.getText().toString();
//                try {
//                    searchForMatch(text);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    //System.out.println("text from search bar was:"+text+"faild on func search for match");
//
//                }
            }
        });
    }
    private void searchForMatch(String keyword) throws ExecutionException, InterruptedException {
        Log.d(TAG, "searchformatch: searching for a match: " + keyword);
        mUserList.clear();
        // Update users list
        if (keyword.length() == 0) {
            // Add logic for an empty keyword
        } else {
            // Perform a prefix search query
            String prefix = keyword; // The prefix entered by the user
            Query query = usersCollection.whereGreaterThanOrEqualTo("username", prefix)
                    .whereLessThan("username", prefix + "\uf8ff");

            // Retrieve the matching users
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Access the user data
                        DocumentReference userRef = document.getReference();
                        userRef.get().addOnCompleteListener(userDataTask -> {
                            if (userDataTask.isSuccessful()) {
                                DocumentSnapshot userData = userDataTask.getResult();
                                if (userData.exists()) {
                                    User user = userData.toObject(User.class);  // Convert DocumentSnapshot to User instance
                                    assert user != null;
                                    addUserAndUserAccountSettingsToList(user);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void addUserAndUserAccountSettingsToList(User user) {
        db.collection("users_account_settings")
                .document(user.getUser_id())
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document1 = task1.getResult();
                        if (document1 != null && document1.exists()) {
                            UserAccountSettings userAccountSettings = document1.toObject(UserAccountSettings.class);
                            UserSettings userSettings = new UserSettings(user,userAccountSettings);
                            // Check if the user already exists in the list
                            boolean userExists = false;
                            for (UserSettings settings : mUserList) {
                                if (settings.getUser().getUser_id().equals(user.getUser_id())) {
                                    userExists = true;
                                    break;
                                }
                            }
                            // Add the user only if it doesn't exist in the list
                            if (!userExists) {
                                mUserList.add(userSettings);
                                updateUsersList();
                            }
                        } else {
                            Log.d(TAG, "Error getting user account settings: " );

                        }
                    } else {
                        // Handle failures
                        Exception e = task1.getException();
                        if (e != null) {
                            Log.d(TAG, "Error getting user account settings: " + e.getMessage());
                            Toast.makeText(mContext, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");
        mAdpter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);
        mListView.setAdapter(mAdpter);
        mAdpter.notifyDataSetChanged();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"onItemClick: selected user: " + mUserList.get(position).toString());

                //navigate to profile activity
                Intent intent = new Intent((SearchActivity.this), (ProfileActivity.class));
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user),mUserList.get(position));
                startActivity(intent);
            }
        });
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}


package Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import Utils.FirebaseMethods;
import Utils.ServerMethods;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String email, username, password, fullname, paypalClientId;
    private EditText mEmail, mPassword, mUsername, mFullName, etPaypalClientId, editText;
    private TextView loadingPleaseWait, tvTitle;
    private Button btnRegister;
    private ProgressBar mProgressBar;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private ServerMethods serverMethods;

    private Boolean isBusiness;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        serverMethods = new ServerMethods(mContext);
        Log.d(TAG, "onCreate: started.");

        initWidgets();
        setupFirebaseAuth();
        init();
    }

    private void init() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvTitle.getText().toString();

                if (text.equals("Choose email")) {
                    email = editText.getText().toString();
                    editText.setText("");
                    tvTitle.setText("Choose username");
                    editText.setHint("Username");
                } else if (text.equals("Choose username")) {
                    username = editText.getText().toString();
                    editText.setText("");
                    tvTitle.setText("Write your full name");
                    editText.setHint("Full name");
                } else if (text.equals("Write your full name")) {
                    fullname = editText.getText().toString();
                    editText.setText("");
                    tvTitle.setText("Choose password");
                    editText.setHint("password");
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                if (!isBusiness) {
                        btnRegister.setText("Register");
                    }
                } else if (text.equals("Choose password")) {
                    password = editText.getText().toString();
                    editText.setText("");
                    if (isBusiness) {
                        tvTitle.setText("Choose paypal client id");
                        editText.setHint("Client id");
                        btnRegister.setText("Register");
                    } else if (checkInputs(email, username, password)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        loadingPleaseWait.setVisibility(View.VISIBLE);
                        firebaseMethods.registerNewEmail(email, password, username, mProgressBar, loadingPleaseWait);
                    }
                } else if (text.equals("Choose paypal client id")) {
                    paypalClientId = editText.getText().toString();
                    editText.setText("");
                    if (checkInputs(email, username, password)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        loadingPleaseWait.setVisibility(View.VISIBLE);
                        firebaseMethods.registerNewEmail(email, password, username, mProgressBar, loadingPleaseWait);
                    }
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password) {
        Log.d(TAG, "checkInputs: checking inputs for null values.\nemail = " + email + "\nusername = " + username + "pass = " + password + "\n\n");
        if (email.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Initialize the activity widgets
     */
    private void initWidgets() {
        Log.d(TAG, "initWidgets: Initializing Widgets.");

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        editText = (EditText) findViewById(R.id.editText);
        editText.setHint("Email");

//        mEmail = (EditText) findViewById(R.id.input_email_re);
//        mUsername = (EditText) findViewById(R.id.input_username);
//        mFullName = (EditText) findViewById(R.id.input_full_name);
//        etPaypalClientId = (EditText) findViewById(R.id.etPaypalClientId);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingPleaseWait = (TextView) findViewById(R.id.loadingPleaseWait);
//        mPassword = (EditText) findViewById(R.id.input_password_re);
        mContext = RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);
        loadingPleaseWait.setVisibility(View.GONE);

        Intent intent = getIntent();
        String userType = intent.getExtras().getString("userType");
        if (userType.equals("business")) {
//            etPaypalClientId.setVisibility(View.VISIBLE);
            isBusiness = true;
        } else {
//            etPaypalClientId.setVisibility(View.GONE);
            isBusiness = false;
        }

    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");

        if (string.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                String Uid = user.getUid();
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                Call<Boolean> call = serverMethods.retrofitInterface.executeCheckUserName(username);

                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

                        Boolean result = response.body();
                        Log.d(TAG, "executeCheckUserName" + result);
                        if (response.code() == 200) {
                            if (Boolean.TRUE.equals(result)) {
                                System.out.println("full_name = " + fullname + "\n");
                                User user1 = new User(password, mAuth.getCurrentUser().getUid(), email, "", username, fullname);
                                HashMap<String, Object> stringObjectHashMap = user1.userMapForServer();
                                if (isBusiness) {
                                    stringObjectHashMap.put("paypalClientId", paypalClientId);
                                }
                                System.out.println("stringObjectHashMap = " + stringObjectHashMap.toString() + "\n");


                                Call<User> call2 = serverMethods.retrofitInterface.executeSignup(stringObjectHashMap);
                                call2.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                                        if (response.code() == 200) {
                                            Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        } else if (response.code() == 400) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Already registered", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, response.message(),
                                                    Toast.LENGTH_LONG).show();
                                        }
//                                        Intent intent = new Intent(mContext,LoginActivity.class);
//                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                        Toast.makeText(RegisterActivity.this, t.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, "This Name Is Already Exists",
                                        Toast.LENGTH_LONG).show();
                                mEmail.setError("This Name Is Already Exists");
                                mEmail.requestFocus();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {

                    }
                });
                finish();

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


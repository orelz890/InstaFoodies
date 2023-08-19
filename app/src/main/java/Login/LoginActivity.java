package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import Home.HomeActivity;
import Server.ClientInfo;
import Server.RetrofitInterface;
import Utils.ServerMethods;
import models.User;
import models.UserAccountSettings;
import okhttp3.OkHttpClient;
import payment.PaypalActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;
    private ServerMethods serverMethods;


    String ipAddress = (new ClientInfo()).getIpAddress();
    String email;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
//    The IP address 10.0.2.2 is a special alias to your host loopback interface (localhost)
//    on the development machine when you are running an Android emulator

    private String BASE_URL = "http://10.0.2.2:8080";
    //    private String BASE_URL = "https://10.0.2.2:443";
//    private String BASE_URL = "http://" + ipAddress + ":8080";
    //    private String BASE_URL = "http://localhost:8080";
    private User user;
    Button loginBtn;
    EditText emailEdit;
    EditText passwordEdit;
//    View acb_getUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        serverMethods = new ServerMethods(mContext);

//        FirebaseApp.initializeApp(this);


        loginBtn = findViewById(R.id.btn_login);
        emailEdit = findViewById(R.id.input_email);
        passwordEdit = findViewById(R.id.input_password);
//        acb_getUser = findViewById(R.id.acb_getUser);

        setContentView(R.layout.activity_login);
        mProgressBar = (ProgressBar) findViewById(R.id.login_request_loading_progress_bar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);


        setupFirebaseAuth();
        init();


        OkHttpClient client = null;
        try {
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(getSSLContext().getSocketFactory(),
                            (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        retrofitInterface = retrofit.create(RetrofitInterface.class);

//        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleLoginDialog();
//            }
//        });
//
//        findViewById(R.id.link_sign_up).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                handleSignupDialog();
//            }
//        });
//
//        findViewById(R.id.acb_getUser).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tast();
////                handleGetUserDialog();
//            }
//        });
//
//        findViewById(R.id.acb_delUser).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleDelUserDialog(view);
//            }
//        });
//        findViewById(R.id.acb_patchUser).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //handlePatchUserDialog();
//            }
//        });
    }

    private void tast() {
        Intent test_intent = new Intent(this, Notifications.reminder.class);
        startActivity(test_intent);
    }

    private SSLContext getSSLContext() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        trustManagerFactory.init(keyStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
    };

//    private void handlePatchUserDialog() {
//        String email = emailEdit.getText().toString();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("email", email);
////        map.put("username", "orel");
//        map.put("display_name", "baruch");
//
////        Call<Void> call = retrofitInterface.executePatchUser(map);
//        Call<Void> call = retrofitInterface.executePatchUserAccountSettings(map);
//
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//
//                if (response.code() == 200) {
//                    Toast.makeText(LoginActivity.this, "Updated user: " + email,
//                            Toast.LENGTH_LONG).show();
//                } else if (response.code() == 404) {
//                    Toast.makeText(LoginActivity.this, "Wrong Credentials: " + response.message(),
//                            Toast.LENGTH_LONG).show();
//                }
//                else {
//                    Toast.makeText(LoginActivity.this, response.message(),
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "onFailure: " + t.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void handleLoginDialog() {
        Intent main_acticity_intent = new Intent(this, HomeActivity.class);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startUserTypeDialog();

                email = emailEdit.getText().toString();
                String pass = passwordEdit.getText().toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", pass);
//                Toast.makeText(LoginActivity.this, "pass= "+pass+", len= "+pass.length(),
//                        Toast.LENGTH_LONG).show();
                Date currentDate = new Date();
                String ifModifiedSince = currentDate.toString();
                Call<User> call = retrofitInterface.executeLogin(map);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                        if (response.code() == 200) {
//                            System.out.println("\n\n\n" + response.body() + "\n\n\n");
                            user = response.body();
                            assert user != null;

                            Toast.makeText(LoginActivity.this, "Name: " + user.getUsername(),
                                    Toast.LENGTH_LONG).show();
                            startActivity(main_acticity_intent);
                            finish();

//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
//                            builder1.setTitle(result.getName());
//                            builder1.setMessage(result.getEmail());
//
//                            builder1.show();

                        } else if (response.code() == 404 || response.code() == 400) {
                            Toast.makeText(LoginActivity.this, "Wrong Credentials",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, response.message(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void startUserTypeDialog() {
        CustomDialogTemplate customDialog = new CustomDialogTemplate( this);
        customDialog.show();

        customDialog.seTitle("User Type")
                .seMessage("Which account do you want to open?\n\nBusiness - can charge followers")
                .setButtons("Regular", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle regular account button click
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        intent.putExtra("userType", "regular");
                        startActivity(intent);
                        customDialog.dismiss();
                    }
                },"Business", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle business account button click
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                intent.putExtra("userType", "business");
                                startActivity(intent);
                                customDialog.dismiss();
                            }
                        });

    }


    private void handleSignupDialog() {
        Intent main_acticity_intent = new Intent(this, HomeActivity.class);

        View viewById = findViewById(R.id.link_sign_up);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEdit.getText().toString();
                String pass = passwordEdit.getText().toString();

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", "baruch");
                map.put("email", email);
                map.put("password", pass);

//                User user1 = new User("baruch", emailEdit.getText().toString(),passwordEdit.getText().toString());
//                HashMap<String, Object> stringObjectHashMap = user1.userHash();

                Call<User> call = retrofitInterface.executeSignup(map);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                        if (response.code() == 200) {
                            user = response.body();
                            assert user != null;
                            Toast.makeText(LoginActivity.this,
                                    user.getUsername() + ": Signed up successfully", Toast.LENGTH_LONG).show();
                            startActivity(main_acticity_intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this,
                                    "Already registered", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

//
//    private void handleGetUserDialog() {
//
//        acb_getUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Call<UserAccountSettings> call = retrofitInterface.executeGetUser(email);
//
//                call.enqueue(new Callback<UserAccountSettings>() {
//                    @Override
//                    public void onResponse(@NonNull Call<UserAccountSettings> call, @NonNull Response<UserAccountSettings> response) {
//
//                        UserAccountSettings result = response.body();
//                        if (response.code() == 200) {
//                            assert result != null;
//                            Toast.makeText(LoginActivity.this,
//                                    "Name: " + result.getUsername(), Toast.LENGTH_LONG).show();
//                        } else if (response.code() == 400) {
//                            Toast.makeText(LoginActivity.this,
//                                    "Don't exist", Toast.LENGTH_LONG).show();
//                        }
//                        else {
//                            Toast.makeText(LoginActivity.this, response.message(),
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<UserAccountSettings> call, @NonNull Throwable t) {
//                        Toast.makeText(LoginActivity.this, t.getMessage(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });
//    }

    private void handleDelUserDialog(View viewById) {

        String ref = "users";

//        Call<Void> call = retrofitInterface.executeDeleteObjectFromRef(ref, email);
//
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//
//                if (response.code() == 200) {
//                    Toast.makeText(LoginActivity.this,
//                            "successfully deleted!", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(LoginActivity.this,
//                            "Failed to delete.. " + response.message(), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                Toast.makeText(LoginActivity.this, t.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });

        String name = emailEdit.getText().toString();

        Call<Boolean> call = retrofitInterface.executeCheckUserName(name);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

                if (response.code() == 200) {
                    Toast.makeText(LoginActivity.this,
                            response.message(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
------------------------------------ Firebase ---------------------------------------------
*/
    private void init(){

        //initialize the button for logging in
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        Toast.makeText(LoginActivity.this, "Failed to Authenticate",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                    } else {
                                        try {
                                            if (user.isEmailVerified()) {
                                                Log.d(TAG, "onComplete: success. email is verified.");
                                                updateToken();
                                            } else {
                                                Toast.makeText(mContext, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                        }
                                    }

                                    // ...
                                }
                            });
                }

            }
        });

        TextView linkSignUp = (TextView) findViewById(R.id.link_sign_up);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                startUserTypeDialog();

            }
        });

         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
        if (mAuth.getCurrentUser() != null) {
            updateToken();
        }
    }

    private void updateToken() {
        Log.d(TAG, " updateToken.");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        // Use the device token as needed
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("token", token);

                        FirebaseFirestore.getInstance().collection("users")
                                .document(uid)
                                .update(tokenMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Token updated successfully");
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating token", e);
                                    }
                                });

                        Log.d("FCM Token", token);
                    } else {
                        Log.e("FCM Token", "Error getting token: " + task.getException());
                    }
                });
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
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

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

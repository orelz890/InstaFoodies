package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.instafoodies.R;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import Home.MainActivity;
import Server.ClientInfo;
import Server.RetrofitInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
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
    View acb_getUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.btn_login);
        emailEdit = findViewById(R.id.input_email);
        passwordEdit = findViewById(R.id.input_password);
        acb_getUser = findViewById(R.id.acb_getUser);


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
        }
        catch (Exception e) {
            e.printStackTrace();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        retrofitInterface = retrofit.create(RetrofitInterface.class);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginDialog();
            }
        });

        findViewById(R.id.link_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignupDialog();
            }
        });

        findViewById(R.id.acb_getUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleGetUserDialog();
            }
        });

        findViewById(R.id.acb_delUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDelUserDialog(view);
            }
        });
        findViewById(R.id.acb_patchUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePatchUserDialog();
            }
        });
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

    private TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }
    };

    private void handlePatchUserDialog() {
        String email = emailEdit.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", email);
//        map.put("username", "orel");
        map.put("display_name", "baruch");

//        Call<Void> call = retrofitInterface.executePatchUser(map);
        Call<Void> call = retrofitInterface.executePatchUserAccountSettings(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(LoginActivity.this, "Updated user: " + email,
                            Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Wrong Credentials: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "onFailure: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLoginDialog() {
        Intent main_acticity_intent = new Intent(this, MainActivity.class);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        }
                        else {
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

    public String showNameInputDialog() {
        // Create a new dialog box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Create the name input EditText
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Enter your name");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 50, 50, 0);
        nameInput.setLayoutParams(params);

        // Create the OK button
        Button okButton = new Button(this);
        okButton.setText("OK");
        okButton.setLayoutParams(params);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                // Do something with the name input, such as displaying it in a TextView
                dialog.dismiss();
            }
        });

        // Create the Cancel button
        Button cancelButton = new Button(this);
        cancelButton.setText("Cancel");
        cancelButton.setLayoutParams(params);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Create the layout and add the name input and buttons
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(nameInput);
        layout.addView(okButton);
        layout.addView(cancelButton);

        // Set the dialog's content view to the layout
        dialog.setContentView(layout);

        // Show the dialog box
        dialog.show();
        return ""+nameInput.getText();
    }

    private void handleSignupDialog() {
        Intent main_acticity_intent = new Intent(this, MainActivity.class);

        View viewById = findViewById(R.id.link_sign_up);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEdit.getText().toString();
                String pass = passwordEdit.getText().toString();

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", showNameInputDialog());
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
                        }
                        else {
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


    private void handleGetUserDialog() {

        acb_getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<User_account_settings> call = retrofitInterface.executeGetUser(email);

                call.enqueue(new Callback<User_account_settings>() {
                    @Override
                    public void onResponse(@NonNull Call<User_account_settings> call, @NonNull Response<User_account_settings> response) {

                        User_account_settings result = response.body();
                        if (response.code() == 200) {
                            assert result != null;
                            Toast.makeText(LoginActivity.this,
                                    "Name: " + result.getUsername(), Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this,
                                    "Don't exist", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User_account_settings> call, @NonNull Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void handleDelUserDialog(View viewById) {

        String ref = "users";

        Call<Void> call = retrofitInterface.executeDeleteObjectFromRef(ref, email);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(LoginActivity.this,
                            "successfully deleted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Failed to delete.. " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}

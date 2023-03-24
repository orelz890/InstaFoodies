package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instafoodies.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Server.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8080";
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

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

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

    private void handlePatchUserDialog() {
        String email = emailEdit.getText().toString();

        Call<User> call = retrofitInterface.executeGetUser(email);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if (response.code() == 200) {

                    User user = response.body();
                    if (user != null) {
                        // >>>>>> If you want to change more attributes do so here <<<<<<
                        String pass = passwordEdit.getText().toString();
                        user.setPasswordHash(pass);
                        ArrayList<String> f63 = new ArrayList<>();
                        f63.add("aaa");
                        user.setFollowing(f63);
                        Call<Void> call2 = retrofitInterface.executePatchUser(email, user.userHash());

                        call2.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                                if (response.code() == 200) {
                                    Toast.makeText(LoginActivity.this, "Updated user: " + email,
                                            Toast.LENGTH_LONG).show();
                                } else if (response.code() == 404) {
                                    Toast.makeText(LoginActivity.this, "Wrong Credentials: " + response.message(),
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
                } else if (response.code() == 400) {
                    Toast.makeText(LoginActivity.this,
                            "handlePatchUserDialog: User is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLoginDialog() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String pass = passwordEdit.getText().toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", pass);
                Toast.makeText(LoginActivity.this, "pass= "+pass+", len= "+pass.length(),
                        Toast.LENGTH_LONG).show();
                Call<User> call = retrofitInterface.executeLogin(map);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                        if (response.code() == 200) {
//                            System.out.println("\n\n\n" + response.body() + "\n\n\n");
                            user = response.body();
                            assert user != null;
                            Toast.makeText(LoginActivity.this, "Name: " + user.getName(),
                                    Toast.LENGTH_LONG).show();
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
//                            builder1.setTitle(result.getName());
//                            builder1.setMessage(result.getEmail());
//
//                            builder1.show();

                        } else if (response.code() == 404) {
                            Toast.makeText(LoginActivity.this, "Wrong Credentials",
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


    private void handleSignupDialog() {

        View viewById = findViewById(R.id.link_sign_up);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
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
                                    user.getName() + ": Signed up successfully", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this,
                                    "Already registered", Toast.LENGTH_LONG).show();
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

                String email = emailEdit.getText().toString();
                Call<User> call = retrofitInterface.executeGetUser(email);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                        User result = response.body();
                        if (response.code() == 200) {
                            assert result != null;
                            Toast.makeText(LoginActivity.this,
                                    "Name: " + result.getName(), Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this,
                                    "Don't exist", Toast.LENGTH_LONG).show();
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

    private void handleDelUserDialog(View viewById) {

        String email = emailEdit.getText().toString();
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

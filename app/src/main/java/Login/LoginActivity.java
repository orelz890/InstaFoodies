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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                handlePatchUserDialog(view);
            }
        });
    }

    private void handlePatchUserDialog(View view) {
        final EditText emailEdit = findViewById(R.id.input_email);
        final EditText passwordEdit = findViewById(R.id.input_password);
        String email = emailEdit.getText().toString();
        String pass = passwordEdit.getText().toString();

//        String email = "orelzx13@gmail.com";
//        String pass = "000";

        HashMap<String, String> map = new HashMap<>();

        map.put("password", pass);
        Call<Void> call = retrofitInterface.executePatchUser(email, map);

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

            }

            @Override
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLoginDialog() {

        Button loginBtn = findViewById(R.id.btn_login);
        final EditText emailEdit = findViewById(R.id.input_email);
        final EditText passwordEdit = findViewById(R.id.input_password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> map = new HashMap<>();

                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<User> call = retrofitInterface.executeLogin(map);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                        if (response.code() == 200) {

                            User result = response.body();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            assert result != null;
                            builder1.setTitle(result.getName());
                            builder1.setMessage(result.getEmail());

                            builder1.show();

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
        final EditText emailEdit = findViewById(R.id.input_email);
        final EditText passwordEdit = findViewById(R.id.input_password);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> map = new HashMap<>();

                map.put("name", "baruch");
                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<Void> call = retrofitInterface.executeSignup(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                        if (response.code() == 200) {
                            Toast.makeText(LoginActivity.this,
                                    "Signed up successfully", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this,
                                    "Already registered", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }


    private void handleGetUserDialog() {
        View viewById = findViewById(R.id.acb_getUser);
        final EditText emailEdit = findViewById(R.id.input_email);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("email", emailEdit.getText().toString());
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
        final EditText emailEdit = findViewById(R.id.input_email);

        String email = emailEdit.getText().toString();
        String ref = "users";
//                retrofitInterface.executeDeleteObjectFromRef(ref, email);
        retrofitInterface.executeDeleteObjectFromRef(email);
    }

}

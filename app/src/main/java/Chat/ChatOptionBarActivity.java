//package Chat;
//
//import android.content.DialogInterface;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.instafoodies.R;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import java.security.KeyStore;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.TrustManagerFactory;
//import javax.net.ssl.X509TrustManager;
//
//import Server.RetrofitInterface;
//import okhttp3.OkHttpClient;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class ChatOptionBarActivity extends AppCompatActivity {
//
//    private MaterialToolbar mToolbar;
//
//    private Retrofit retrofit;
//    private RetrofitInterface retrofitInterface;
//    private String BASE_URL = "http://10.0.2.2:8080";
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.options_chat_menu, menu);
//
//        setupBar();
//
//        setupRetrofit();
//        return true;
//    }
//
//    private void setupBar() {
//        mToolbar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("ChatUp");
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        System.out.println("im here 2");
//        switch (item.getItemId()){
//            case R.id.item_new_group:
//
//                createNewGroup();
//                break;
//
//            default:
//                getUserGroups();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void getUserGroups() {
//        System.out.println("im here");
//        String uid = "eVkAc1hVnAOCdX8QCFFGxZqFU3c2";
//        Call<String[]> call = retrofitInterface.getUserChatGroups(uid);
//        call.enqueue(new Callback<String[]>() {
//            @Override
//            public void onResponse(Call<String[]> call, Response<String[]> response) {
//                if (response.code() == 200) {
//                    String[] groups = response.body();
//
//                    System.out.println(groups[0]);
//                }
//                else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String[]> call, Throwable t) {
//
//                System.out.println(t.getMessage());
//            }
//        });
//    }
//
//    private void createNewGroup() {
//        String uid = "eVkAc1hVnAOCdX8QCFFGxZqFU3c2";
//// Create an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(ChatOptionBarActivity.this);
//        builder.setTitle("Enter a String");
//
//        // Create an EditText view to allow user input
//        final EditText input = new EditText(ChatOptionBarActivity.this);
//        builder.setView(input);
//
//        // Set the positive button and its click listener
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String userInput = input.getText().toString();
//                // Do something with the user input
//                // For example, display the input in a TextView
//                Call<Void> call = retrofitInterface.createNewChatGroup(uid, userInput);
//                call.enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//
//                    }
//                });
//            }
//        });
//
//        // Set the negative button and its click listener
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // Create and show the AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
//
//
//    private void setupRetrofit() {
//        OkHttpClient client = null;
//        try {
//            client = new OkHttpClient.Builder()
//                    .sslSocketFactory(getSSLContext().getSocketFactory(),
//                            (X509TrustManager) trustAllCerts[0])
//                    .hostnameVerifier((hostname, session) -> true)
//                    .build();
//
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//
//
//        retrofitInterface = retrofit.create(RetrofitInterface.class);
//    }
//
//    private SSLContext getSSLContext() throws Exception {
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
//                TrustManagerFactory.getDefaultAlgorithm());
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(null, null);
//        trustManagerFactory.init(keyStore);
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//        return sslContext;
//    }
//
//    private TrustManager[] trustAllCerts = new TrustManager[] {
//            new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[]{};
//                }
//            }
//    };
//}

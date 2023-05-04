package Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import models.UserAccountSettings;
import Server.RetrofitInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServerMethods {

    private static final String TAG = "FirebaseMethods";


    private String userID;

    private Context mContext;
    FirebaseAuth mAuth;
    private Retrofit retrofit;
    public RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8080";


    public ServerMethods(Context context) {
        mContext = context;
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
        mAuth = FirebaseAuth.getInstance();


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


    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_account_Settings node
     *
     * @return
     */
    public UserAccountSettings getUserSettings() {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase.");

        UserAccountSettings user_account_settings = new UserAccountSettings();
        Call<UserAccountSettings> call = retrofitInterface.executeGetUser(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());

        call.enqueue(new Callback<UserAccountSettings>() {
            @Override
            public void onResponse(@NonNull Call<UserAccountSettings> call, @NonNull Response<UserAccountSettings> response) {

                UserAccountSettings result = response.body();
                if (response.code() == 200) {
                    assert result != null;
                    Toast.makeText(mContext,
                            "Name: " + result.getUsername(), Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    Toast.makeText(mContext,
                            "Don't exist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserAccountSettings> call, @NonNull Throwable t) {
                Toast.makeText(mContext, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        return user_account_settings;
    }

    public Boolean[] checkIfUsernameExists(String userName) {

        Boolean[] ans = {false};
        Call<Boolean> call = retrofitInterface.executeCheckUserName(userName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

                Boolean result = response.body();
                if (response.code() == 200) {
                    System.out.println("inside checkIfUsernameExists");
                    ans[0] = result;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {

            }
        });
        Log.d("ServerMethods: checkIfUsernameExists", ans[0].toString());
        return ans;
    }


}

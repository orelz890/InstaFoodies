package Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.instafoodies.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import Login.LoginActivity;
import Server.RetrofitInterface;
import okhttp3.Handshake;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ChatActivity extends AppCompatActivity {

    private Context mContext;

    private MaterialToolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8080";

    private TextView textViewGroupName;

    private int tabPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = ChatActivity.this;

        mToolbar = (MaterialToolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatUp");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        myViewPager = (ViewPager) findViewById(R.id.chat_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myViewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
                viewPager.setAdapter(newAdapter);
            }
        });

        myTabLayout = (TabLayout) findViewById(R.id.chat_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        myTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPos = tab.getPosition();
                System.out.println("tab pos = " + tabPos);
                myTabsAccessorAdapter.resetFragment(tabPos);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabPos = tab.getPosition();
                System.out.println("reselected tab pos = " + tabPos);
                myTabsAccessorAdapter.resetFragment(tabPos);


            }
        });

        textViewGroupName = (TextView) findViewById(R.id.textViewGroupName);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform the refresh action here

                myTabsAccessorAdapter.resetFragment(tabPos);


                // Stop the refreshing animation after the refresh action is complete
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        setupRetrofit();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.item_new_group:

                createNewGroup();
                break;

            default:
                getUserGroups();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getUserGroups() {
        String uid = "eVkAc1hVnAOCdX8QCFFGxZqFU3c2";
        Call<String[]> call = retrofitInterface.getUserChatGroups(uid);
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.code() == 200) {
                    String[] groups = response.body();




                }
                else {
                    Toast.makeText(ChatActivity.this, "Document does not exist, " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "onFailure: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
    }

    private void createNewGroup() {
        String uid = "eVkAc1hVnAOCdX8QCFFGxZqFU3c2";
// Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Enter a String");

        // Create an EditText view to allow user input
        final EditText input = new EditText(mContext);
        builder.setView(input);

        // Set the positive button and its click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();
                // Do something with the user input
                // For example, display the input in a TextView
                Call<Void> call = retrofitInterface.createNewChatGroup(uid, userInput);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

        // Set the negative button and its click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

// =========================================== Retrofit =============================

    private void setupRetrofit() {
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


}

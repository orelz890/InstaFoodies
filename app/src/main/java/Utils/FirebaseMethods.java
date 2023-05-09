package Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import Login.RegisterActivity;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    public Boolean EmailInInUse;
    private ServerMethods serverMethods;


    private Context mContext;

    public FirebaseMethods(Context context) {
        serverMethods = new ServerMethods(mContext);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Register a new email and password to Firebase Authentication
     *
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username, ProgressBar progressBar, TextView textView) {
        Call<Boolean> call = serverMethods.retrofitInterface.executeCheckUserName(username);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

                Boolean result = response.body();
                Log.d(TAG, "executeCheckUserName" + result);
                if (response.code() == 200) {
                    if (Boolean.TRUE.equals(result)) {

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            textView.setVisibility(View.GONE);
                                            Toast.makeText(mContext, "Failed to Authenticate (Try to changed email address)",
                                                    Toast.LENGTH_SHORT).show();

                                        } else if (task.isSuccessful()) {
                                            //send verificaton email
                                            sendVerificationEmail();
                                            userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                            Log.d(TAG, "onComplete: Auth state changed: " + userID);
                                        }

                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        Toast.makeText(mContext, "This Name Is Already Exists",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {

            }
        });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}




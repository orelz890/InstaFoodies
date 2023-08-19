package payment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import Utils.ServerMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaypalActivity extends AppCompatActivity {

    ImageView btn;
    EditText amount;

    public static final String clientId = "AbvMu5a2zAFu5qfPTwmu6RiavvuJxOLdRxw-qsD8oKSylPCCUpZlcn_HlOvja3V978u82JEOhZ7oJ4aq";
    public static final int PAPAL_REQUEST_CODE = 123;
    private static ServerMethods serverMethods;

    public static PayPalConfiguration configuration;
    private String uid;
    private String friendUid;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_paypal_page);
        mContext = PaypalActivity.this;

        // Server
        serverMethods = new ServerMethods(this);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btn = findViewById(R.id.btn);
        amount = findViewById(R.id.amount);
        amount.setEnabled(false);
        amount.setText("The cost is 1 ");

        Intent intent = getIntent();
        String usrClientId = intent.getExtras().getString("paypalClientId").toString();
        friendUid = intent.getExtras().getString("friendUid").toString();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usrClientId != null && !usrClientId.isEmpty()){
                    configuration = new PayPalConfiguration().environment(PayPalConfiguration
                            .ENVIRONMENT_SANDBOX).clientId(usrClientId);
                    getPayment();
                }
                else {
                    Toast.makeText(mContext, "Invalid client id", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void getPayment() {

        String amounts = "1";
//        String amounts = amount.getText().toString();


        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amounts)), "USD", "Learn", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation config = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (config != null) {
                    try {
                        JSONObject paymentResponse = config.toJSONObject();
                        JSONObject response = paymentResponse.getJSONObject("response");
                        String paymentId = response.getString("id");
                        String state = response.getString("state");

                        // If the payment was approved add the user to the business followers list
                        if ("approved".equals(state)) {
                            // Payment was successfully approved and transferred
                            Log.i("Payment", "Payment approved. ID: " + paymentId);
                            serverMethods.retrofitInterface.followUnfollow(uid,friendUid,true).enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(mContext, "Congratulations you are now a Follower :)",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(mContext, "Following failed :(",Toast.LENGTH_LONG).show();
                                    }
                                    finish();
                                }

                                @Override
                                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                                    Toast.makeText(mContext, "Following failed :(", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                        else {
                            // Payment was not approved or other status
                            Toast.makeText(mContext, "Payment not approved. State: " + state,Toast.LENGTH_LONG).show();
                            Log.e("Payment", "Payment not approved. State: " + state);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Error", "Error parsing PayPal payment response");
                        Toast.makeText(mContext, "Error parsing PayPal payment response",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Log.e("Error", "Payment confirmation object is null");
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "Payment canceled :(",Toast.LENGTH_LONG).show();
                Log.e("Payment", "Payment canceled");
                finish();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(mContext, "Invalid payment :(",Toast.LENGTH_LONG).show();
                Log.i("Payment", "Invalid payment");
                finish();

            }
        }
    }

    private void finishAndReturnToParentWithData(String dataToPass) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("dataToPass", dataToPass); // Put your data here
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
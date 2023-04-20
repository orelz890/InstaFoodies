package payment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.instafoodies.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class amount_paypal_page extends AppCompatActivity {

    ImageView btn;
    EditText amount;

    public static final String clientId = "AbvMu5a2zAFu5qfPTwmu6RiavvuJxOLdRxw-qsD8oKSylPCCUpZlcn_HlOvja3V978u82JEOhZ7oJ4aq";
    public static final int PAPAL_REQUEST_CODE = 123;

    public static PayPalConfiguration configuration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(clientId);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_paypal_page);

        btn = findViewById(R.id.btn);
        amount = findViewById(R.id.amount);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPayment();
            }
        });
    }

    private void getPayment() {

        String amounts = amount.getText().toString();
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amounts)),"USD","Learn",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this,PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);

        startActivityForResult(intent,PAPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAPAL_REQUEST_CODE){
            PaymentConfirmation config = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

        if (config!= null){

            try {
                String paymentDetails = config.toJSONObject().toString(4);

                JSONObject payobj = new JSONObject(paymentDetails);
            } catch (JSONException e) {
                e.printStackTrace();

                Log.e("Error", "somthing went wrong with paypal payment");
            }
        }
        } else if (requestCode == Activity.RESULT_CANCELED){
            Log.e("Error", "somthing went wrong with paypal payment");
        }
        else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Log.i("Pyment", "invalid payment");
        }
    }
}
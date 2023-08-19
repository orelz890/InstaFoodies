package payment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instafoodies.R;

import Search.SearchActivity;
import models.User;
import models.UserAccountSettings;
import payment.PaypalActivity;


public class CustomPaymentDialog extends Dialog {
    private TextView tvNoteAndAmount;
    private ImageView ivCancel, ivPaypal;

    private Context mContext;
    private String noteAndAmount;
    private UserAccountSettings friendSettings;
    private User friendUser;

    public CustomPaymentDialog(Context context, String noteAndAmount, UserAccountSettings friendSettings, User friendUser) {
        super(context);
        mContext = context;
        this.noteAndAmount = noteAndAmount;
        this.friendSettings = friendSettings;
        this.friendUser = friendUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_payment_custom);

        setupViews();
    }

    private void setupViews() {
        tvNoteAndAmount = findViewById(R.id.tvNoteAndAmount);
        ivCancel = findViewById(R.id.ivCancel);
        ivPaypal = findViewById(R.id.ivPaypal);

        if (!noteAndAmount.isEmpty()){
            tvNoteAndAmount.setText(noteAndAmount);
        }

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click here
                dismiss(); // Close the dialog
            }
        });

        ivPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PaypalActivity.class);
                intent.putExtra("paypalClientId", friendSettings.getPaypalClientId());
                intent.putExtra("friendUid", friendUser.getUser_id());
                mContext.startActivity(intent);
            }
        });
    }
}

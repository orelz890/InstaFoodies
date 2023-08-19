package Login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instafoodies.R;

public class CustomDialogTemplate extends Dialog {
    private TextView tvDialogTitle, tvMessage;
    private ImageView ivCancel;
    private Button regularAccountButton, businessAccountButton;
    private String dialogTitle, message, firstButtonText, secondButtonText;
    private View.OnClickListener regularAccountListener, businessAccountListener;

    public CustomDialogTemplate(Context context, String dialogTitle, String message,
                                String firstButtonText, String secondButtonText,
                                View.OnClickListener regularListener,
                                View.OnClickListener businessListener) {
        super(context);
        this.dialogTitle = dialogTitle;
        this.message = message;
        this.regularAccountListener = regularListener;
        this.businessAccountListener = businessListener;
        this.firstButtonText = firstButtonText;
        this.secondButtonText = secondButtonText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_user_type_costum);

        setupViews();
    }

    private void setupViews() {

        businessAccountButton = findViewById(R.id.businessAccountButton);
        businessAccountButton.setText(secondButtonText);

        tvDialogTitle = findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(dialogTitle);

        tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setText(message);


        ivCancel = findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        regularAccountButton = findViewById(R.id.regularAccountButton);
        regularAccountButton.setOnClickListener(regularAccountListener);

        regularAccountButton.setText(firstButtonText);
        businessAccountButton.setOnClickListener(businessAccountListener);
    }
}

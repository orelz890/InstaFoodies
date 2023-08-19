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

    public CustomDialogTemplate(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_user_type_costum);

        setupViews();
    }

    private void setupViews() {
        regularAccountButton = findViewById(R.id.regularAccountButton);
        businessAccountButton = findViewById(R.id.businessAccountButton);

        tvDialogTitle = findViewById(R.id.tvLoginDialogTitle);
        tvMessage = findViewById(R.id.tvMessage);


        ivCancel = findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public CustomDialogTemplate setButtons(String buttonName1, View.OnClickListener Listener1, String buttonName2, View.OnClickListener Listener2){
        regularAccountButton.setText(buttonName1);
        regularAccountButton.setOnClickListener(Listener1);

        businessAccountButton.setText(buttonName2);
        businessAccountButton.setOnClickListener(Listener2);

        return this;
    }


    public CustomDialogTemplate setCancel(View.OnClickListener cancelListener){
        ivCancel.setOnClickListener(cancelListener);

        return this;
    }


    public CustomDialogTemplate seMessage(String message){
        tvMessage.setText(message);
        return this;
    }


    public CustomDialogTemplate seTitle(String title){
        tvDialogTitle.setText(title);

        return this;
    }

}

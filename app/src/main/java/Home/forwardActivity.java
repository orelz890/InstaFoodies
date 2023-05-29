package Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.instafoodies.R;

import Chat.ChatActivity;
import MLKIT.object.ObjectDetectionActivity;
import MLKIT.text.SpamTextDetectionActivity;

public class forwardActivity extends AppCompatActivity {

    Button object;
    Button spam;
    Button chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);

        object = findViewById(R.id.button1);
        spam = findViewById(R.id.button2);
        chat = findViewById(R.id.button3);

        init();
    }

    private void init(){
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(forwardActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });

        spam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(forwardActivity.this, SpamTextDetectionActivity.class);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(forwardActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }



}
package Notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.instafoodies.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseMessagingHandler extends AppCompatActivity {

    private Button button;
    private String collapseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_messageing_handler);

        button = findViewById(R.id.sendFirebaseMessage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleNotification.schedule(FirebaseMessagingHandler.this);

                // Send an FCM message
                sendFCMMessage();

            }
        });
    }


    private void sendFCMMessage() {
        // Replace 'your_device_token' with the actual FCM token of the recipient device

        // Get the FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // Get the token and handle it
                        String deviceToken = task.getResult();

                        // Prepare the message payload
                        String title = "New Update";
                        String body = "Check out the latest features!";

                        // Create a data payload with custom data
                        Map<String, String> dataPayload = new HashMap<>();
                        dataPayload.put("customKey", "Recipe details");

                        RemoteMessage.Builder remoteMessageBuilder = new RemoteMessage.Builder(deviceToken)
                                .setMessageId(UUID.randomUUID().toString())
                                .setData(dataPayload)
                                .setTtl(3600)  // Time-to-live in seconds
                                .setCollapseKey("collapse_key");

                        // Set notification fields directly on the RemoteMessage object
                        remoteMessageBuilder
                                .addData("title", title)
                                .addData("body", body)
                                .addData("icon", "icon_name")
                                .addData("color", "#FF5733")
                                .addData("sound", "sound_uri")
                                .addData("tag", "notification_tag")
                                .addData("click_action", "OPEN_ACTIVITY")
                                .addData("body_loc_key", "body_localization_key")
                                .addData("body_loc_args", "arg1,arg2")
                                .addData("title_loc_key", "title_localization_key")
                                .addData("title_loc_args", "arg1");

                        // Send the FCM message
                        FirebaseMessaging.getInstance().send(remoteMessageBuilder.build());
                        Log.d("FCM", "FCM Token: " + deviceToken);
                    }
                    // Task was unsuccessful
                    else {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                });
    }
}
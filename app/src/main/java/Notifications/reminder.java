package Notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.instafoodies.R;

public class reminder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        creatNotificationCannel();
        
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {

                Toast.makeText(reminder.this, "Reminder set", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(reminder.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(reminder.this,0,intent, PendingIntent.FLAG_MUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeatbuttonclick = System.currentTimeMillis();

                long tenSecondsInMillis = 1;

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        timeatbuttonclick + tenSecondsInMillis,
                        pendingIntent);

            }
        });
    }

    private void creatNotificationCannel(){
        CharSequence name = "LemubitReminderChannel";
        String description =" Channel for Lemubit Reminder";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("notifylemubit",name,importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}
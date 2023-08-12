package Notifications;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.instafoodies.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class reminder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        creatNotificationCannel();
        
        Button button = findViewById(R.id.submitButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                TextInputEditText titleEditText = findViewById(R.id.titleET);
                TextInputEditText messageEditText = findViewById(R.id.massgeET);
                // Get references to the DatePicker and TimePicker
                DatePicker datePicker = findViewById(R.id.datepicer);
                TimePicker timePicker = findViewById(R.id.timepicer);

                // Get the user-entered values
                String title = titleEditText.getText().toString();
                String message = messageEditText.getText().toString();



                // Get the selected year, month, day, hour, and minute
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Create a Calendar instance to set the selected date and time
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute);

                // Convert the Calendar time to milliseconds
                long selectedTimeInMillis = calendar.getTimeInMillis();

                Intent intent = new Intent(reminder.this, ReminderBroadcast.class);
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.putExtra("selectedTime", selectedTimeInMillis); // Pass the selected time
                PendingIntent pendingIntent = PendingIntent.getBroadcast(reminder.this, 0, intent, PendingIntent.FLAG_MUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                long timeatbuttonclick = System.currentTimeMillis();
                long tenSecondsInMillis = 1;
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeatbuttonclick + tenSecondsInMillis, pendingIntent);
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




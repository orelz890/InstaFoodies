package Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.instafoodies.R;

public class ReminderBroadcast  extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
//        // Retrieve the selected time from the intent
//        long selectedTimeInMillis = intent.getLongExtra("selectedTime", 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifylemubit")
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle(title)  // Set the title from user input
                .setContentText(message) // Set the message from user input
                .setPriority(NotificationCompat.PRIORITY_HIGH);

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        PendingIntent notificationIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTimeInMillis, notificationIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200,builder.build());
    }


}

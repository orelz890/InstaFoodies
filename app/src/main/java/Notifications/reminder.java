//package Notifications;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.app.AlarmManager;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.TimePicker;
//import android.widget.Toast;
//import com.example.instafoodies.R;
//import com.google.android.material.textfield.TextInputEditText;
//
//import java.util.Calendar;
//
//import Login.LoginActivity;
//
//public class reminder extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reminder);
//        creatNotificationCannel();
//
//        Button button = findViewById(R.id.submitButton);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v ) {
//                TextInputEditText titleEditText = findViewById(R.id.titleET);
//                TextInputEditText messageEditText = findViewById(R.id.massgeET);
//                // Get references to the DatePicker and TimePicker
//                DatePicker datePicker = findViewById(R.id.datepicer);
//                TimePicker timePicker = findViewById(R.id.timepicer);
//
//                // Get the user-entered values
//                String title = titleEditText.getText().toString();
//                String message = messageEditText.getText().toString();
//
//
//
//                // Get the selected year, month, day, hour, and minute
//                int year = datePicker.getYear();
//                int month = datePicker.getMonth();
//                int day = datePicker.getDayOfMonth();
//                int hour = timePicker.getHour();
//                int minute = timePicker.getMinute();
//
//                // Create a Calendar instance to set the selected date and time
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(year, month, day, hour, minute);
//
//                // Convert the Calendar time to milliseconds
//                long selectedTimeInMillis = calendar.getTimeInMillis();
//
//                Intent intent = new Intent(reminder.this, ReminderBroadcast.class);
//                intent.putExtra("title", title);
//                intent.putExtra("message", message);
////                intent.putExtra("selectedTime", selectedTimeInMillis); // Pass the selected time
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(reminder.this, 0, intent, PendingIntent.FLAG_MUTABLE);
//
//                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                long delayInMillis = selectedTimeInMillis - System.currentTimeMillis();
//
////                long timeatbuttonclick = System.currentTimeMillis();
////                long tenSecondsInMillis = 1;
////                Toast.makeText(Notifications.reminder.this,
////                         timeatbuttonclick + ": time timeatbuttonclick" + selectedTimeInMillis + " time selectedTimeInMillis" , Toast.LENGTH_LONG).show();
////                alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTimeInMillis - timeatbuttonclick , pendingIntent);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pendingIntent);
//
//            }
//        });
//    }
//
//    private void creatNotificationCannel(){
//        CharSequence name = "LemubitReminderChannel";
//        String description =" Channel for Lemubit Reminder";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel channel = new NotificationChannel("notifylemubit",name,importance);
//        channel.setDescription(description);
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//    }
//
//}
//
//
//


package Notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.instafoodies.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class reminder extends AppCompatActivity {

    // initialize variables
    private TextInputEditText titleEditText, messageEditText;
    private TextView repeatTextView;
    private boolean[] selectedRepeatOptions;
    private ArrayList<Integer> selectedRepeatList  = new ArrayList<>();
    private String[] repeatOptionsArray  = {"One_time","Day", "Week", "Month"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        createNotificationChannel();

        Button submitButton = findViewById(R.id.submitButton);
        titleEditText = findViewById(R.id.titleET);
        messageEditText = findViewById(R.id.massageET);
        repeatTextView = findViewById(R.id.Repit_it);

        selectedRepeatOptions  = new boolean[repeatOptionsArray.length];

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = findViewById(R.id.datepicer);
                TimePicker timePicker = findViewById(R.id.timepicer);

                String title = titleEditText.getText().toString();
                String message = messageEditText.getText().toString();

                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month,  day, hour, minute);
                long selectedTimeInMillis = calendar.getTimeInMillis();

                Intent intent = new Intent(reminder.this, ReminderBroadcast.class);
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(reminder.this, 0, intent, PendingIntent.FLAG_MUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                long timeAtButtonClick = System.currentTimeMillis();
                long delayInMillis = selectedTimeInMillis - System.currentTimeMillis();
                Log.d("AlarmDebug", "Delay in milliseconds: " + delayInMillis);

                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pendingIntent);
                Toast.makeText(Notifications.reminder.this,
                        timeAtButtonClick + ": time timeAtButtonClick" + selectedTimeInMillis + " time selectedTimeInMillis", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        repeatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(reminder.this, R.style.AlertDialogTheme);
                builder.setTitle("Repeat options");
                builder.setMultiChoiceItems(repeatOptionsArray, selectedRepeatOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        if (isChecked) {
                            selectedRepeatList.add(i);

                        } else {
                            selectedRepeatList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < selectedRepeatList.size(); j++) {
                            stringBuilder.append(repeatOptionsArray[selectedRepeatList.get(j)]);
                            if (j < selectedRepeatList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        repeatTextView.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < selectedRepeatOptions.length; j++) {
                            selectedRepeatOptions[j] = false;
                        }
                        selectedRepeatList.clear();
                        repeatTextView.setText(""); // Clear the displayed text
                        // Do NOT dismiss the dialog, let it stay open
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("reminder_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

package Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create and send the notification using FCM
        NotificationUtils.displayNotification(context, "Scheduled Notification", "This is a scheduled notification.");
    }
}

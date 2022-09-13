package com.example.my_wallet_app.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.my_wallet_app.R;

public class AlarmReceiver3Day extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setSmallIcon(R.mipmap.wallet_icon_foreground)
                .setContentTitle("My Wallet")
                .setContentText("Pagamento programmato tra 3 giorni!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Pagamento programmato tra 3 giorni!"))
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}

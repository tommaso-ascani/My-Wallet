package com.example.my_wallet_app.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.fragment_transaction.Transaction;

import java.time.LocalDate;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    com.example.my_wallet_app.Database.dbDAO dbDAO;
    MainDatabase db;
    Event event;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setSmallIcon(R.mipmap.wallet_icon_foreground)
                .setContentTitle("My Wallet")
                .setContentText("Pagamento programmato registrato!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Pagamento programmato registrato!"))
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2, builder.build());

        db = MainDatabase.getDb(context);
        dbDAO = db.dbDAO();

        Calendar calendar = Calendar.getInstance();
        LocalDate currentdate = LocalDate.now();

        calendar.set(Calendar.DAY_OF_MONTH, currentdate.getDayOfMonth());
        calendar.set(Calendar.MONTH, currentdate.getMonthValue());
        calendar.set(Calendar.YEAR, currentdate.getYear());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                event = dbDAO.getEventID(calendar.getTimeInMillis());
                dbDAO.registerTransaction(new Transaction(event.getType(), event.getAccount(), null, event.getAmount(), currentdate.getDayOfMonth() + "/" + currentdate.getMonthValue() + "/" + currentdate.getYear(), null, null));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

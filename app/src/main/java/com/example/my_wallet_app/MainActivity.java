package com.example.my_wallet_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.Notifications.Notification;
import com.example.my_wallet_app.Notifications.fragment_notifications;
import com.example.my_wallet_app.fragment_accounts.fragment_accounts;
import com.example.my_wallet_app.fragment_transaction.fragment_add_transaction;
import com.example.my_wallet_app.fragment_transaction.fragment_transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainDatabase db = MainDatabase.getDb(getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        Utilities.insertFragment(this, new fragment_accounts(this), fragment_accounts.class.getSimpleName());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSettings(preferences);

        ImageButton notificationButton = findViewById(R.id.notificationButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        FloatingActionButton add_transaction_button = findViewById(R.id.add_transaction_button);

        TextView notificationBadge = findViewById(R.id.notificationBadge);
        NavigationBarView bottomNavigation = findViewById(R.id.bottom_navigation);

        int accounts_page_id = findViewById(R.id.accounts_page).getId();
        int transaction_page_id = findViewById(R.id.transaction_page).getId();
        int planning_page_id = findViewById(R.id.planning_page).getId();
        int stats_page_id = findViewById(R.id.stats_page).getId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notification", "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        dbDAO.getLiveNotifications().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> test) {
                if(test.size()>0){
                    notificationBadge.setVisibility(View.VISIBLE);
                }else{
                    notificationBadge.setVisibility(View.GONE);
                }
            }
        });

        dbDAO.getBudgetsLive().observe(this, new Observer<List<Budget>>() {
            @Override
            public void onChanged(List<Budget> test) {
                int x;
                for(x=0; x < test.size(); x++){
                    if(Double.parseDouble(test.get(x).getUsedPercentage())>=75 && Double.parseDouble(test.get(x).getUsedPercentage())<100){
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar calendar = Calendar.getInstance();
                                formatter.format(calendar.getTime());
                                dbDAO.registerNotification(new Notification("Budget", "Budget sopra il 75%!", formatter.format(calendar.getTime())));
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Notification")
                                .setSmallIcon(R.mipmap.wallet_icon_foreground)
                                .setContentTitle("My Wallet")
                                .setContentText("Budget sopra il 75%!")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Budget sopra il 75%!"))
                                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(3, builder.build());
                    }else if(Double.parseDouble(test.get(x).getUsedPercentage())>=100){
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar calendar = Calendar.getInstance();
                                formatter.format(calendar.getTime());
                                dbDAO.registerNotification(new Notification("Budget", "Budget sopra il 100%!", formatter.format(calendar.getTime())));
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Notification")
                                .setSmallIcon(R.mipmap.wallet_icon_foreground)
                                .setContentTitle("My Wallet")
                                .setContentText("Budget sopra il 100%!")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Budget sopra il 100%!"))
                                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(4, builder.build());
                    }
                }
            }
        });

        add_transaction_button.setOnClickListener(v -> {
            fragment_add_transaction newFragment = new fragment_add_transaction(this);
            newFragment.show(getSupportFragmentManager(), "dialog");
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            fragment_notifications newFragment = new fragment_notifications(this);
            newFragment.show(getSupportFragmentManager(), "dialog");
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == accounts_page_id) {
                Utilities.insertFragment(this, new fragment_accounts(this), fragment_accounts.class.getSimpleName());
                findViewById(R.id.add_transaction_button).setVisibility(View.VISIBLE);
            }else if(item.getItemId() == transaction_page_id){
                Utilities.insertFragment(this, new fragment_transaction(this), fragment_transaction.class.getSimpleName());
                findViewById(R.id.add_transaction_button).setVisibility(View.VISIBLE);
            }else if(item.getItemId() == planning_page_id){
                Utilities.insertFragment(this, new fragment_planning(this), fragment_planning.class.getSimpleName());
                findViewById(R.id.add_transaction_button).setVisibility(View.GONE);
            }else if(item.getItemId() == stats_page_id){
                findViewById(R.id.add_transaction_button).setVisibility(View.GONE);
                Utilities.insertFragment(this, new fragment_statistics(), fragment_statistics.class.getSimpleName());
            }
            return true;
        });
    }

    public void loadSettings(SharedPreferences preferences) {
        if(preferences.getBoolean("NightMode", true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public AppCompatActivity getActivity() {
        MainActivity mainActivity = this;
        return mainActivity;
    }
}


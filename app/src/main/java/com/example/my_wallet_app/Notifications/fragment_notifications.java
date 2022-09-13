package com.example.my_wallet_app.Notifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.Notifications.Adapter;
import com.example.my_wallet_app.Notifications.Notification;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.fragment_accounts.Account;

import java.util.List;
import java.util.Objects;

public class fragment_notifications extends DialogFragment {

    private RecyclerView recyclerView;
    private List<Notification> notificationsList;
    private AppCompatActivity activity;
    private Adapter adapter;

    public fragment_notifications(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_notifications() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, null);
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        View mView = inflater.inflate(R.layout.fragment_notifications, null);

        loadList();

        if(notificationsList.size()>0){
            mView.findViewById(R.id.emptyNotification).setVisibility(View.GONE);
            mView.findViewById(R.id.notificationsRecyclerView).setVisibility(View.VISIBLE);
        }

        adapter = new Adapter(notificationsList, activity, mView);

        recyclerView = mView.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        recyclerView.setAdapter(adapter);

        dbDAO.getLiveNotifications().observe(activity, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> test) {
                loadList();
                adapter = new Adapter(notificationsList, activity, mView);
                recyclerView.setAdapter(adapter);
            }
        });

        builder.setView(mView);
        return builder.create();
    }

    public void loadList(){
        MainDatabase db = MainDatabase.getDb(activity.getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                notificationsList = dbDAO.getNotifications();
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
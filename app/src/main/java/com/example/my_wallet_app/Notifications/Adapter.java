package com.example.my_wallet_app.Notifications;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_transaction.Transaction;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    private List<Notification> notificationList;
    private AppCompatActivity activity;
    private View fragment_notifications;
    private boolean change = false;

    public Adapter(List<Notification> notificationList, AppCompatActivity activity, View fragment_notifications) {
        this.notificationList = notificationList;
        this.activity = activity;
        this.fragment_notifications = fragment_notifications;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView text;
        private TextView date;
        private ImageButton imageButton;
        private ConstraintLayout notificationCard;
        private TextView readBadge;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.notificationTitle);
            this.text = itemView.findViewById(R.id.notificationText);
            this.date = itemView.findViewById(R.id.notificationDate);
            this.imageButton = itemView.findViewById(R.id.imageButton);
            this.notificationCard = itemView.findViewById(R.id.notificationCard);
            this.readBadge = itemView.findViewById(R.id.readBadge);
        }

        public TextView getName() {
            return name;
        }

        public TextView getText() {
            return text;
        }

        public TextView getDate() {
            return date;
        }

        public ImageButton getImageButton() {
            return imageButton;
        }

        public ConstraintLayout getNotificationCard() {
            return notificationCard;
        }

        public TextView getReadBadge() {
            return readBadge;
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Notification currentNotification = notificationList.get(position);

        MainDatabase db = MainDatabase.getDb(activity.getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        holder.getName().setText(currentNotification.getName());
        holder.getText().setText(currentNotification.getText());
        holder.getDate().setText(currentNotification.getDate());

        if(!currentNotification.isRead()){
            holder.getName().setTypeface(null, Typeface.BOLD);
            holder.getText().setTypeface(null, Typeface.BOLD);
            holder.getReadBadge().setVisibility(View.VISIBLE);
        }

        holder.getNotificationCard().setOnClickListener(l -> {
            holder.getName().setTypeface(null, Typeface.NORMAL);
            holder.getText().setTypeface(null, Typeface.NORMAL);
            holder.getReadBadge().setVisibility(View.GONE);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDAO.readNotification(String.valueOf(currentNotification.getId()));
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        holder.getImageButton().setOnClickListener(l -> {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDAO.removeNotification(String.valueOf(currentNotification.getId()));
                    if(dbDAO.getNotifications().size()<1){
                        change = true;
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(change){
                fragment_notifications.findViewById(R.id.notificationsRecyclerView).setVisibility(View.GONE);
                fragment_notifications.findViewById(R.id.emptyNotification).setVisibility(View.VISIBLE);
                change = false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}

package com.example.my_wallet_app.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.my_wallet_app.Calendar.helper.TimeUtil;
import com.example.my_wallet_app.fragment_planning;
import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class fragment_edit_event extends DialogFragment {
    AppCompatActivity activity;
    Event event;
    com.example.my_wallet_app.Database.dbDAO dbDAO;
    MainDatabase db;
    CalenderEvent calenderEvent;

    public fragment_edit_event(AppCompatActivity activity, Event event) {
        this.activity = activity;
        this.event = event;
    }

    public fragment_edit_event() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_edit_event, null);

        db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO = db.dbDAO();

        TextView eventText = mView.findViewById(R.id.eventTitle);
        TextView eventType = mView.findViewById(R.id.typeValue);
        TextView date = mView.findViewById(R.id.dateValue);
        TextView eventAmount = mView.findViewById(R.id.amountValue);
        TextView eventAccount = mView.findViewById(R.id.accountValue);

        double temp = Double.parseDouble(event.getAmount());
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.ITALIAN);
        formatter.format("%(,.2f", temp);

        eventText.setText(event.getEventText());
        eventType.setText(event.getType());

        Date dateFormatatted = new Date(event.getTime());
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        String d = f.format(dateFormatatted);
        date.setText(d);

        eventAmount.setText(sb + " €");
        eventAccount.setText(event.getAccount());

        calenderEvent = activity.findViewById(R.id.calender_event);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                calenderEvent.removeEvent(event);
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Utilities.insertFragment(activity, new fragment_planning(activity), fragment_planning.class.getSimpleName());
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if(b != null){
            b.setTextColor(Color.RED);
        }

        return dialog;
    }
}
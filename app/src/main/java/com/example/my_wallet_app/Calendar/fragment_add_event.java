package com.example.my_wallet_app.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.my_wallet_app.Calendar.model.DayContainerModel;
import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Notifications.NotificationService;
import com.example.my_wallet_app.Notifications.NotificationService3Day;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.example.my_wallet_app.fragment_planning;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class fragment_add_event extends DialogFragment{
    AppCompatActivity activity;
    com.example.my_wallet_app.Database.dbDAO dbDAO;
    MainDatabase db;
    String transactionType;
    String account_0;
    Integer ciao;

    EditText title;
    EditText amount;
    AutoCompleteTextView accountChoose;
    Button expenseButton;
    Button incomeButton;
    MaterialButtonToggleGroup toggleGroup;
    CalenderEvent calenderEvent;
    DayContainerModel dayContainerModel;

    public fragment_add_event(AppCompatActivity activity, DayContainerModel dayContainerModel) {
        this.activity = activity;
        this.dayContainerModel = dayContainerModel;
    }

    public fragment_add_event() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_add_event, null);

        db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO = db.dbDAO();

        expenseButton = mView.findViewById(R.id.expense_button);
        incomeButton = mView.findViewById(R.id.income_button);
        title = mView.findViewById(R.id.eventTextValue);
        amount = mView.findViewById(R.id.amountValue2);
        accountChoose = mView.findViewById(R.id.autoCompleteTextView);
        toggleGroup = mView.findViewById(R.id.toggleButton);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(dbDAO.getAccounts().size() > 0){
                    account_0 = dbDAO.getAccounts().get(0).Account_Name;
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if(account_0 != null){
            accountChoose.setText(account_0);
        }

        ArrayList<String> inputs = getAccounts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mView.getContext(), R.layout.account_menu_item, inputs);
        accountChoose.setAdapter(adapter);

        calenderEvent = activity.findViewById(R.id.calender_event);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!amount.getText().toString().equals("")) {
                            if(Double.parseDouble(amount.getText().toString()) != 0) {
                                if(!title.getText().toString().equals("")){
                                    if (expenseButton.getId() == toggleGroup.getCheckedButtonId()) {
                                        transactionType = "Expense";
                                    }
                                    if (incomeButton.getId() == toggleGroup.getCheckedButtonId()) {
                                        transactionType = "Income";
                                    }
                                    Event temp = new Event(dayContainerModel.getTimeInMillisecond(), title.getText().toString(), transactionType, amount.getText().toString(), accountChoose.getText().toString());

                                    calenderEvent.addEvent(temp);
                                    Utilities.insertFragment(activity, new fragment_planning(activity), fragment_planning.class.getSimpleName());
                                } else {
                                    Toast toast = Toast.makeText(getContext(), "Titolo non inserito, inserirne uno!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Importo uguale a zero o non inserito!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(getContext(), "Importo uguale a zero o non inserito!", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("tempEventTime", dayContainerModel.getTimeInMillisecond());
                        editor.apply();

                        if((dayContainerModel.getTimeInMillisecond() - 259200000) > Calendar.getInstance().getTimeInMillis()){
                            activity.startService(new Intent(activity, NotificationService3Day.class));
                        }
                        activity.startService(new Intent(activity, NotificationService.class));
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public ArrayList<String> getAccounts() {
        ArrayList<String> temp = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for(i=0; i < dbDAO.getAccounts().size(); i++) {
                    temp.add(dbDAO.getAccounts().get(i).Account_Name);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return temp;
    }
}
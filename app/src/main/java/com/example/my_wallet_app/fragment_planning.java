package com.example.my_wallet_app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_wallet_app.Budget.Adapter;
import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Budget.fragment_edit_budget;
import com.example.my_wallet_app.Calendar.*;
import com.example.my_wallet_app.Calendar.listener.CalenderDayClickListener;
import com.example.my_wallet_app.Calendar.model.DayContainerModel;
import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class fragment_planning extends Fragment {

    private RecyclerView recyclerView;
    private AppCompatActivity activity;
    private List<Budget> budgetList;

    public fragment_planning(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_planning() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planning, container, false);

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                budgetList = dbDAO.getBudgets();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CalenderEvent calenderEvent = view.findViewById(R.id.calender_event);

        calenderEvent.initCalderItemClickCallback(new CalenderDayClickListener() {
            @Override
            public void onGetDay(DayContainerModel dayContainerModel) {
                if(dayContainerModel.isHaveEvent()) {
                    fragment_edit_event newFragment = new fragment_edit_event(activity, dayContainerModel.getEvent());
                    newFragment.show(activity.getSupportFragmentManager(), "dialog");
                }else{
                    fragment_add_event newFragment = new fragment_add_event(activity, dayContainerModel);
                    newFragment.show(activity.getSupportFragmentManager(), "dialog");
                }
            }
        });

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.budgetRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new Adapter(budgetList, activity));

        return view;
    }
}
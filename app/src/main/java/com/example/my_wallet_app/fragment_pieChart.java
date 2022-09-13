package com.example.my_wallet_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.fragment_accounts.Account;

import java.util.ArrayList;
import java.util.List;

public class fragment_pieChart extends Fragment {
    List<Account> accountList;

    public fragment_pieChart() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        accountList = new ArrayList<>();

        // First Chart -----------------------------------------------------------------------------
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for(i=0; i < dbDAO.getAccounts().size(); i++){
                    data.add(new ValueDataEntry(dbDAO.getAccounts().get(i).Account_Name, Double.parseDouble(dbDAO.getAccounts().get(i).Account_Balance)));
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pie.data(data);
        pie.title("Balance");

        AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.totalAmountChart);
        anyChartView.setChart(pie);

        return view;
    }
}
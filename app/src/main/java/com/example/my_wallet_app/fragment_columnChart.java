package com.example.my_wallet_app;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class fragment_columnChart extends Fragment {

    double Expense;
    double Income;

    public fragment_columnChart() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_column_chart, container, false);

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        AnyChartView anyChartView2 = view.findViewById(R.id.expenseIncomeChart);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        Expense = 0;
        Income = 0;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for(i=0; i < dbDAO.getTransactionsType("Expense").size(); i++){
                    Expense = Expense + Double.parseDouble(dbDAO.getTransactionsType("Expense").get(i).Amount);
                }

                int y;
                for(y=0; y < dbDAO.getTransactionsType("Income").size(); y++){
                    Income = Income + Double.parseDouble(dbDAO.getTransactionsType("Income").get(y).Amount);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        data.add(new CustomDataEntry("Expense", Expense, "red 0.8"));
        data.add(new CustomDataEntry("Income", Income, "green 0.8"));

        Column column = cartesian.column(data);

        cartesian.title("Cash Flow");

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Cash Flow");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        anyChartView2.setChart(cartesian);

        return view;
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, String fill) {
            super(x, value);
            setValue("fill", fill);
        }
    }
}
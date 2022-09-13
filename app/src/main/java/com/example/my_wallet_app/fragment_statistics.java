package com.example.my_wallet_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationBarView;

public class fragment_statistics extends Fragment {

    public fragment_statistics() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView2, new fragment_pieChart(), fragment_pieChart.class.getSimpleName());
        transaction.replace(R.id.fragmentContainerView3, new fragment_columnChart(), fragment_columnChart.class.getSimpleName());
        transaction.commit();

        return view;
    }
}
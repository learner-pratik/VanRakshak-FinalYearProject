package com.example.forestofficerapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SOSAlertFragment extends Fragment {

    public static final String TITLE = "SOS Alert";
    public static List<Alert> sosAlertList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    public static SOSAlertFragment newInstance() {
        return new SOSAlertFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sos_alert, container, false);
        recyclerView = view.findViewById(R.id.sosAlertRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        addAlerts();
        adapter = new AlertCardRecyclerViewAdapter(sosAlertList, position -> {
            Intent mapActivityIntent = new Intent(view.getContext(), MapActivity.class);
            startActivity(mapActivityIntent);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addAlerts() {
        Alert alert = new Alert(
                "Animal intrusion",
                "SOS Alert",
                new Date(),
                LocalTime.now(),
                19.75432,
                79.12232
        );
        sosAlertList.add(alert);
    }
}

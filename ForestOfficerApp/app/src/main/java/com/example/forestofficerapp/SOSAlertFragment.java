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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sos_alert, container, false);
        recyclerView = view.findViewById(R.id.sosAlertRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SosCardRecyclerViewAdapter(sosAlertList, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                Intent mapActivityIntent = new Intent(view.getContext(), AlertMapActivity.class);
                mapActivityIntent.putExtra("type", "sos");
                mapActivityIntent.putExtra("alertIndex", position);
                startActivity(mapActivityIntent);
            }

            @Override
            public void onLongClicked(int position) {
                Intent reportIntent = new Intent(view.getContext(), ReportActivity.class);
                reportIntent.putExtra("type", "Alert Report");
                startActivity(reportIntent);
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }
}

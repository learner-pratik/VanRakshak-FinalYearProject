package com.example.forestofficerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class AlertCardRecyclerViewAdapter extends RecyclerView.Adapter<AlertCardViewHolder> {

    private final List<Alert> alertList;
    private final ClickListener listener;

    public AlertCardRecyclerViewAdapter(List<Alert> alertList, ClickListener listener) {
        this.alertList = alertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlertCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_alert_card, parent, false);
        return new AlertCardViewHolder(layoutView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertCardViewHolder holder, int position) {
        if (alertList != null && position<alertList.size()) {
            Alert alert = alertList.get(position);
            holder.alertName.setText(alert.getAlertName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
            String alertDate = dateFormat.format(alert.getAlertDate());
            String alertTime = alert.getAlertTime().toString();
            holder.alertDate.setText(alertDate);
            holder.alertTime.setText(alertTime);
        }
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }
}

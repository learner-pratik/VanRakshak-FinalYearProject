package com.example.forestofficerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

public class SosCardRecyclerViewAdapter extends RecyclerView.Adapter<SosCardViewHolder> {

    private final List<Alert> sosAlertList;
    private final ClickListener listener;

    public SosCardRecyclerViewAdapter(List<Alert> sosAlertList, ClickListener listener) {
        this.sosAlertList = sosAlertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SosCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sos_alert_card, parent, false);
        return new SosCardViewHolder(layoutView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SosCardViewHolder holder, int position) {
        if (sosAlertList != null && position<sosAlertList.size()) {
            Alert alert = sosAlertList.get(position);
            holder.alertName.setText(alert.getAlertName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
            String alertDate = dateFormat.format(alert.getAlertDate());
            String alertTime = alert.getAlertTime().toString();
            holder.alertDate.setText(alertDate);
            holder.alertTime.setText(alertTime);
            JSONObject userDetails = alert.getUserObject();
            try {
                holder.userName.setText(userDetails.getString("name"));
                holder.userPhone.setText(userDetails.getString("phone"));
                holder.userAddress.setText(userDetails.getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return sosAlertList.size();
    }
}

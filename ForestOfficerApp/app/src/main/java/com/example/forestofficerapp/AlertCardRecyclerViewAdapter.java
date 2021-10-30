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
            holder.alertName.setText(makeCapital(alert.getAlertName()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
            String alertDate = dateFormat.format(alert.getAlertDate());
            String alertTime = alert.getAlertTime().toString();
            holder.alertDate.setText(alertDate);
            holder.alertTime.setText(alertTime);
        }
    }

    private String makeCapital(String text) {
        // stores each characters to a char array
        char[] charArray = text.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {

            // if the array element is a letter
            if(Character.isLetter(charArray[i])) {

                // check space is present before the letter
                if(foundSpace) {

                    // change the letter into uppercase
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            }

            else {
                // if the new character is not character
                foundSpace = true;
            }
        }

        // convert the char array to the string
        String message = String.valueOf(charArray);
        return message;
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }
}

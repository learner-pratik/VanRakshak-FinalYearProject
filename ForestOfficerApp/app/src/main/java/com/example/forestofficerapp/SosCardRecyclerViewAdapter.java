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
            holder.alertName.setText(makeCapital(alert.getAlertName()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
            String alertDate = dateFormat.format(alert.getAlertDate());
            String alertTime = alert.getAlertTime().toString();
            holder.alertDate.setText(alertDate);
            holder.alertTime.setText(alertTime);
            JSONObject userDetails = alert.getUserObject();
            try {
                holder.userName.setText(makeCapital(userDetails.getString("name")));
                holder.userPhone.setText(userDetails.getString("phone"));
                holder.userAddress.setText(userDetails.getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        return sosAlertList.size();
    }
}

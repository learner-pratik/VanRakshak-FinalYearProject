package com.example.forestofficerapp;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class AlertCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView alertName, alertDate, alertTime;
    private WeakReference<ClickListener> listenerWeakReference;

    public AlertCardViewHolder (@NonNull View itemView, ClickListener listener) {
        super(itemView);

        listenerWeakReference = new WeakReference<>(listener);

        alertName = itemView.findViewById(R.id.alertCardAlertName);
        alertDate = itemView.findViewById(R.id.alertCardDateName);
        alertTime = itemView.findViewById(R.id.alertCardTimeName);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "ROW PRESSED = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        listenerWeakReference.get().onPositionClicked(getAdapterPosition());
    }

}

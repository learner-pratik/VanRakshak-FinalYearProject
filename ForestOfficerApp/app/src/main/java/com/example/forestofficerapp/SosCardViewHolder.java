package com.example.forestofficerapp;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class SosCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView alertName, alertDate, alertTime, userName, userPhone, userAddress;
    private WeakReference<ClickListener> listenerWeakReference;

    public SosCardViewHolder(@NonNull View itemView, ClickListener listener) {
        super(itemView);

        listenerWeakReference = new WeakReference<>(listener);

        alertName = itemView.findViewById(R.id.sosAlertCardAlertName);
        alertDate = itemView.findViewById(R.id.sosAlertCardDateName);
        alertTime = itemView.findViewById(R.id.sosAlertCardTimeName);
        userName = itemView.findViewById(R.id.sosUserName);
        userPhone = itemView.findViewById(R.id.sosPhoneNumber);
        userAddress = itemView.findViewById(R.id.sosUserAddressName);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "ROW PRESSED = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        listenerWeakReference.get().onPositionClicked(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(v.getContext(), "ROW PRESSED = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        listenerWeakReference.get().onLongClicked(getAdapterPosition());
        return true;
    }
}

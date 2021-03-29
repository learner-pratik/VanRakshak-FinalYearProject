package com.example.forestofficerapp;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;

public class TaskCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView taskName, typeName, deadline;
    public FloatingActionButton actionButton;
    private WeakReference<ClickListener> listenerRef;

    public TaskCardViewHolder(@NonNull View itemView, ClickListener listener) {

        super(itemView);
        listenerRef = new WeakReference<>(listener);

        taskName = itemView.findViewById(R.id.taskCardTaskName);
        typeName = itemView.findViewById(R.id.taskCardTypeName);
        deadline = itemView.findViewById(R.id.taskCardDeadlineDate);
        actionButton = itemView.findViewById(R.id.openTaskButton);

        actionButton.setOnClickListener(this);
        actionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == actionButton.getId()) {
            Toast.makeText(v.getContext(), "BUTTON PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
        }

        listenerRef.get().onPositionClicked(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == actionButton.getId()) {
            Toast.makeText(v.getContext(), "BUTTON PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
        }

        listenerRef.get().onLongClicked(getAdapterPosition());
        return true;
    }
}

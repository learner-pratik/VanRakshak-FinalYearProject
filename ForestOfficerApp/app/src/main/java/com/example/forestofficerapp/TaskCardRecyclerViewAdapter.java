package com.example.forestofficerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TaskCardRecyclerViewAdapter extends RecyclerView.Adapter<TaskCardViewHolder> {

    private final List<Task> taskList;
    private final ClickListener listener;
    public TaskCardRecyclerViewAdapter(List<Task> taskList, ClickListener listener) {
        this.listener = listener;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_task_card, parent, false);
        return new TaskCardViewHolder(layoutView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskCardViewHolder holder, int position) {
        if (taskList != null && position<taskList.size()) {
            Task task = taskList.get(position);
            holder.taskName.setText(task.getTaskName());
            holder.typeName.setText(task.getTaskType());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
            String deadlineDate = dateFormat.format(task.getTaskDeadline());
            holder.deadline.setText(deadlineDate);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}

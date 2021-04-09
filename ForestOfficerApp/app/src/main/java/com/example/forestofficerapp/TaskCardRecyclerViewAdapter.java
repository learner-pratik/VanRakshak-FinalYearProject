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
            holder.taskName.setText(makeCapital(task.getTaskDescription()));
            holder.typeName.setText(makeCapital(task.getTaskType()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
            String deadlineDate = dateFormat.format(task.getTaskDeadline());
            holder.deadline.setText(deadlineDate);
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
        return taskList.size();
    }
}

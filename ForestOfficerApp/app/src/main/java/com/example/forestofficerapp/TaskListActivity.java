package com.example.forestofficerapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskListActivity extends Activity {

    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getTasks();
    }

    private void getTasks() {
        Task task1 = new Task("Animal", "Elephant footprint", "Get footprints", "Ravi", new Date());
        taskList.add(task1);

        Task task2 = new Task("Tree", "Count pine tree", "All types", "Ravi", new Date());
        taskList.add(task2);

        Task task3 = new Task("Animal", "deer count", "All kinds", "Ravi", new Date());
        taskList.add(task3);
    }
}

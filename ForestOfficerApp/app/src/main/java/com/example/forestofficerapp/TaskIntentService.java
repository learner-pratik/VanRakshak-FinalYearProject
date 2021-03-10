package com.example.forestofficerapp;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TaskIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        JSONObject jsonObject = new JSONObject();
        JSONObject taskObject = new JSONObject();
        int id = 0;
        String type="", name="", description="", assignedBy="", deadline="";
        Date date = new Date();

        try {
            jsonObject.put("email", SaveSharedPreference.getEmail(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendData sendData = new SendData();
        JSONArray receivedData = (JSONArray) sendData.sendJsonData(this, jsonObject, "Task");

        for (int i=0; i<receivedData.length(); i++) {

            try {
                taskObject = receivedData.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                id = taskObject.getInt("id");
                type = taskObject.getString("type");
                name = taskObject.getString("name");
                description = taskObject.getString("description");
                assignedBy = taskObject.getString("assigned");
                deadline = taskObject.getString("deadline");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                date=new SimpleDateFormat("dd/MM/yyyy").parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Task newTask = new Task(id, type, name, description, assignedBy, date);
            TaskListActivity.taskList.add(newTask);
        }
    }
}

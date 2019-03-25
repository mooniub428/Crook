package com.example.stathis.crookstore.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.stathis.crookstore.Connection.Connect;
import com.example.stathis.crookstore.Connection.Get_Request_Handler;
import com.example.stathis.crookstore.MainActivity;
import com.example.stathis.crookstore.Model.Arrival;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArrivalsJobService extends JobService {

    private static final String TAG = "ArrivalsJobService";
    private static final String url_get_sorted_arrivals = "http://192.168.1.65/crook/api/location/read_sorted.php";
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started ");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {

                Get_Request_Handler get = new Get_Request_Handler();
                String result = get.performGetCall(url_get_sorted_arrivals);

                Log.d(TAG, "result : " + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    JSONArray jsonArray = jsonObject.getJSONArray("arrivals");

                    MainActivity.arrivals.clear();

                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject a = jsonArray.getJSONObject(i);

                        int user_id = a.getInt("user_id");
                        String email = a.getString("email");
                        String firstname = a.getString("firstname");
                        String lastname = a.getString("lastname");
                        String time_remain = a.getString("time_remain");

                        Log.d(TAG, "user_id=" + user_id);
                        Log.d(TAG, "email=" + email);
                        Log.d(TAG, "firstname=" + firstname);
                        Log.d(TAG, "lastname=" + lastname);
                        Log.d(TAG, "time_remain=" + time_remain);

                        Arrival arrival = new Arrival(user_id, email, firstname, lastname, time_remain);
                        MainActivity.arrivals.add(arrival);

                    }

                    Intent intent = new Intent("arrivals_update");
                    sendBroadcast(intent);

                    Log.d(TAG, "Arrivals were updated");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Job Finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled before completion");
        jobCancelled = true;
        return true;
    }
}

package com.example.stathis.crookstore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.example.stathis.crookstore.Adapter.ArrivalsRecyclerViewAdapter;
import com.example.stathis.crookstore.Adapter.CategoriesRecyclerViewAdapter;
import com.example.stathis.crookstore.Model.Arrival;
import com.example.stathis.crookstore.Service.ArrivalsJobService;
import com.example.stathis.crookstore.Service.Constants;
import com.example.stathis.crookstore.Service.MyNotificationManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button scheduleJob;
    private Button cancelJob;
    private RecyclerView myrv;
    private ProgressDialog pDialog;
    private BroadcastReceiver broadcastReceiver;

    private ArrivalsRecyclerViewAdapter myAdapter;

    public static List<Arrival> arrivals;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleJob = findViewById(R.id.scheduleJob);
        cancelJob = findViewById(R.id.cancelJob);
        myrv = findViewById(R.id.arrivalsRecyclerView);

        arrivals = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Crook Store");
        }
        toolbar.inflateMenu(R.menu.main_menu);



        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.add_product)
                {
                    Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId()== R.id.display_products)
                {
                    Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });


        /*
         * If the device is having android oreo we will create a notification channel
         * */
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }*/

        myAdapter = new ArrivalsRecyclerViewAdapter(MainActivity.this, arrivals);
        myrv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        myrv.setAdapter(myAdapter);

        /*
         * Displaying a notification locally
         */
        //MyNotificationManager.getInstance(this).displayNotification("Greetings", "Hello how are you?");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    myAdapter.notifyDataSetChanged();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("arrivals_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void scheduleJob(View view){
        ComponentName componentName = new ComponentName(getApplicationContext(), ArrivalsJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job Scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob(View view){
        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }

    public class LoadCartOfUser extends AsyncTask<Integer, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading product categories. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            return null;
        }
    }
}

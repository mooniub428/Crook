package com.example.stath.crookclient.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.stath.crookclient.Adapter.CategoriesRecyclerViewAdapter;
import com.example.stath.crookclient.Connection.Connect;
import com.example.stath.crookclient.Connection.Get_Request_Handler;
import com.example.stath.crookclient.Connection.Post_Request_Handler;
import com.example.stath.crookclient.Model.Category;
import com.example.stath.crookclient.R;
import com.example.stath.crookclient.Service.LocationService;
import com.example.stath.crookclient.UserStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String url_send_location = "http://" + Connect.host + "/crook/api/location/publish.php";
    private static final String url_categories = "http://" + Connect.host + "/crook/api/category/read.php";
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private RecyclerView myrv;
    private ProgressDialog pDialog;
    private List<Category> categoriesList;

    private BroadcastReceiver broadcastReceiver;

    private double latitude, longitude;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_main);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Crook");

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_main);
        navigationView.setNavigationItemSelectedListener(this);

        /*toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Come On");*/



        /*Check if user is logged*/
        String userEmail = UserStatus.getUserEmail(MainActivity.this);
        String userFirstname = UserStatus.getUserFirstname(MainActivity.this);
        if(userEmail.isEmpty()){
            Intent i = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(MainActivity.this, "Welcome " + userFirstname, Toast.LENGTH_SHORT).show();
        }

        /*Correlate views*/
        myrv = findViewById(R.id.categories_recyclerview);

        /*Initialize data*/
        categoriesList = new ArrayList<>();

        /*Loading products in Background thread*/
        new MainActivity.LoadCategories().execute();

        if(!checkPermissions()){
            startLocationService();
        }

    }

    private boolean checkPermissions(){
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);

            return true;
        }
        return false;
    }

    private void startLocationService(){
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            } else {
                checkPermissions();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.cart:
                intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("fragment", 0);
                startActivity(intent);
                break;
            case R.id.orders:
                intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("fragment", 1);
                startActivity(intent);
                break;
            case R.id.profile:
                intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("fragment", 2);
                startActivity(intent);
                break;
            case R.id.logout:
                logout();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    latitude = intent.getExtras().getDouble("latitude");
                    longitude = intent.getExtras().getDouble("longitude");
                    speed = intent.getExtras().getFloat("speed");
                    Log.d(TAG, "onReceive: " + latitude + " " + longitude + " " + speed);
                    new SendLocalPosition().execute(String.valueOf(latitude),
                                                        String.valueOf(longitude),
                                                            String.valueOf(speed));
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void logout(){
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        UserStatus.clearData(MainActivity.this);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    class LoadCategories extends AsyncTask<Void, Void, String>{

        /**
         * Before starting background thread show Progress Dialog
         */
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
        protected String doInBackground(Void... voids) {

            String response = "";

            try{

                Get_Request_Handler get = new Get_Request_Handler();
                response = get.performGetCall(url_categories);

                Log.d(TAG, "doInBackground: " + response);

                //Convert result string to jsonObject
                JSONObject categoriesObj = new JSONObject(response);

                JSONArray categories = categoriesObj.getJSONArray("categories");

                for (int i = 0; i < categories.length(); i++) {

                    JSONObject c = categories.getJSONObject(i);

                    int id = c.getInt("id");
                    String name = c.getString("name");
                    String desc = c.getString("desc");
                    String thumbUrl = c.getString("thumb");

                    Log.d(TAG, "doInBackground: " + id + "\t" + name + "\t" + desc + "\t" + thumbUrl);

                    Bitmap bitmap = getBitmapFromURL(thumbUrl);
                    Category category = new Category(c.getInt("id"), c.getString("name"), c.getString("desc"), bitmap);
                    categoriesList.add(category);

                }


            }  catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        /**
         * After completing background task dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            //dismiss dialog after getting categories
            pDialog.dismiss();

            //update UI from background thread
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    CategoriesRecyclerViewAdapter myAdapter = new CategoriesRecyclerViewAdapter(MainActivity.this, categoriesList);
                    myrv.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    myrv.setAdapter(myAdapter);
                }
            });

        }

        public Bitmap getBitmapFromURL(String src){

            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                input.close();
                connection.disconnect();
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

    }

    class SendLocalPosition extends AsyncTask<String, Void, Integer>{


        @Override
        protected Integer doInBackground(String... strings) {
            String latitude = strings[0];
            String longitude = strings[1];
            String speed = strings[2];
            boolean success = false;

            HashMap<String, String> params = new HashMap<>();
            params.put("email", UserStatus.getUserEmail(getApplication()));
            params.put("latitude", latitude);
            params.put("longitude", longitude);
            params.put("speed", speed);

            Post_Request_Handler post = new Post_Request_Handler();
            String response = post.performPostCall(url_send_location, params, false, null);

            Log.d(TAG, "doInBackground: url_send_location=" +url_send_location);
            Log.d(TAG, "doInBackground: response=" + response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                success = jsonObject.getBoolean("success");
                if(!success)
                    Log.d(TAG, "doInBackground: success=" + success);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}

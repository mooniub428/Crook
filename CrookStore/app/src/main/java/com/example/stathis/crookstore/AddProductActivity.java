package com.example.stathis.crookstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.stathis.crookstore.Adapter.CategoriesRecyclerViewAdapter;
import com.example.stathis.crookstore.Connection.Connect;
import com.example.stathis.crookstore.Connection.Get_Request_Handler;
import com.example.stathis.crookstore.Connection.Post_Request_Handler;
import com.example.stathis.crookstore.Model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private static final String url_get_categories = "http://" + Connect.host + "/crook/api/category/read.php";
    private static final String url_create_product = "http://" + Connect.host + "/crook/api/product/create.php";
    private static final String TAG = "AddProductActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView productImage;

    private EditText inputProductName;
    private EditText inputProductDesc;
    private EditText inputProductPrice;

    private RecyclerView myrv;

    private Button btnAddProduct;
    private Button btnUpdateProduct;

    private ProgressDialog pDialog;

    CategoriesRecyclerViewAdapter myAdapter;
    private List<Category> categories;

    private Bitmap bitmap;

    private int success = 0;
    private String server_message = "Default";

    private RelativeLayout add_product_layout;

    private boolean update_mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);



        /*if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                update_mode = false;
            } else {
                product_id = extras.getInt("product_id");
                update_mode =  true;
            }
        } else {
            product_id= (Integer) savedInstanceState.getSerializable("product_id");
        }*/


        productImage = findViewById(R.id.productImage);
        inputProductName = findViewById(R.id.inputProductName);
        inputProductDesc = findViewById(R.id.inputProductDesc);
        inputProductPrice = findViewById(R.id.inputProductPrice);
        myrv = findViewById(R.id.categoriesRecyclerView);

        add_product_layout = findViewById(R.id.add_product_layout);
        add_product_layout.requestFocus();
        Toolbar toolbar = findViewById(R.id.add_product_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crook Store");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);
        btnAddProduct = findViewById(R.id.btnAddProduct);


        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                btnUpdateProduct.setVisibility(View.VISIBLE);
                btnAddProduct.setVisibility(View.GONE);

                initializeViews(extras);

            }
        }


        categories = new ArrayList<>();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               UploadProduct();
            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        new LoadCategories().execute();
    }

    private void initializeViews(Bundle extras){

        int id = extras.getInt("id");
        int categoryId = extras.getInt("productId");
        String name = extras.getString("name");
        String desc = extras.getString("desc");
        String price = extras.getString("price");
        //String filename = extras.getString("filename");
        /*Bitmap thumb = null;
        try{
            FileInputStream is = this.openFileInput(filename);
            thumb = BitmapFactory.decodeStream(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //Bitmap thumb = extras.getParcelable("thumb");

        inputProductName.setText(name);
        inputProductDesc.setText(desc);
        inputProductPrice.setText(price);
        //productImage.setImageBitmap(thumb);


    }

    private void highlightCategory(int category_id){

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            productImage.setImageBitmap(bitmap);
        }
    }

    private void UploadProduct(){
        if(validInputs()){
            new UploadProduct().execute();
        }
    }

    private boolean validInputs(){

        boolean valid = true;
        int selectedItem = -1;

        selectedItem = myAdapter.selectedItem;
        String name = inputProductName.getText().toString();
        String desc = inputProductDesc.getText().toString();
        String price = inputProductPrice.getText().toString();

        if(bitmap == null){
            Toast.makeText(getApplicationContext(), "An image is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(name.isEmpty() || name.length() < 4){
            inputProductName.setError("at least 4 characters");
            valid = false;
        } else {
            inputProductName.setError(null);
        }

        if(desc.isEmpty() || desc.length() < 10){
            inputProductDesc.setError("at least 10 characters");
            valid = false;
        } else {
            inputProductDesc.setError(null);
        }

        if(price.isEmpty()){
            inputProductPrice.setError("cannot be empty");
            valid = false;
        } else {
            inputProductPrice.setError(null);
        }

        if(selectedItem == -1){
            Toast.makeText(getApplicationContext(), "A category is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    class LoadCategories extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddProductActivity.this);
            pDialog.setMessage("Loading activity. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter = new CategoriesRecyclerViewAdapter(AddProductActivity.this, categories);
                    myrv.setLayoutManager(new LinearLayoutManager(AddProductActivity.this));
                    myrv.setAdapter(myAdapter);
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {

            Get_Request_Handler get = new Get_Request_Handler();
            String response = get.performGetCall(url_get_categories);

            Log.d(TAG, "doInBackground: response=" + response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray("categories");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    int id = Integer.parseInt(c.getString("id"));
                    String name = c.getString("name");
                    String desc = c.getString("desc");
                    String thumbUrl = c.getString("thumb");
                    Bitmap thumb = getBitmapFromURL(thumbUrl);
                    //Bitmap thumb = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    //        R.drawable.produce);
                    Log.d(TAG, "doInBackground: name=" + name);

                    Category category = new Category(id, name, desc, thumb);
                    categories.add(category);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        public Bitmap getBitmapFromURL(String src) {

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

    class UploadProduct extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddProductActivity.this);
            pDialog.setMessage("Uploading product...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_SHORT).show();
                    if (success == 1) {
                        pDialog.dismiss();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {

            int selectedItem = myAdapter.selectedItem;
            String name = inputProductName.getText().toString();
            String desc = inputProductDesc.getText().toString();
            String price = inputProductPrice.getText().toString();
            String category_id = String.valueOf(categories.get(selectedItem).getId());
            String encodedThumb = getStringImage(bitmap);

            HashMap<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("desc", desc);
            params.put("price", price);
            params.put("category_id", category_id);
            params.put("encodedThumb", encodedThumb);

            Post_Request_Handler post = new Post_Request_Handler();
            String response = post.performPostCall(url_create_product, params);

            Log.d(TAG, "doInBackground: response=" + response);

            if (response != null && !response.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    server_message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                success = 0;
                server_message = "No response from server";
            }

            return null;
        }

        private String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
    }

    class LoadProduct extends AsyncTask<Integer, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddProductActivity.this);
            pDialog.setMessage("Uploading ...");
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


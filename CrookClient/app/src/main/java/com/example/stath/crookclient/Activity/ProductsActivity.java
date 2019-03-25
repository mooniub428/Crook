package com.example.stath.crookclient.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stath.crookclient.Adapter.ProductsRecyclerViewAdapter;
import com.example.stath.crookclient.Connection.Connect;
import com.example.stath.crookclient.Connection.Get_Request_Handler;
import com.example.stath.crookclient.Helper.RecyclerItemTouchHelper;
import com.example.stath.crookclient.Helper.RecyclerItemTouchHelperListener;
import com.example.stath.crookclient.Model.Product;
import com.example.stath.crookclient.R;
import com.example.stath.crookclient.SQLiteDatabase.DatabaseHelper;

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

public class ProductsActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    private ProgressDialog pDialog;
    private RecyclerView products_rv;
    private int category_id;

    private static String url_get_products_of_category = "http://" + Connect.host + "/crook/api/product/read_using_category_id.php";
    private static final String TAG = "ProductsActivity";

    private List<Product> products;
    private ProductsRecyclerViewAdapter myAdapter;

    private FloatingActionButton fab_cart;

    private boolean productIsSelected;

    private DatabaseHelper crook_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        products_rv = findViewById(R.id.products_recyclerview);
        fab_cart = findViewById(R.id.fabAddToCart);

        //Initialize data
        products = new ArrayList<>();
        crook_db = new DatabaseHelper(this);

        //Receive data
        Intent intent = getIntent();
        category_id = intent.getExtras().getInt("category_id");
        Log.d(TAG, "Category id that was passed: " + category_id);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ProductsActivity.this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(products_rv);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            public static final float ALPHA_FULL = 1.0f;

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    Bitmap icon;

                    if (dX > 0) {

                        //color : left side (swiping towards right)
                        p.setARGB(255, 255, 0, 0);
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                        // icon : left side (swiping towards right)
                        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.crook_icon);
                        c.drawBitmap(icon,
                                (float) itemView.getLeft() + convertDpToPx(16),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                p);
                    } else {

                        //color : right side (swiping towards left)
                        p.setARGB(255, 0, 255, 0);

                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                        //icon : left side (swiping towards right)
                        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.crook_icon);
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                p);
                    }

                    // Fade out the view when it is swiped out of the parent
                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            private int convertDpToPx(int dp){
                return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //swiped position

                if (direction == ItemTouchHelper.LEFT) { //swipe left

                    products.remove(position);
                    myAdapter.notifyItemRemoved(position);

                    Toast.makeText(getApplicationContext(),"Swipped to left",Toast.LENGTH_SHORT).show();

                }else if(direction == ItemTouchHelper.RIGHT){//swipe right

                    products.remove(position);
                    myAdapter.notifyItemRemoved(position);

                    Toast.makeText(getApplicationContext(),"Swipped to right",Toast.LENGTH_SHORT).show();

                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(null);

        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsActivity.this, AccountActivity.class);
                intent.putExtra("fragment", 0);
                startActivity(intent);
            }
        });

        //Loading products in Background Thread
        new ProductsActivity.LoadProducts().execute();


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof ProductsRecyclerViewAdapter.MyViewHolder) {

            int id;
            int quantity;

            int product_index = viewHolder.getAdapterPosition();
            Product product = products.get(product_index);
            id = product.getId();

            //Get cart from SQLite database
            Cursor res = crook_db.getCartItemUsingProductId(id);

            /* show res */
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append("CARTITEM_ID: " + String.valueOf(res.getInt(0)) + "\n");
                buffer.append("PRODUCT_ID: " + String.valueOf(res.getInt(1)) + "\n");
                buffer.append("QUANTITY: " + String.valueOf(res.getInt(2)) + "\n");
            }

            Log.d(TAG, "onSwiped: buffer:" + buffer.toString());

            /* Add to cart */
            if (direction == ItemTouchHelper.RIGHT) {

                if (res.getCount() == 0) {
                    crook_db.insertData(id, 1);
                } else if (res.getCount() == 1) {

                    /* Increase quantity of retrieved product */
                    res.moveToFirst();
                    crook_db.incQuantityOfProduct(res.getInt(1));

                } else {
                    Toast.makeText(getApplicationContext(), "More than one of the same item were found.", Toast.LENGTH_SHORT).show();
                }


            } else if (direction == ItemTouchHelper.LEFT){  //Decrease

                if(res.getCount() == 0){
                    //Nothing
                } else if (res.getCount() == 1){

                    /* Decrease quantity of retrieved product */
                    res.moveToFirst();
                    crook_db.decQuantityOfProduct(res.getInt(1));

                } else {
                    Toast.makeText(this, "More or less of product " + res.getInt(1) +
                        "were retrieved", Toast.LENGTH_SHORT).show();
                }
            }
            myAdapter.notifyItemChanged(product_index);
        }

    }

    @Override
    public void onChildDraw(RecyclerView.ViewHolder viewHolder, float dX) {

        final int index = viewHolder.getAdapterPosition();

        TextView addtocart = ((ProductsRecyclerViewAdapter.MyViewHolder)viewHolder).addtocart;
        TextView removefromcart = ((ProductsRecyclerViewAdapter.MyViewHolder)viewHolder).removefromcart;

        if(dX > 0){
            addtocart.setVisibility(View.VISIBLE);
            removefromcart.setVisibility(View.INVISIBLE);
        } else {
            addtocart.setVisibility(View.INVISIBLE);
            removefromcart.setVisibility(View.VISIBLE);
        }
    }


    class LoadProducts extends AsyncTask<String, String, String>{

        /**
         * Before starting background thread show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductsActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * After completing background task dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    myAdapter = new ProductsRecyclerViewAdapter(ProductsActivity.this, products);
                    products_rv.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
                    products_rv.setAdapter(myAdapter);

                }
            });
        }

        /**
         * getting all products from url
         */
        protected String doInBackground(String... args){


            HashMap<String, String> params = new HashMap<>();
            params.put("category_id", String.valueOf(category_id));

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(Connect.host)
                    .appendPath("crook")
                    .appendPath("api")
                    .appendPath("product")
                    .appendPath("read_using_category_id.php")
                    .appendQueryParameter("category_id", String.valueOf(category_id));

            url_get_products_of_category = builder.build().toString();

            Get_Request_Handler get = new Get_Request_Handler();
            String response = get.performGetCall(url_get_products_of_category);

            Log.d(TAG, "doInBackground: here" + response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray productsArray = jsonObject.getJSONArray("products");

                for(int i=0; i<productsArray.length(); i++){

                    JSONObject p = productsArray.getJSONObject(i);
                    Log.d(TAG, "Product: " + p.getString("name"));

                    int id = p.getInt("id");
                    String name = p.getString("name");
                    String description = p.getString("description");
                    String price = p.getString("price");
                    Bitmap thumb = getBitmapFromURL(p.getString("thumb"));

                    Product product = new Product(id, name, description, price, thumb);
                    products.add(product);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "doInBackground: " + response);
            return null;
        }

        private Bitmap getBitmapFromURL(String src){

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
}

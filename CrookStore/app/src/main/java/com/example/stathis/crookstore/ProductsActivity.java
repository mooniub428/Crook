package com.example.stathis.crookstore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.stathis.crookstore.Adapter.ProductsRecyclerViewAdapter;
import com.example.stathis.crookstore.Connection.Connect;
import com.example.stathis.crookstore.Connection.Delete_Request_Handler;
import com.example.stathis.crookstore.Connection.Get_Request_Handler;
import com.example.stathis.crookstore.Helper.RecyclerItemTouchHelper;
import com.example.stathis.crookstore.Helper.RecyclerItemTouchHelperListener;
import com.example.stathis.crookstore.Model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ProductsActivity";
    private final String url_get_products = "http://" + Connect.host + "/crook/api/product/read.php";
    private final String url_delete_product = "http://" + Connect.host + "/crook/api/product/delete.php";

    private FloatingActionButton fabAddProduct;
    private RecyclerView myrv;
    private List<Product> products;
    private ProductsRecyclerViewAdapter adapter;
    private RelativeLayout rootLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog pDialog;
    private LinearLayout greyLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        greyLayout = findViewById(R.id.greyLayout);
        rootLayout = findViewById(R.id.rootLayout);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        myrv = findViewById(R.id.productRecyclerView);
        Toolbar toolbar = findViewById(R.id.products_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crook Store");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        products = new ArrayList<>();

        //SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));


        //Swipe left & right recycler view items
        ItemTouchHelper.SimpleCallback itemTouchHelperCallbackLeft
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, ProductsActivity.this);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallbackRight
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, ProductsActivity.this);

        new ItemTouchHelper(itemTouchHelperCallbackLeft).attachToRecyclerView(myrv);
        new ItemTouchHelper(itemTouchHelperCallbackRight).attachToRecyclerView(myrv);


        //FAB
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddProductActivity.class);
                startActivity(i);
            }
        });

        new LoadProducts().execute();

        /*mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);


            }
        });*/


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
            if(viewHolder instanceof ProductsRecyclerViewAdapter.MyViewHolder){

                /* delete */
                if(direction == ItemTouchHelper.LEFT){
                    String name = products.get(viewHolder.getAdapterPosition()).getName();

                    final Product productToDelete = products.get(viewHolder.getAdapterPosition());
                    final int deleteIndex = viewHolder.getAdapterPosition();

                    adapter.removeProduct(deleteIndex);

                    Snackbar snackbar = Snackbar.make(rootLayout, name + " removed from product list", Snackbar.LENGTH_SHORT);

                    snackbar.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT ||
                                    event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                                new DeleteProduct().execute(productToDelete.getId());
                            }
                        }
                    });

                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.restoreProduct(productToDelete, deleteIndex);
                        }
                    });

                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                } else { /* edit */
                    final int editIndex = viewHolder.getAdapterPosition();
                    final Product productToEdit = products.get(editIndex);

                    String name = products.get(editIndex).getName();

                    adapter.notifyItemChanged(editIndex);

                    Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("id", productToEdit.getId());
                    extras.putInt("category_id", productToEdit.getCategoryId());
                    extras.putString("name", productToEdit.getName());
                    extras.putString("desc", productToEdit.getDesc());
                    extras.putString("price", productToEdit.getPrice());

                    /* Put bitmap in file and then pass filename in extras*/
                    /*try {
                        String filename = "thumb.png";
                        Bitmap thumb = productToEdit.getThumb();
                        FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                        thumb.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        //Cleanup
                        stream.close();

                        if (thumb != null && !thumb.isRecycled()) {
                            thumb.recycle();
                            thumb = null;
                        }

                        extras.putString("filename", filename);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    intent.putExtras(extras);
                    startActivity(intent);
                }

            }
    }

    @Override
    public void onChildDraw(RecyclerView.ViewHolder viewHolder, float dX) {

        if(viewHolder instanceof ProductsRecyclerViewAdapter.MyViewHolder){

            //if right (edit)
            if(dX > 0){
                ((ProductsRecyclerViewAdapter.MyViewHolder) viewHolder).viewBackgroundEdit.setVisibility(View.VISIBLE);
                ((ProductsRecyclerViewAdapter.MyViewHolder) viewHolder).viewBackgroundDelete.setVisibility(View.INVISIBLE);
            } else {
                ((ProductsRecyclerViewAdapter.MyViewHolder) viewHolder).viewBackgroundEdit.setVisibility(View.INVISIBLE);
                ((ProductsRecyclerViewAdapter.MyViewHolder) viewHolder).viewBackgroundDelete.setVisibility(View.VISIBLE);
            }

        }
    }


    @Override
    public void onRefresh() {
        /*finish();
        startActivity(getIntent());*/
        greyLayout.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        products.clear();
        adapter.notifyDataSetChanged();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new LoadProducts().execute();

    }

    private void clearRecyclerView(){
        for(int i = 0; i<products.size(); i++){
            products.remove(i);
            adapter.notifyItemRemoved(i);
        }
    }


    class LoadProducts extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!mSwipeRefreshLayout.isRefreshing()){
                pDialog = new ProgressDialog(ProductsActivity.this);
                pDialog.setMessage("Loading products. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(!mSwipeRefreshLayout.isRefreshing())
                pDialog.dismiss();
            else {
                greyLayout.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductsActivity.this);
                    myrv.setLayoutManager(layoutManager);
                    myrv.setItemAnimator(new DefaultItemAnimator());
                    myrv.addItemDecoration(new DividerItemDecoration(ProductsActivity.this, DividerItemDecoration.VERTICAL));
                    adapter = new ProductsRecyclerViewAdapter(ProductsActivity.this, products);
                    myrv.setAdapter(adapter);



                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {

            Get_Request_Handler get = new Get_Request_Handler();
            String response = get.performGetCall(url_get_products);

            Log.d(TAG, "doInBackground: response=" + response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray("products");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject p = jsonArray.getJSONObject(i);

                    int id = p.getInt("id");
                    int categoryId = p.getInt("category_id");
                    String name = p.getString("name");
                    String desc = p.getString("desc");
                    String price = p .getString("price");
                    String thumbUrl = p.getString("thumb");
                    Bitmap thumb = getBitmapFromURL(thumbUrl);
                    //Bitmap thumb = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    //        R.drawable.produce);
                    Log.d(TAG, "doInBackground: name=" + name);

                    Product product = new Product(id, categoryId, name, desc, price, thumb);
                    products.add(product);

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

    class DeleteProduct extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... integers) {

            String product_id = String.valueOf(integers[0]);

            HashMap<String, String> params = new HashMap<>();
            params.put("id", product_id);

            Delete_Request_Handler delete = new Delete_Request_Handler();
            String response = delete.performDeleteCall(url_delete_product, params);
            Log.d(TAG, "doInBackground: response:" + response);
            try {
                JSONObject jsonObject = new JSONObject(response);

                int success = jsonObject.getInt("success");
                String server_message = jsonObject.getString("message");

                Log.d(TAG, "doInBackground: server message:" + server_message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

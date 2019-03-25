package com.example.stath.crookclient.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.stath.crookclient.Activity.ProductsActivity;
import com.example.stath.crookclient.Adapter.CartItemsRecyclerViewAdapter;
import com.example.stath.crookclient.Adapter.ProductsRecyclerViewAdapter;
import com.example.stath.crookclient.Connection.Connect;
import com.example.stath.crookclient.Connection.Get_Request_Handler;
import com.example.stath.crookclient.Connection.Post_Request_Handler;
import com.example.stath.crookclient.Model.CartItem;
import com.example.stath.crookclient.Model.Product;
import com.example.stath.crookclient.R;
import com.example.stath.crookclient.SQLiteDatabase.DatabaseHelper;
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

public class FragmentCart extends Fragment {

    private static final String TAG = "FragmentCart";
    private static String url_get_cart_products;
    private static final String url_publish_cart = "http://" + Connect.host +"/crook/api/cart/publish.php";

    private CartItemsRecyclerViewAdapter adapter;
    private DatabaseHelper crook_db;

    private List<CartItem> cartitems;

    private View view;
    private RecyclerView cartitems_rv;
    private ProgressDialog pDialog;

    private boolean emptycart = false;

    private FloatingActionButton publishCart;
    private FloatingActionButton displayCartPrice;

    public FragmentCart(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cart_fragment, container, false);

        cartitems_rv = view.findViewById(R.id.cartitems_recyclerview);
        publishCart = view.findViewById(R.id.fabPublishCart);
        displayCartPrice = view.findViewById(R.id.fabDisplayCartPrice);

        cartitems = new ArrayList<>();
        crook_db = new DatabaseHelper(getActivity());

        new LoadCart().execute();

        publishCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));

                new PublishCart().execute();
            }
        });

        displayCartPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                double cartPrice = 0D;
                /* Gather cartitem prices */
                for(CartItem item: cartitems){
                    Log.d(TAG, "onClick: itemPrice" + item.getPrice());
                    cartPrice += item.getPrice();
                }

                String price = String.format("%.2f", cartPrice);
                Log.d(TAG, "onClick: Price" + price);
                Toast.makeText(getActivity(), price, Toast.LENGTH_SHORT).show();


            }
        });
        return view;
    }

    class LoadCart extends AsyncTask<Void, Void, Integer> {

        private String server_message = "default";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Cart. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if(emptycart)
                Toast.makeText(getActivity(), "Your cart is empty", Toast.LENGTH_LONG).show();
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CartItemsRecyclerViewAdapter( getActivity(), cartitems);
                        cartitems_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        cartitems_rv.setAdapter(adapter);
                    }
                });
            }



        }

        @Override
        protected Integer doInBackground(Void... voids) {

            int success = 0;
            int product_id, product_quantity;

            HashMap<Integer, Integer> localCart = new HashMap<>();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(Connect.host)
                    .appendPath("crook")
                    .appendPath("api")
                    .appendPath("product")
                    .appendPath("read_using_multiple_ids.php");

            /* Read from SQLite database the product ids */
            Cursor res = crook_db.getCart();

            if(res.getCount() == 0 ){
                emptycart = true;
            } else {
                emptycart = false;
                while(res.moveToNext()){

                    Log.d(TAG, "doInBackground: product_id: " + String.valueOf(res.getInt(1)));
                    product_id = res.getInt(1);
                    product_quantity = res.getInt(2);

                    localCart.put(product_id, product_quantity);
                    builder.appendQueryParameter("product_id[]", String.valueOf(product_id));
                }
                printMap(localCart);
                url_get_cart_products = builder.toString();

                Log.d(TAG, "doInBackground: url:" + url_get_cart_products);

                /* Call the REST api to get the products which have the ids retrieved */
                Get_Request_Handler get = new Get_Request_Handler();
                String result = get.performGetCall(url_get_cart_products);

                Log.d(TAG, "doInBackground: result:" + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray productsArray = jsonObject.getJSONArray("products");

                    for(int i=0; i<productsArray.length(); i++){

                        JSONObject p = productsArray.getJSONObject(i);
                        Log.d(TAG, "Product: " + p.getString("name"));

                        int pid = p.getInt("id");
                        int quantity = localCart.get(pid);
                        double product_price = p.getDouble("price");
                        double price = product_price * quantity;
                        String pname = p.getString("name");
                        String pdesc = p.getString("desc");
                        Bitmap pthumb = getBitmapFromURL(p.getString("thumb"));

                        Log.d(TAG, "doInBackground: product_price" + product_price);
                        Log.d(TAG, "doInBackground: price" + price);


                        CartItem cartItem = new CartItem(pid, price, quantity, pname, pdesc, product_price, pthumb);
                        cartitems.add(cartItem);

                    }

                    success = jsonObject.getInt("success");
                    server_message = jsonObject.getString("message");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return success;
        }

        private void printMap(HashMap<Integer, Integer> map){
            for (HashMap.Entry<Integer, Integer> entry : map.entrySet()) {
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                Log.d(TAG, "key:" + key + " --- " + value);
            }
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

        private String ProductIdsToJSONFormat(){

            boolean first = true;
            List<Integer> product_ids = new ArrayList<>();
            StringBuilder builder = new StringBuilder();

            builder.append("{ \"email\" : \"" + UserStatus.getUserEmail(getActivity()) + "\", ");
            builder.append("\"product_ids\" : [ ");
            for  ( int id : product_ids ) {
                if(first){
                    first = false;
                    builder.append(String.valueOf(id));
                }
                else
                    builder.append(", " + String.valueOf(id));
            }
            builder.append(" ] }");

            Log.d(TAG, "convertCartToJSON: " + builder.toString());

            return builder.toString();
        }
    }

    class PublishCart extends AsyncTask<Void, Void, Integer>{

        private String server_message = "default";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Publishing Cart...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            pDialog.dismiss();
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            int success = 0;

            HashMap<String, String> params = new HashMap<>();
            params.put("email", UserStatus.getUserEmail(getActivity()));

            for(CartItem item : cartitems){
                params.put("cartitems[]", String.valueOf(item.getId()));
            }

            String jsonParams = buildJSONObject(params);
            Log.d(TAG, "jsonParams = " + jsonParams);

            Post_Request_Handler post = new Post_Request_Handler();
            String response = post.performPostCall(url_publish_cart, null, true, jsonParams);

            Log.d(TAG, "Publish Cart: response=" + response);





            return null;
        }

        private String buildJSONObject(HashMap<String, String> params){
            StringBuilder builder = new StringBuilder();
            boolean first = true;

            builder.append("{ \"email\" : \"" + UserStatus.getUserEmail(getActivity()) + "\", \"cartitems\" : [ ");
            for(CartItem item : cartitems) {
                if(first){
                    builder.append("{ \"product_id\" : \"" + item.getId() + "\" , \"quantity\" : \"" + item.getQuantity() + "\" }");
                    first = false;
                }
                else
                    builder.append(", { \"product_id\" : \"" + item.getId() + "\" , \"quantity\" : \"" + item.getQuantity() + "\" } ");
            }
            builder.append(" ] }");


            return builder.toString();
        }

        private String removeLastChar(String str) {
            return str.substring(0, str.length() - 1);
        }

        private void printMap(HashMap<String, String> map){
            for (HashMap.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d(TAG, "key:" + key + " --- " + value);
            }
        }
    }
}

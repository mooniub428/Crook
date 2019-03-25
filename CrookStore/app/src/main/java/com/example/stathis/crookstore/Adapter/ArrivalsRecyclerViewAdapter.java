package com.example.stathis.crookstore.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stathis.crookstore.Connection.Connect;
import com.example.stathis.crookstore.Connection.Get_Request_Handler;
import com.example.stathis.crookstore.MainActivity;
import com.example.stathis.crookstore.Model.Arrival;
import com.example.stathis.crookstore.Model.Product;
import com.example.stathis.crookstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ArrivalsRecyclerViewAdapter extends RecyclerView.Adapter<ArrivalsRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = "ArrivalsRecyclerViewAda";
    private Context mContext;
    private List<Arrival> mData;
    private HashMap<String, Integer> cart;

    public ArrivalsRecyclerViewAdapter(Context mContext, List<Arrival> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arrivals_recyclerview_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.email.setText(mData.get(position).getEmail());
        holder.name.setText(mData.get(position).getFirstname() + " " + mData.get(position).getLastname());

        double time_remain = Double.parseDouble(mData.get(position).getTime_remain());
        double minutes_remain = time_remain * 60;
        holder.time.setText(minutes_remain + " minutes");

        holder.arrivalCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LoadCart().execute(mData.get(position).getEmail());
            }
        });
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView email;
        private TextView name;
        private TextView time;
        private CardView arrivalCardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.email);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            arrivalCardView = itemView.findViewById(R.id.arrivalCardView);
        }
    }

    class LoadCart extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

        }

        @Override
        protected String doInBackground(String... strings) {

            String userEmail = strings[0];
            StringBuilder strbuilder = new StringBuilder();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(Connect.host)
                    .appendPath("crook")
                    .appendPath("api")
                    .appendPath("cartitem")
                    .appendPath("read_using_email.php")
                    .appendQueryParameter("email", userEmail);

            /*Get cartitems of uid */
            String url_get_cartitems = builder.toString();
            Log.d(TAG, "url="+url_get_cartitems);

            Get_Request_Handler get = new Get_Request_Handler();
            String result = get.performGetCall(url_get_cartitems);

            Log.d(TAG, "result="+ result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("cartitems");


                strbuilder.append("Product " + "\t" + "Quantity\n");

                for(int i = 0; i<jsonArray.length(); i++){

                    JSONObject ci = jsonArray.getJSONObject(i);
                    strbuilder.append(ci.getString("product_name") + "\t" + ci.getInt("quantity") + "\n");
                }
                Log.d(TAG, "strbuilder =  " + strbuilder.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return strbuilder.toString();
        }

        private void showMessage(String title, String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }
    }
}

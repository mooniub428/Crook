package com.example.stath.crookclient.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stath.crookclient.Model.Category;
import com.example.stath.crookclient.Activity.ProductsActivity;
import com.example.stath.crookclient.R;

import java.util.List;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Category> mData;
    private static final String TAG = "RecyclerViewAdapter";


    public CategoriesRecyclerViewAdapter(Context mContext, List<Category> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.category_recyclerview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.category_name.setText(mData.get(position).getName());
        holder.category_img.setImageBitmap(mData.get(position).getThumb());

        holder.category_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on: " + mData.get(position).getName());
                Toast.makeText(mContext, mData.get(position).getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, ProductsActivity.class);

                //passing category name to products activity
                intent.putExtra("category_id", mData.get(position).getId());
                //start activity
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category_name;
        ImageView category_img;

        public MyViewHolder(View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);
            category_img = itemView.findViewById(R.id.category_image);
        }
    }
}

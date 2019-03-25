package com.example.stathis.crookstore.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stathis.crookstore.Model.Category;
import com.example.stathis.crookstore.R;

import java.util.List;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Category> mData;
    private static final String TAG = "CategoriesViewAdapter";

    public int selectedItem = -1;


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
        holder.category_desc.setText(mData.get(position).getDesc());
        holder.category_thumb.setImageBitmap(mData.get(position).getThumb());

        if(position != selectedItem){
            holder.category_cardview.setBackgroundColor(Color.parseColor("#fafafa")); //reset background
        } else {
            holder.category_cardview.setBackgroundColor(Color.parseColor("#F9D37C")); //ffffe0
        }

        holder.category_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedItem = position;

                //Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();

                notifyDataSetChanged();
               /* Log.d(TAG, "Clicked on: " + mData.get(position).getName());
                Toast.makeText(mContext, mData.get(position).getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, Products.class);

                //passing category name to products activity
                intent.putExtra("category_id", mData.get(position).getId());
                //start activity
                mContext.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category_name;
        TextView category_desc;
        ImageView category_thumb;
        CardView category_cardview;

        public MyViewHolder(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
            category_desc = itemView.findViewById(R.id.category_desc);
            category_thumb = itemView.findViewById(R.id.category_thumb);
            category_cardview = itemView.findViewById(R.id.category_cardview);
        }
    }
}

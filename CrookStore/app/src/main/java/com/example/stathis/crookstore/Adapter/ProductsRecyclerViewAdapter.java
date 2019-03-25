package com.example.stathis.crookstore.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.stathis.crookstore.Model.Product;
import com.example.stathis.crookstore.R;

import java.util.List;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Product> mData;

    public ProductsRecyclerViewAdapter(Context mContext, List<Product> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_recyclerview_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Product product = mData.get(position);
        holder.name.setText(product.getName());
        holder.desc.setText(product.getDesc());
        holder.price.setText(product.getPrice());
        holder.thumb.setImageBitmap(product.getThumb());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void removeProduct(int position){
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreProduct(Product product, int position){
        mData.add(position,product);
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name, desc, price;
        public ImageView thumb;
        public RelativeLayout viewBackgroundEdit, viewBackgroundDelete, viewForeground;


        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            desc = itemView.findViewById(R.id.product_desc);
            price = itemView.findViewById(R.id.product_price);
            thumb = itemView.findViewById(R.id.product_thumbnail);
            viewBackgroundEdit = itemView.findViewById(R.id.view_background_edit);
            viewBackgroundDelete = itemView.findViewById(R.id.view_background_delete);
            viewForeground = itemView.findViewById(R.id.view_foreground);

        }
    }
}

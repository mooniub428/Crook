package com.example.stath.crookclient.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.stath.crookclient.Model.Product;
import com.example.stath.crookclient.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Product> mData;
    public List<Boolean> selectedProducts = new ArrayList<>();

    private static final String TAG = "ProductsRecyclerViewAda";

    public ProductsRecyclerViewAdapter(Context mContext, List<Product> mProducts) {
        this.mContext = mContext;
        this.mData = mProducts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.product_recyclerview_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.product_name.setText(mData.get(position).getName());
        holder.product_desc.setText(mData.get(position).getDesc());
        holder.product_price.setText(mData.get(position).getPrice());
        holder.product_thumbnail.setImageBitmap(mData.get(position).getThumbnail());

        selectedProducts.add(false);

        /*holder.product_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on " + mData.get(position).getName());

                if(!selectedProducts.get(position)){
                    v.setBackgroundColor(Color.parseColor("#F9D37C"));
                    selectedProducts.set(position, true);
                }
                else {
                    v.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    selectedProducts.set(position, false);
                }

            }
        });*/
    }

    public void removeProduct(int position){
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreProduct(Product product, int position){
        mData.add(position,product);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView product_name;
        TextView product_desc;
        TextView product_price;
        public TextView addtocart;
        public TextView removefromcart;
        ImageView product_thumbnail;
        CardView product_cardview;
        public RelativeLayout foregroundView, backgroundView;


        public MyViewHolder(View itemView){
            super(itemView);

            product_name = itemView.findViewById(R.id.product_name);
            product_desc = itemView.findViewById(R.id.product_desc);
            product_price = itemView.findViewById(R.id.product_price);
            addtocart = itemView.findViewById(R.id.addtocart);
            removefromcart = itemView.findViewById(R.id.removefromcart);
            product_thumbnail = itemView.findViewById(R.id.product_thumbnail);
            foregroundView = itemView.findViewById(R.id.foreground_view);
            backgroundView = itemView.findViewById(R.id.background_view);
        }
    }
}

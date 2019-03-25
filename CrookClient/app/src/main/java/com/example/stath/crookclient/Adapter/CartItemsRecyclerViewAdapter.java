package com.example.stath.crookclient.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stath.crookclient.Model.CartItem;
import com.example.stath.crookclient.R;
import com.example.stath.crookclient.SQLiteDatabase.DatabaseHelper;

import java.text.DecimalFormat;
import java.util.List;

import static java.lang.Math.round;

public class CartItemsRecyclerViewAdapter extends RecyclerView.Adapter<CartItemsRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<CartItem> mData;
    DecimalFormat df;

    public CartItemsRecyclerViewAdapter(Context mContext, List<CartItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
        df = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public CartItemsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cartitem_recyclerview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemsRecyclerViewAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(mData.get(position).getProductName());
        holder.desc.setText(mData.get(position).getProductDesc());
        holder.thumbnail.setImageBitmap(mData.get(position).getProductThumb());
        holder.quantity.setText(String.valueOf(mData.get(position).getQuantity()));

        String cartItemPrice = String.format("%.2f", mData.get(position).getPrice());
        holder.price.setText(cartItemPrice);

        holder.incQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                /* Increase quantity of cartitem in SQLite Database */
                incQuantityOfProduct(mData.get(position).getId());

                /* Update cart item in cartitems list */
                int oldQuantity = mData.get(position).getQuantity();
                double productPrice = mData.get(position).getPrice() / oldQuantity;
                mData.get(position).setQuantity(oldQuantity + 1);
                mData.get(position).setPrice(productPrice * (oldQuantity + 1));

                /* Update cart item in recycler view */
                notifyItemChanged(position);
            }
        });
        holder.decQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                /* Decrease quantity of cartitem in SQLite database */
                int newQuantity = decQuantityOfProduct(mData.get(position).getId());

                if(newQuantity == 0){
                    mData.remove(position);
                    notifyItemRemoved(position);
                } else {
                    /* Update cartitem of cartitems list and recyclerview */
                    int oldQuantity = newQuantity + 1;
                    double productPrice = mData.get(position).getPrice() / oldQuantity;
                    double cartItemPrice = mData.get(position).getPrice();
                    mData.get(position).setQuantity(mData.get(position).getQuantity() - 1);
                    mData.get(position).setPrice(cartItemPrice - productPrice);
                    notifyItemChanged(position);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void incQuantityOfProduct(int pid){
        DatabaseHelper db = new DatabaseHelper(mContext);
        db.incQuantityOfProduct(pid);
    }

    private int decQuantityOfProduct(int pid){
        DatabaseHelper db = new DatabaseHelper(mContext);
        return db.decQuantityOfProduct(pid); //returns the new quantity
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, desc, price, quantity;
        ImageView thumbnail;
        Button incQuantity, decQuantity;
        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cartitem_name);
            desc = itemView.findViewById(R.id.cartitem_desc);
            price = itemView.findViewById(R.id.cartitem_price);
            quantity = itemView.findViewById(R.id.cartitem_quantity);
            thumbnail = itemView.findViewById(R.id.cartitem_thumb);
            incQuantity = itemView.findViewById(R.id.inc_quantity);
            decQuantity = itemView.findViewById(R.id.dec_quantity);
        }
    }
}

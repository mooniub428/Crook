package com.example.stath.crookclient.Model;

import android.graphics.Bitmap;

public class CartItem {

    private int id;
    private double price;
    private int quantity;
    private String productName;
    private String productDesc;
    private double productPrice;
    private Bitmap productThumb;

    public CartItem(int id, double price, int quantity, String productName, String productDesc, double productPrice, Bitmap productThumb) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.productName = productName;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.productThumb = productThumb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public Bitmap getProductThumb() {
        return productThumb;
    }

    public void setProductThumb(Bitmap productThumb) {
        this.productThumb = productThumb;
    }
}

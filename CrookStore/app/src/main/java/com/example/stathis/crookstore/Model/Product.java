package com.example.stathis.crookstore.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Product {

    private int id;
    private int categoryId;
    private String name;
    private String desc;
    private String price;
    private Bitmap thumb;

    public Product(int id, int categoryId, String name, String desc, String price, Bitmap thumb) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.thumb = thumb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}

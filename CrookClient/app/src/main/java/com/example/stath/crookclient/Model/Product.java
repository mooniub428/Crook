package com.example.stath.crookclient.Model;

import android.graphics.Bitmap;

public class Product {

    private int id;
    private String name;
    private String desc;
    private String price;
    private Bitmap thumbnail;

    public Product(int id, String name, String desc, String price, Bitmap thumbnail) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}

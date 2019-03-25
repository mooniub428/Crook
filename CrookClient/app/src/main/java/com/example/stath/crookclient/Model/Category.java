package com.example.stath.crookclient.Model;

import android.graphics.Bitmap;

public class Category {

    private int id;
    private String name;
    private String desc;
    private Bitmap thumb;

    public Category(){

    }

    public Category(int id, String name, String desc, Bitmap thumb) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.thumb = thumb;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}

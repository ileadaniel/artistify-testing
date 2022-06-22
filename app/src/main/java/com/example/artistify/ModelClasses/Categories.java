package com.example.artistify.ModelClasses;

public class Categories {

    String category_name,cover_image_url;


    public Categories(){

    }

    public Categories(String category_name, String cover_image_url) {
        this.category_name = category_name;
        this.cover_image_url = cover_image_url;
    }


    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }
}

package com.example.artistify.ModelClasses;

public class Image {

    private String imageUrl;
    private String userId;
    private String category;
    private String title;
    private long timestamp_upload;
    private int winner_photo;

    public Image(){


    }

    public Image(String imageUrl, String userId, String category, String title, long timestamp_upload, int winner_photo) {
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.timestamp_upload = timestamp_upload;
        this.winner_photo = winner_photo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp_upload() {
        return timestamp_upload;
    }

    public void setTimestamp_upload(long timestamp_upload) {
        this.timestamp_upload = timestamp_upload;
    }

    public int getWinner_photo() {
        return winner_photo;
    }

    public void setWinner_photo(int winner_photo) {
        this.winner_photo = winner_photo;
    }
}

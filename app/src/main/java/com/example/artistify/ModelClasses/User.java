package com.example.artistify.ModelClasses;

public class User {

    public String fullName, email,user_pic_url,cover_pic_url,status;
    public int authentication_level;

    public User(){

    }

    public User(String fullName,String email,int authentication_level,String profile_pic_url,String cover_pic_url,String status){
        this.fullName=fullName;
        this.email=email;
        this.authentication_level=authentication_level;
        this.user_pic_url=profile_pic_url;
        this.cover_pic_url=cover_pic_url;
        this.status=status;
    }

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public void setUser_pic_url(String profile_pic_id) {
        this.user_pic_url= profile_pic_id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public int getAuthentication_level() {
        return authentication_level;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthentication_level(int authentication_level) {
        this.authentication_level = authentication_level;
    }

    public String getCover_pic_url() {
        return cover_pic_url;
    }

    public void setCover_pic_url(String cover_pic_url) {
        this.cover_pic_url = cover_pic_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

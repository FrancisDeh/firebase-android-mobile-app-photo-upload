package com.francisdeh.comdepapp;

/**
 * Created by FrancisDeh on 10/27/2017.
 */

public class Member {

    private String Name;
    private String Image;
    private String Level;

    public Member() {
    }

    public Member(String Name, String Image, String Level) {
       this.Name = Name;
        this.Image = Image;
        this.Level = Level;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getImage() {
        return this.Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getLevel() {
        return this.Level;
    }

    public void setLevel(String Level) {
        this.Level = Level;
    }







}

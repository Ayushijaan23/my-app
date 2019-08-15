package com.example.ayu.phasetwoproject;

public class UserData {
    private String mName;
    private String mLocation;
    private String mLinks;
    private String mMobile;

    public UserData(String mName, String mLocation, String mLinks, String mMobile) {
        this.mName = mName;
        this.mLocation = mLocation;
        this.mLinks = mLinks;
        this.mMobile = mMobile;
    }

    public String getmName() {
        return mName;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmLinks() {
        return mLinks;
    }

    public String getmMobile() {
        return mMobile;
    }
}

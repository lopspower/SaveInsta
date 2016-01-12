package com.mikhaellopez.saveinstagram.controller.model;

/**
 * Created by mlopez on 11/01/16.
 */
public class EventInstaPictureLoad {

    private String userName;
    private String urlImage;
    private String urlIconProfil;
    private String userFullName;

    public EventInstaPictureLoad(String userName, String userFullName, String urlImage, String urlIconProfil) {
        this.userName = userName;
        this.userFullName = userFullName;
        this.urlImage = urlImage;
        this.urlIconProfil = urlIconProfil;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlIconProfil() {
        return urlIconProfil;
    }

    public void setUrlIconProfil(String urlIconProfil) {
        this.urlIconProfil = urlIconProfil;
    }
}

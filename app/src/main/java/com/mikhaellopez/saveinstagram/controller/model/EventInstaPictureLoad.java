package com.mikhaellopez.saveinstagram.controller.model;

/**
 * Created by mlopez on 11/01/16.
 */
public class EventInstaPictureLoad {

    private String urlImage;
    private String urlIconProfil;
    private String profilName;

    public EventInstaPictureLoad(String urlImage, String urlIconProfil, String profilName) {
        this.urlImage = urlImage;
        this.urlIconProfil = urlIconProfil;
        this.profilName = profilName;
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

    public String getProfilName() {
        return profilName;
    }

    public void setProfilName(String profilName) {
        this.profilName = profilName;
    }
}

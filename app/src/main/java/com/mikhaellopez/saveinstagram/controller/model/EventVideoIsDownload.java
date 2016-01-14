package com.mikhaellopez.saveinstagram.controller.model;

/**
 * Created by Mikhael LOPEZ on 14/01/16.
 */
public class EventVideoIsDownload {

    private boolean isDownload;

    public EventVideoIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public boolean isDownload() {
        return isDownload;
    }
}

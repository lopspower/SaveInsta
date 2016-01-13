package com.mikhaellopez.saveinstagram.controller.model;

/**
 * Created by mlopez on 12/01/16.
 */
public class InstaMedia {

    private String id;
    private InstaOwner owner;

    public InstaMedia() {
        super();
    }

    public InstaMedia(String id, InstaOwner owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public InstaOwner getOwner() {
        return owner;
    }
}

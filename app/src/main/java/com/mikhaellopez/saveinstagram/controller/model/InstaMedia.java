package com.mikhaellopez.saveinstagram.controller.model;

/**
 * Created by mlopez on 12/01/16.
 */
public class InstaMedia {

    private InstaOwner owner;

    public InstaMedia() {
        super();
    }

    public InstaMedia(InstaOwner owner) {
        this.owner = owner;
    }

    public InstaOwner getOwner() {
        return owner;
    }
}

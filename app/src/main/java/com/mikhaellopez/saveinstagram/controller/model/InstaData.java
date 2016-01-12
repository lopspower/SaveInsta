package com.mikhaellopez.saveinstagram.controller.model;

/**
 * Created by mlopez on 12/01/16.
 */
public class InstaData {

    private InstaEntryData entry_data;

    public InstaData() {
        super();
    }

    public InstaData(InstaEntryData entry_data) {
        this.entry_data = entry_data;
    }

    public InstaEntryData getEntry_data() {
        return entry_data;
    }
}

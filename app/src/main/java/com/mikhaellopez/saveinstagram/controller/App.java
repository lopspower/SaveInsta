package com.mikhaellopez.saveinstagram.controller;

import android.app.Application;

/**
 * Created by mlopez on 11/01/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/century_gothic.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );*/
    }
}

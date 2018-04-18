package com.plumya.bakingapp;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by miltomasz on 17/04/18.
 */

public class BakeingAppApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
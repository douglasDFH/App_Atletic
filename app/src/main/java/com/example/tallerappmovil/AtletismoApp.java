package com.example.tallerappmovil;

import android.app.Application;
import android.content.Context;

public class AtletismoApp extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getContext() {
        return appContext;
    }
}

package com.joe.eyer;

import android.app.Application;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class CoreApplication extends Application {

    private static CoreApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CoreApplication getInstance() {
        return instance;
    }
}
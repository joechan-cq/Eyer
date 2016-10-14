package com.joe.eyer.config;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */
public class AppConfig {
    private static AppConfig ourInstance;

    public static AppConfig getInstance() {
        if (ourInstance == null) {
            synchronized (AppConfig.class) {
                if (ourInstance == null) {
                    ourInstance = new AppConfig();
                }
            }
        }
        return ourInstance;
    }

    private AppConfig() {
    }
}

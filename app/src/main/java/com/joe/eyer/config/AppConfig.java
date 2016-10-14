package com.joe.eyer.config;

import android.os.Environment;

import java.io.File;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */
public class AppConfig {
    private static AppConfig ourInstance;
    public File dataDirectory;
    private static final String SUFFIX_DATA = "traineddata";

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
        File root = new File(Environment.getExternalStorageDirectory(), "EyerData");
        dataDirectory = new File(root, "tessdata");
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
    }

    public boolean checkDataExist(String lang) {
        return false;
    }
}

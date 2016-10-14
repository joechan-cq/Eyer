package com.joe.eyer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.joe.eyer.CoreApplication;

import java.io.File;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */
public class AppConfig {
    private static AppConfig ourInstance;
    public File dataDirectory;
    private static final String SUFFIX_DATA = "traineddata";
    private SharedPreferences sp;

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
        sp = CoreApplication.getInstance().getSharedPreferences("Eyer", Context.MODE_PRIVATE);
        File root = new File(Environment.getExternalStorageDirectory(), "EyerData");
        dataDirectory = new File(root, "tessdata");
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
    }

    public boolean checkDataExist(String lang) {
        String[] fileNames = dataDirectory.list();
        for (String fileName : fileNames) {
            if (fileName.equals(lang + "." + SUFFIX_DATA)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLanguageEnable(String language) {
        return sp.getBoolean(language, false);
    }

    public void setLanguageEnable(String language, boolean isEnable) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(language, isEnable);
        editor.apply();
    }
}

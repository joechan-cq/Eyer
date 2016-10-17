package com.joe.eyer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.joe.eyer.CoreApplication;
import com.joe.eyer.R;

import java.io.File;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */
public class AppConfig {
    private static AppConfig ourInstance;
    public File dataDirectory;
    public File root;
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
        root = new File(Environment.getExternalStorageDirectory(), "EyerData");
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

    public void deleteLanguageData(String lang) {
        setLanguageEnable(lang, false);
        setDownloading(lang, false);
        File[] files = dataDirectory.listFiles();
        for (File file : files) {
            if (file.getName().startsWith(lang)) {
                file.delete();
            }
        }
    }

    public boolean isLanguageEnable(String language) {
        return sp.getBoolean(language + "ENABLE", false);
    }

    public void setLanguageEnable(String language, boolean isEnable) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(language + "ENABLE", isEnable);
        editor.apply();
    }

    public boolean isDownloading(String language) {
        return sp.getBoolean(language + "DOWNLOADING", false);
    }

    public void setDownloading(String language, boolean isEnable) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(language + "DOWNLOADING", isEnable);
        editor.apply();
    }

    public String getChosenMode() {
        StringBuilder result = new StringBuilder("");
        String[] languages = CoreApplication.getInstance().getResources().getStringArray(R.array.languages);
        for (String language : languages) {
            if (isLanguageEnable(language)) {
                result.append("+").append(language);
            }
        }
        if (result.length() > 0) {
            return result.substring(1);
        } else {
            return null;
        }
    }
}

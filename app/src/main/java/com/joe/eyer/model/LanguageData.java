package com.joe.eyer.model;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class LanguageData {
    private String name;
    private boolean isDownloaded;
    private boolean isEnable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
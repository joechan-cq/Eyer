package com.joe.eyer.model;

import com.joe.eyer.CoreApplication;
import com.joe.eyer.R;
import com.joe.eyer.config.AppConfig;
import com.joe.eyer.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class LanguageFactory {

    public static List<LanguageData> getLanguages() {
        List<LanguageData> languages = new ArrayList<>();
        String[] languageNames = CoreApplication.getInstance().getResources().getStringArray(R.array.languages);
        for (String languageName : languageNames) {
            LanguageData data = new LanguageData();
            data.setName(languageName);
            data.setEnable(AppConfig.getInstance().isLanguageEnable(languageName));
            data.setDownloaded(AppConfig.getInstance().checkDataExist(languageName));
            data.setDownloading(AppConfig.getInstance().isDownloading(languageName));
            if (data.isEnable() && (!data.isDownloaded() || data.isDownloading())) {
                data.setEnable(false);
                AppConfig.getInstance().setLanguageEnable(languageName, false);
            }
            languages.add(data);
        }
        return languages;
    }

    public static String getLanguageDataUrl(String lang) {
        String[] urls = CoreApplication.getInstance().getResources().getStringArray(R.array.languages_data_url);
        for (String url : urls) {
            if (FileUtils.getFileName(url).toLowerCase().equals(lang + ".zip")) {
                return url;
            }
        }
        return null;
    }
}
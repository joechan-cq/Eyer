package com.joe.eyer.model;

import com.joe.eyer.CoreApplication;
import com.joe.eyer.R;
import com.joe.eyer.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class LanguageFactory {

    public List<LanguageData> getLanguages() {
        List<LanguageData> languages = new ArrayList<>();
        String[] languageNames = CoreApplication.getInstance().getResources().getStringArray(R.array.languages);
        for (String languageName : languageNames) {
            LanguageData data = new LanguageData();
            data.setName(languageName);
            data.setEnable(AppConfig.getInstance().isLanguageEnable(languageName));
            data.setDownloaded(AppConfig.getInstance().checkDataExist(languageName));
        }
        return languages;
    }
}
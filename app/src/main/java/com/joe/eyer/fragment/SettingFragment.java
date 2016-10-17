package com.joe.eyer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joe.eyer.R;
import com.joe.eyer.activity.SettingActivity;
import com.joe.eyer.config.AppConfig;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    LanguageManagerFragment languageManagerFragment;
    private AppCompatTextView chosenModeTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.layout_set_language).setOnClickListener(this);
        view.findViewById(R.id.tv_set_about).setOnClickListener(this);
        chosenModeTv = (AppCompatTextView) view.findViewById(R.id.tv_choose_mode);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        languageManagerFragment = new LanguageManagerFragment();
        getActivity().setTitle(R.string.set);
        String temp = AppConfig.getInstance().getChosenMode();
        if (!TextUtils.isEmpty(temp)) {
            chosenModeTv.setText(temp);
        } else {
            chosenModeTv.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_set_language:
                ((SettingActivity) getActivity()).replaceFragment(languageManagerFragment);
                break;
            case R.id.tv_set_about:
                break;
        }
    }
}
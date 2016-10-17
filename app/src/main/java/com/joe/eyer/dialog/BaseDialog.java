package com.joe.eyer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.joe.eyer.R;

/**
 * Description
 * Created by chenqiao on 2016/10/17.
 */

public class BaseDialog extends Dialog {

    private TextView titleTv, contentTv, confirmTv, cancelTv, otherTv;
    private FrameLayout contentLayout;

    public BaseDialog(Context context) {
        super(context, R.style.BaseDialog);
        setContentView(R.layout.dialog_base);
        titleTv = (TextView) findViewById(R.id.tv_dialog_title);
        contentTv = (TextView) findViewById(R.id.tv_dialog_content);
        confirmTv = (TextView) findViewById(R.id.tv_dialog_confirm);
        otherTv = (TextView) findViewById(R.id.tv_dialog_other);
        cancelTv = (TextView) findViewById(R.id.tv_dialog_cancel);
        contentLayout = (FrameLayout) findViewById(R.id.layout_dialog_content);

        View.OnClickListener defaultClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        confirmTv.setOnClickListener(defaultClick);
        cancelTv.setOnClickListener(defaultClick);
    }

    public BaseDialog showTitle(@StringRes int resId) {
        return showTitle(getContext().getString(resId));
    }

    public BaseDialog showTitle(String title) {
        titleTv.setText(title);
        titleTv.setVisibility(View.VISIBLE);
        return this;
    }

    public BaseDialog setMessage(@StringRes int resId) {
        return setMessage(getContext().getString(resId));
    }

    public BaseDialog setMessage(String msg) {
        contentTv.setVisibility(View.VISIBLE);
        contentTv.setText(msg);
        return this;
    }

    public BaseDialog setDialogContentView(View view) {
        contentTv.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        contentLayout.addView(view, params);
        return this;
    }

    public BaseDialog setPositiveButton(@StringRes int resId, View.OnClickListener clickListener) {
        return setPositiveButton(getContext().getString(resId), clickListener);
    }

    public BaseDialog setPositiveButton(String text, View.OnClickListener clickListener) {
        confirmTv.setText(text);
        confirmTv.setOnClickListener(clickListener);
        return this;
    }

    public BaseDialog setCancelButton(@StringRes int resId, View.OnClickListener clickListener) {
        return setCancelButton(getContext().getString(resId), clickListener);
    }

    public BaseDialog setCancelButton(String text, View.OnClickListener clickListener) {
        cancelTv.setText(text);
        cancelTv.setOnClickListener(clickListener);
        return this;
    }

    public BaseDialog setOtherButton(@StringRes int resId, View.OnClickListener clickListener) {
        return setOtherButton(getContext().getString(resId), clickListener);
    }

    public BaseDialog setOtherButton(String text, View.OnClickListener clickListener) {
        otherTv.setVisibility(View.VISIBLE);
        otherTv.setText(text);
        otherTv.setOnClickListener(clickListener);
        return this;
    }

    public void show(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            show();
        }
    }

    public void dismiss(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            dismiss();
        }
    }
}
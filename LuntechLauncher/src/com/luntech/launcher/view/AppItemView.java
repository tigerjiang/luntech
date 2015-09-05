package com.luntech.launcher.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luntech.launcher.R;
import com.luntech.launcher.secondary.ApplicationInfo;

/**
 * Created by tiger on 15-9-4.
 */
public class AppItemView extends RelativeLayout {
    private ImageView mIconView;
    private TextView mNameView;
    private ApplicationInfo mApp;

    public AppItemView(Context context, ApplicationInfo app) {
        super(context);
        initView(context, app);

    }

    private void initView(Context context, ApplicationInfo app) {
        mApp = app;
        LayoutInflater.from(context).inflate(R.layout.app_item_icon, this);
        mIconView = (ImageView) findViewById(R.id.app_icon);
        mNameView = (TextView) findViewById(R.id.app_label);
        mIconView.setImageDrawable(mApp.getIcon());
        mNameView.setText(mApp.getTitle());
    }

    public ApplicationInfo getAppnfo() {
        return mApp;
    }
}

package com.luntech.launcher.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luntech.launcher.R;
import com.luntech.launcher.secondary.ApplicationInfo;

/**
 * Created by tiger on 15-9-4.
 */
public class GroupItemView extends RelativeLayout {
    private ImageView mIconView;
    private TextView mNameView;
    private ApplicationInfo mApp;

    public GroupItemView(Context context, ApplicationInfo app) {
        super(context);
        initView(context, app);

    }

    private void initView(Context context, ApplicationInfo app) {
        mApp = app;
        LayoutInflater.from(context).inflate(R.layout.group_item_icon, this);
        mIconView = (ImageView) findViewById(R.id.group_icon);
        mNameView = (TextView) findViewById(R.id.group_label);
        mIconView.setImageDrawable(mApp.getIcon());
        mNameView.setText(mApp.getTitle());
    }

    public ApplicationInfo getAppnfo() {
        return mApp;
    }
}

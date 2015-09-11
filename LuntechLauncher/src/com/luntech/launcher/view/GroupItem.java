package com.luntech.launcher.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luntech.launcher.R;

/**
 * Created by tiger on 15-9-4.
 */
public class GroupItem extends RelativeLayout {
    private ImageView mBgView;
    private ImageView mIconView;
    private TextView mNameView;

    public GroupItem(Context context) {
        super(context);
        initView(context);

    }

    public GroupItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GroupItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.group_item, this);
        mBgView = (ImageView) findViewById(R.id.group_bg);
        mIconView = (ImageView) findViewById(R.id.group_icon);
        mNameView = (TextView) findViewById(R.id.group_label);
    }


    public void setBgView(Drawable drawable) {
        mBgView.setImageDrawable(drawable);
    }

    public void setBgView(int drawableId) {
        mBgView.setImageResource(drawableId);
    }

    public void setIconView(Drawable drawable) {
        mIconView.setImageDrawable(drawable);
    }

    public void setIconView(int drawableId) {
        mIconView.setImageResource(drawableId);
    }

    public void setmNameView(CharSequence name) {
        mNameView.setText(name);
    }


    public void setmNameView(int nameId) {
        mNameView.setText(nameId);
    }


    public ImageView getBgView() {
        return mBgView;
    }

    public ImageView getIconView() {
        return mIconView;
    }

    public TextView getNameView() {
        return mNameView;
    }
}

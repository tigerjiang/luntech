package com.luntech.launcher.view;

import android.content.Context;
import android.graphics.Bitmap;
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
public class QisItem extends RelativeLayout {
    private ImageView mBgView;
    private ImageView mIconView;
    private TextView mNameView;

    public QisItem(Context context) {
        super(context);
        initView(context);

    }

    public QisItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public QisItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.q1s_category_item, this);
        mBgView = (ImageView) findViewById(R.id.module_bg);
        mIconView = (ImageView) findViewById(R.id.module_icon);
        mNameView = (TextView) findViewById(R.id.module_label);
    }


    public void setBgView(Bitmap drawable) {
        mBgView.setImageBitmap(drawable);
    }

    public void setBgView(int drawableId) {
        mBgView.setImageResource(drawableId);
    }

    public void setIconView(Bitmap drawable) {
        mIconView.setImageBitmap(drawable);
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


package com.luntech.launcher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.luntech.launcher.view.AutoScrollViewPager;
import com.luntech.launcher.view.ImagePagerAdapter;

/**
 * AutoScrollViewPagerDemo
 */
public class ScreenSaverActivity extends Activity {

    private AutoScrollViewPager viewPager;

    private static List<Drawable> imageIdList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_scroll_view_pager);

        viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);

        imageIdList = new ArrayList<Drawable>();
        initScreenSaverRes();
        viewPager.setAdapter(new ImagePagerAdapter(this, imageIdList).setInfiniteLoop(true));

        viewPager.setInterval(2000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2
                % ListUtils.getSize(imageIdList));

    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        viewPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume
        viewPager.startAutoScroll();
    }

    private void initScreenSaverRes() {
        if (Launcher.sScreenSaverFileList != null && Launcher.sScreenSaverFileList.size() > 0) {
            for (String filePath : Launcher.sScreenSaverFileList) {
                Drawable drawable = changeFiletoDrawable(
                        ScreenSaverActivity.this.getApplicationContext(), filePath);
                imageIdList.add(drawable);
            }
        }
    }

    public Drawable changeIdtoDrawable(Context context, String name) {
        Drawable icon = null;
        int resId = context.getResources()
                .getIdentifier(name, "drawable", context.getPackageName());
        if (resId == 0) {
            Log.e("error", context.getPackageName() + " resource not found for " + name);
        } else {
            icon = context.getResources().getDrawable(resId);
        }
        return icon;
    }

    public Drawable changeFiletoDrawable(Context context, String filePath) {
        Drawable icon = null;
        Log.d("jzh", "change path " + filePath);
        if (!TextUtils.isEmpty(filePath)) {
            icon = Drawable.createFromPath(filePath);
        }
        return icon;
    }

    public Drawable getDrawableFromAttribute(Context context, String attribute) {
        if (attribute.endsWith(".png") | attribute.endsWith(".PNG") | attribute.endsWith(".JPG")
                | attribute.endsWith(".jpg")) {
            return changeFiletoDrawable(context, attribute);
        } else {
            return changeIdtoDrawable(context, attribute);
        }
    }
    
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        finish();
    }

}

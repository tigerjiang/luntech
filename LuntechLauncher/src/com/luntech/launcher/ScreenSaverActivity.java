
package com.luntech.launcher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.luntech.launcher.view.AutoScrollViewPager;
import com.luntech.launcher.view.ImagePagerAdapter;

/**
 * AutoScrollViewPagerDemo
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-22
 */
public class ScreenSaverActivity extends Activity {

    private AutoScrollViewPager viewPager;

    private List<Drawable> imageIdList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_scroll_view_pager);

        viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);

        imageIdList = new ArrayList<Drawable>();
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
}

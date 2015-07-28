
package com.luntech.launcher;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.AppDialogFragment;

public class SettingActivity extends Activity implements OnClickListener {
    // setting menu
    private ImageView settingBaseView;
    private ImageView settingDispalyView;
    private ImageView settingNetView;
    private ImageView settingUpdateView;
    private ImageView settingMoreView;
    private ImageView settingErweiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        // setting
        settingBaseView = (ImageView) findViewById(R.id.setting_base);
        settingBaseView.setOnClickListener(this);
        settingDispalyView = (ImageView) findViewById(R.id.setting_display);
        settingDispalyView.setOnClickListener(this);
        settingNetView = (ImageView) findViewById(R.id.setting_net);
        settingNetView.setOnClickListener(this);
        settingUpdateView = (ImageView) findViewById(R.id.setting_update);
        settingUpdateView.setOnClickListener(this);
        settingMoreView = (ImageView) findViewById(R.id.setting_more);
        settingMoreView.setOnClickListener(this);
        settingErweiView = (ImageView) findViewById(R.id.setting_erwei);
        settingErweiView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == settingBaseView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.GeneralSettingActivity");
        } else if (view.getId() == settingDispalyView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.DisplayConfigActivity");
        } else if (view.getId() == settingNetView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.NetworkConfigActivity");
        } else if (view.getId() == settingUpdateView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.UpdateActivity");
        }
        // else if (view.getId() == settingImView_unfold.getId()){
        else if (view.getId() == settingMoreView.getId()) {
            safeStartApk("com.android.settings", "com.android.settings.Settings");

        } else if (view.getId() == settingErweiView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.QRActivity");
        }
    }

    void safeStartApk(String pkName, String className) {
        try {
            Intent pickIntent = new Intent();
            pickIntent.setClassName(pkName, className);
            startActivity(pickIntent);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

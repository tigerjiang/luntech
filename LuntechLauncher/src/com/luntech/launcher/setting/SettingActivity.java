
package com.luntech.launcher.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.luntech.launcher.R;

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
        settingBaseView = (ImageView) findViewById(R.id.setting_general);
        settingBaseView.setOnClickListener(this);
        settingDispalyView = (ImageView) findViewById(R.id.setting_about);
        settingDispalyView.setOnClickListener(this);
        settingNetView = (ImageView) findViewById(R.id.setting_net);
        settingNetView.setOnClickListener(this);
        settingUpdateView = (ImageView) findViewById(R.id.setting_update);
        settingUpdateView.setOnClickListener(this);
        settingMoreView = (ImageView) findViewById(R.id.setting_more);
        settingMoreView.setOnClickListener(this);
        settingErweiView = (ImageView) findViewById(R.id.setting_qr);
        settingErweiView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_general) {
            safeStartApk("com.luntech.launcher", "com.luntech.launcher.setting.GeneralSettingActivity");
        } else if (view.getId() == R.id.setting_about) {
            safeStartApk("com.luntech.launcher", "com.luntech.launcher.setting.AboutActivity");
        } else if (view.getId() == R.id.setting_net) {
            safeStartApk("com.luntech.launcher", "com.luntech.launcher.setting.NetworkConfigActivity");
        } else if (view.getId() == R.id.setting_update) {
            safeStartApk("com.luntech.launcher", "com.luntech.launcher.setting.UpdateActivity");
        }
        // else if (view.getId() == settingImView_unfold.getId()){
        else if (view.getId() == R.id.setting_more) {
            safeStartApk("com.android.settings", "com.android.settings.Settings");

        } else if (view.getId() == R.id.setting_qr) {
            safeStartApk("com.luntech.launcher", "com.luntech.launcher.setting.QRActivity");
        }
    }

    void safeStartApk(String pkName, String className) {
        try {
            Intent pickIntent = new Intent();
            pickIntent.setClassName(pkName, className);
            startActivity(pickIntent);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }
}

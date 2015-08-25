package com.luntech.launcher;

import android.app.Application;

import com.hisense.network.utils.EpgDataInfoLoader.HiLauncherLoader;

public class LuntechApplication extends Application {
    public HiLauncherLoader mModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mModel = new HiLauncherLoader(this);
    }

    /**
     * There's no guarantee that this function is ever called.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public HiLauncherLoader setLauncher(Q1SLauncher launcher) {
        mModel.initialize(launcher);
        return mModel;
    }

    HiLauncherLoader getModel() {
        return mModel;
    }
}


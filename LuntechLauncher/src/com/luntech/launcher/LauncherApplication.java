package com.luntech.launcher;

import android.app.Application;
import android.content.Context;

import com.hisense.network.utils.EpgDataInfoLoader.HiLauncherLoader;

public class LauncherApplication extends Application {
    public HiLauncherLoader mModel;
    /** the application context */
    private static Context sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mModel = new HiLauncherLoader(this);
        sContext = this;
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
    /**
     * Get Launcher application context.
     *
     * @return the Launcher application context.
     */
    public static Context getAppContext() {
        return sContext;
    }

    HiLauncherLoader getModel() {
        return mModel;
    }
}


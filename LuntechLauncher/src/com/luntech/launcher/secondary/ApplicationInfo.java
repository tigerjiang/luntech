
package com.luntech.launcher.secondary;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
/**
 * Application Data Structure
 * @author ruiniago
 *
 */
public class ApplicationInfo implements Parcelable, Comparable<ApplicationInfo> {

    private static final Collator COLLATOR = Collator.getInstance();
    public static final InstallTimeComparator INSTALL_TIME_COMPARATOR = new InstallTimeComparator();
    /**
     * The application name.
     */
    public CharSequence mTitle;

    /**
     * The main component used to start the application.
     */
    public ComponentName mComponent;

    /**
     * The application icon.
     */
    public Drawable mIcon;

    /**
     * The installation time.
     */
    public long mInstallTime;
    
    public String mpackageName;

    ApplicationInfo() {}

    @SuppressWarnings("deprecation")
    private ApplicationInfo(Parcel in) {
        ClassLoader classLoader = getClass().getClassLoader();
        mIcon = new BitmapDrawable((Bitmap) in.readParcelable(classLoader)); // no context to get resources
        mComponent = in.readParcelable(classLoader);
        mTitle = in.readString();
        mInstallTime = in.readLong();
        mpackageName = in.readString();
    }

    /**
     * Creates the application intent based on a component name and various
     * launch flags.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    /*package*/ void setActivity(ComponentName className) {
        mComponent = className;
    }

    /**
     * Used to start the appropriate application.
     */
    public void startApplication(Context context) {
        try {
            context.startActivity(getIntent());
        } catch (ActivityNotFoundException e) {
            Log.w("AppCenter", "Activity Not Found", e);
        }
    }

    /**
     * Returns intent to start this application
     * @return
     */
    public Intent getIntent() {
        final Intent i = Intent.makeMainActivity(mComponent);
        String packageName = mComponent.getPackageName();
        String activityName = mComponent.getClassName();
        if(("com.skzh.elifetv.MainActivity").equals(activityName)){
            i.putExtra("frag_index", "1");
        }
        Log.d("AppCenter", "packageName is " + packageName);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }

    /**
     * @return {@link Drawable} The application icon.
     */
    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * @return The application name.
     */
    public CharSequence getTitle() {
        return mTitle;
    }

    /**
     * @return The application pkg.
     */
    public String getPackageName() {
        return mpackageName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationInfo)) {
            return false;
        }

        ApplicationInfo that = (ApplicationInfo) o;
        return mTitle.equals(that.mTitle)
                && mComponent.getClassName()
                        .equals(that.mComponent.getClassName());
    }

    @Override
    public int hashCode() {
        int result;
        result = (mTitle != null ? mTitle.hashCode() : 0);
        final String name = mComponent.getClassName();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mInstallTime);
        dest.writeString(mTitle.toString());
        dest.writeString(mpackageName.toString());
        dest.writeParcelable(mComponent, flags);
        dest.writeParcelable((Bitmap)((BitmapDrawable) mIcon).getBitmap(), flags);
    }

    public static final Parcelable.Creator<ApplicationInfo> CREATOR = new Parcelable.Creator<ApplicationInfo>() {
        public ApplicationInfo createFromParcel(Parcel in) {
            return new ApplicationInfo(in);
        }

        public ApplicationInfo[] newArray(int size) {
            return new ApplicationInfo[size];
        }
    };

    @Override
    public int compareTo(ApplicationInfo applicationInfo) {
        return COLLATOR.compare(this.mTitle, applicationInfo.mTitle);
    }

    private static class InstallTimeComparator implements Comparator<ApplicationInfo> {
        @Override
        public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
            // Ascending order, last installed app located at end in list
            return (int) (lhs.mInstallTime - rhs.mInstallTime);
        }
    }

    @Override
    public String toString() {
        return "ApplicationInfo [mTitle=" + mTitle + ", mComponent=" + mComponent + ", mIcon="
                + mIcon + ", mInstallTime=" + mInstallTime + "]";
    }

}

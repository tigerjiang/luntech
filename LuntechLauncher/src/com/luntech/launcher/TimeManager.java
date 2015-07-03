package com.luntech.launcher;

import android.content.*;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.content.res.Resources;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Helper class to work with Date and Time.
 * Allows multiple listeners to be notified if time is changed and
 * once per minute.
 * Also provides methods to format Date and Time with predefined formats.
 *
 * @author zzz
 *
 */
/*
 * Commented lines are prepared for case if date/time format should depends
 * on current System Settings and Locale.
 */
public class TimeManager {
    private static final String TAG = TimeManager.class.getSimpleName();
    private static final boolean DEBUG = false;

    /**
     * Predefined Date and Time formats types
     *
     * @author zzz
     *
     */
    public static enum DateTimeFormat {
        /**
         * Examples: 6:30; 11:20 in 24h format
         */
        TIME_SHORT,

        /**
         * Examples: 6:30; 11:20 in AP/PM format without AP/PM sign
         */
        TIME_SHORT_12,

        /**
         * Examples: 6:30 AM; 11:20 PM
         */
        TIME_AM_PM,

        /**
         * Examples: AM; PM
         */
        AM_PM,

        /**
         * Examples: 01/15/12; 02/31/98
         */
        DATE_SHORT_FULL,

        /**
         * Examples: Aug 9; May 15
         */
        DATE_MONTH_DAY,

        /**
         * Examples: Aug 2012; May 1999
         */
        DATE_MONTH_YEAR,

        /**
         * Examples: Aug 9, 2012; May 15, 1999
         */
        DATE_LONG,

        /**
         * Examples: 2012; 1999
         */
        YEAR,

        /**
         * Examples: Wednesday
         */
        DAY_OF_WEEK,

        /**
         * Examples : Wed 17 Sep
         */
        DAY_OF_WEEK_MONTH_DAY,
        // Special Formats

        /**
         * Time format based on current user settings for 24 hour format settings
         */
        TIME_CURRENT_FORMAT,

        /**
         * Current date format selected by user in Settings
         */
        DATE_CURRENT_FORMAT,

        /**
         * Same as DATE_LONG but with special case for Today and Yesterday.
         * <br><b>getInstance should be called at least once BEFORE using this format<b/>
         */
        DATE_LONG_FROM_TODAY,

      /**
        * For example: "01:32:14".
        */
        DURATION
    }

    private static final int[] DATE_TIME_FORMAT_PATTERN_RES = new int[] {
            R.string.date_time_format_time_short,
            R.string.date_time_format_time_short_12,
            R.string.date_time_format_time_am_pm,
            R.string.date_time_format_am_pm,
            R.string.date_time_format_date_short_full,
            R.string.date_time_format_month_day,
            R.string.date_time_format_month_year,
            R.string.date_time_format_date_long,
            R.string.date_time_format_year,
            R.string.date_time_format_day_of_week,
            R.string.date_time_format_day_of_week_month_day
    };

    private static final String[] DATE_TIME_FORMAT_PATTERNS = new String[DATE_TIME_FORMAT_PATTERN_RES.length];

    private static final String TIME_RANGE_DELIM = " - ";

    private static final char DATE_DELIM = '/';

    private static final String WEEKDAY_DELIM = " ";

    private static final String TIME_FORMAT_PREFIX = "%d ";

    private String mTimeFormatHrs;

    private String mTimeFormatMins;

    private String mTimeFormatHr;

    private String mTimeFormatMin;

    private String mTimeFormatSec;

    private String mTimeFormatDuration;

    private static final int MIN_IN_SECONDS = 60;

    private static final Format[] DATE_TIME_FORMATS = new Format[DATE_TIME_FORMAT_PATTERN_RES.length];

    private WeakReference<Context> mContextRef;

    private long mLastKnownTime;

    private String sYesterdayString;

    private String sTodayString;

    private Object mCalendarSync = new Object();

    private Calendar mCalendarNow;

    private Calendar mCalendarTmp;

    private Format mCurrentDateFormat;

    private Format mCurrentTimeFormat;

    private Handler mNonUiHandler;

    private Handler mUiHandler;

    private boolean mIs24;

    private boolean mNeedInit = true;

    private final CopyOnWriteArrayList<OnTimeChangedListener> mOnTimeChangedListeners
        = new CopyOnWriteArrayList<OnTimeChangedListener>();

    private final CopyOnWriteArrayList<OnFormatChangedListener> mOnFormatChangedListeners
            = new CopyOnWriteArrayList<OnFormatChangedListener>();

    /**
     * Interface definition for a callback to be invoked when a time
     * changed and once in a minute.
     *
     * @author zzz
     *
     */
    public interface OnTimeChangedListener {
        /**
         * Called when a time changed and once in a minute.
         * Will be called in UI thread.
         * @param time current time in msecs
         */
        void onTimeChanged(long time);
    }

    private final BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mUiHandler.removeMessages(UiHandler.MSG_PROCESS_TIME_INTENT);
            mUiHandler.obtainMessage(UiHandler.MSG_PROCESS_TIME_INTENT).sendToTarget();
        }
    };

    /**
     * Interface definition for a callback to be invoked when a time
     * or date format changed.
     */
    public interface OnFormatChangedListener {
        /**
         * Called when a time or date format changed.
         * Will be called in UI thread.
         */
        void onFormatChanged();
    }

    private final BroadcastReceiver mFormatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processFormatIntent();
        }
    };

    private ContentObserver mTimeFormatObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG) {
                Log.d(TAG, "mTimeFormatObserver");
            }
            mUiHandler.sendEmptyMessage(UiHandler.MSG_PROCESS_TIME_FORMAT_CHANGE);
        }
    };

    private ContentObserver mDateFormatObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG) {
                Log.d(TAG, "mDateFormatObserver");
            }
            mUiHandler.sendEmptyMessage(UiHandler.MSG_PROCESS_DATE_FORMAT_CHANGE);
        }
    };

    // private static holder uses lazy instantiation and removes
    // dependency on synchronization
    private static class SingletonHolder {
        private static final TimeManager INSTANCE = new TimeManager();
    }

    private boolean needInit() {
        return mNeedInit || mContextRef == null;
    }

    /**
     * Creates if needed and returns instance of TimeManager.
     * We must make sure:
     * 1. init() method has been called before we return the INSTANCE;
     * 2. init() method should only be called once as long as mContextRef is valid;
     * 3. If mContexRef is GCed, we shall call init() again.
     * @param context Context for TimeManager
     * @return instance of TimeManager
     */
    public static TimeManager getInstance(Context context) {
        if (SingletonHolder.INSTANCE.needInit()) {
            synchronized (SingletonHolder.INSTANCE) {
                if (SingletonHolder.INSTANCE.needInit()) {
                    SingletonHolder.INSTANCE.init(context);
                }
            }
        }
        return SingletonHolder.INSTANCE;
    }

    private void init(Context context) {
        Context app = context.getApplicationContext();
        if (app != null) {
            context = app;
        }
        mContextRef = new WeakReference<Context>(context);
        initFormatStrings();
        registerFormatReceiver();
        registerSettingsObservers();

        new FormatChangedAsyncTask(this, true, true).execute();
        mNeedInit = false;
    }

    private TimeManager() {
        mUiHandler = new UiHandler(this);
    }

    private void initFormatStrings() {
        if (getContext() != null) {
            Resources res = getContext().getResources();
            sYesterdayString = res.getString(R.string.date_time_yesterday);
            sTodayString = res.getString(R.string.date_time_today);
            mTimeFormatHrs = TIME_FORMAT_PREFIX + res.getString(R.string.date_time_hrs);
            mTimeFormatMins = TIME_FORMAT_PREFIX + res.getString(R.string.date_time_mins);
            mTimeFormatHr = TIME_FORMAT_PREFIX + res.getString(R.string.date_time_hr);
            mTimeFormatMin = TIME_FORMAT_PREFIX + res.getString(R.string.date_time_min);
            mTimeFormatSec = TIME_FORMAT_PREFIX + res.getString(R.string.date_time_sec);
            mTimeFormatDuration = res.getString(R.string.date_time_format_duration);
            for (int i = 0; i < DATE_TIME_FORMAT_PATTERN_RES.length; i++) {
                int patternRes = DATE_TIME_FORMAT_PATTERN_RES[i];
                DATE_TIME_FORMAT_PATTERNS[i] = res.getString(patternRes);
            }
        }
    }

    /**
     * Destroys the TimeManager.
     * Non-static methods cannot be used for this instance
     * after call this method.
     */
    public synchronized void destroy() {
        mNonUiHandler.getLooper().quit();
        if (mContextRef != null) {
            if (!mOnTimeChangedListeners.isEmpty()) {
                mOnTimeChangedListeners.clear();
                unregisterTimeReceiver();
            }
            mOnFormatChangedListeners.clear();
            unregisterFormatReceiver();
            unregisterSettingsObservers();
            mContextRef.clear();
            mContextRef = null;
            onLowMemory();
        }
    }

    /**
     * Can be called while application is in Low Memory conditions
     * to cleanup cache.
     */
    public void onLowMemory() {
        clearDateTimeFormats();
        synchronized (mCalendarSync) {
            mCalendarNow = null;
            mCalendarTmp = null;
        }
    }

    /**
     * Clears cached formats.
     * Can be called at any time to be sure new Locale is used for formats.
     * Called automatically on Locale change detected.
     */
    private void clearDateTimeFormats() {
        synchronized (DATE_TIME_FORMATS) {
            for (int i = 0; i < DATE_TIME_FORMATS.length; i++) {
                DATE_TIME_FORMATS[i] = null;
            }
        }
    }

    /**
     * Adds OnTimeChangedListener
     * @param listener OnTimeChangedListener
     */
    public synchronized void addOnFormatChangedListener(OnFormatChangedListener listener) {
        checkDestroyed();
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        mOnFormatChangedListeners.addIfAbsent(listener);
    }

    /**
     * Removes OnTimeChangedListener
     * @param listener OnTimeChangedListener
     */
    public synchronized void removeOnFormatChangedListener(OnFormatChangedListener listener) {
        checkDestroyed();
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        mOnFormatChangedListeners.remove(listener);
    }

    /**
     * Adds OnTimeChangedListener
     * @param listener OnTimeChangedListener
     */
    public synchronized void addOnTimeChangedListener(OnTimeChangedListener listener) {
        checkDestroyed();
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        if (mOnTimeChangedListeners.isEmpty()) {
            registerTimeReceiver();
        }
        mOnTimeChangedListeners.addIfAbsent(listener);

        listener.onTimeChanged(mLastKnownTime);
    }

    /**
     * Removes OnTimeChangedListener
     * @param listener OnTimeChangedListener
     */
    public synchronized void removeOnTimeChangedListener(OnTimeChangedListener listener) {
        checkDestroyed();
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        mOnTimeChangedListeners.remove(listener);
        if (mOnTimeChangedListeners.isEmpty()) {
            unregisterTimeReceiver();
        }
    }

    /**
     * Returns last known time from latest update. At least one listener
     * should be added to be able to get this time.
     * Will return the same time between time ticks (once per minute).
     * @return last known time
     */
    public synchronized long getLastKnownTime() {
        if (mLastKnownTime == 0) {
            throw new IllegalStateException("No listeners registered");
        }
        return mLastKnownTime;
    }

    /**
     * Formats time in msecs with one of predefined formats using the
     * default locale for the calling process. The format is cached for
     * future use and will be re-used until cache
     * is cleared via a call to {@link #onLowMemory()}.
     * @param time time in msecs
     * @param formatType predefined format type
     * @return formatted time as String
     */
    public String formatDateTime(long time, DateTimeFormat formatType) {
        int index = formatType.ordinal();
        if (index >= DATE_TIME_FORMATS.length) {
            return processSpecialFormat(time, formatType);
        }

        return initFormat(index).format(time);
    }

    private Format initFormat(int index) {
        Format format;
        synchronized (DATE_TIME_FORMATS) {
            format = DATE_TIME_FORMATS[index];
            if (format == null) {
                if (DATE_TIME_FORMAT_PATTERNS[index] == null) {
                    initFormatStrings();
                }
                format = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERNS[index]);
                DATE_TIME_FORMATS[index] = format;
            }
        }

        return format;
    }

    /**
     * Formats the time interval using the default locale for the calling
     * process. The format is cached for future use and will be re-used
     * until cache is cleared via a call to {@link #onLowMemory()}.
     * @param start start time
     * @param end end time
     * @return formatted time interval as String
     */
    public String formatTimeRange(long start, long end) {
        return formatTimeRange(start, end, false);
    }

    /**
     * Formats the time interval using the default locale for the calling
     * process. The format is cached for future use and will be re-used until
     * cache is cleared via a call to {@link #onLowMemory()}.
     *
     * @param start start time in milliseconds
     * @param end end time in milliseconds
     * @param showWeekday indicates whether to show or not the weekday together
     *            with time range. If true, the weekday will be displayed if
     *            start time is not today.
     * @return formatted time interval as String
     */
    public String formatTimeRange(long start, long end, boolean showWeekday) {
        StringBuilder sb = new StringBuilder();

        if (showWeekday) {
            synchronized (mCalendarSync) {
                initCalendars(start);

                int diff = mCalendarNow.get(Calendar.DATE) - mCalendarTmp.get(Calendar.DATE);
                if (diff != 0) {
                    sb.append(formatDateTime(start,DateTimeFormat.DAY_OF_WEEK));
                    sb.append(WEEKDAY_DELIM);
                }
            }
        }

        sb.append(formatDateTime(start, mIs24 ? DateTimeFormat.TIME_SHORT : DateTimeFormat.TIME_SHORT_12));
        sb.append(TIME_RANGE_DELIM);
        sb.append(formatDateTime(end, mIs24 ? DateTimeFormat.TIME_SHORT : DateTimeFormat.TIME_AM_PM));

        return sb.toString();
    }

    /**
     * Converts duration in milliseconds to text String.
     * For example: "1 hour 45 mins" or "39 sec".
     *
     * @param duration duration in milliseconds
     * @return duration as formatted String
     */
    public String formatDuration(long duration) {
        long hours = duration / DateUtils.HOUR_IN_MILLIS;

        long minutes = duration / DateUtils.MINUTE_IN_MILLIS;
        long sec = duration / DateUtils.SECOND_IN_MILLIS;

        if (hours > 0 || minutes > 0) {
            String h = String.format(hours == 1 ? mTimeFormatHr : mTimeFormatHrs, hours);
            String m = String.format(minutes == 1 ? mTimeFormatMin : mTimeFormatMins, minutes % MIN_IN_SECONDS);
            String s = String.format(mTimeFormatSec, sec % MIN_IN_SECONDS);

            return hours > 0 ? h + " " + m : m + " " + s;
        }

        // Seconds
        return String.format(mTimeFormatSec, sec);
    }

    /**
     * Converts duration in milliseconds to time format string.
     * For example: "1:45:12".
     *
     * @param duration duration in milliseconds
     * @return duration as formatted String
     */
    public String formatDurationInTimeFormat(long duration) {
        if (mTimeFormatDuration == null) {
            initFormatStrings();
        }

        long hours = duration / DateUtils.HOUR_IN_MILLIS;
        SimpleDateFormat format = new SimpleDateFormat(mTimeFormatDuration);
        String result =  format.format(duration);

        return hours > 0 ? hours + ":" + result : result;
    }

    private String processSpecialFormat(long time, DateTimeFormat formatType) {
        switch (formatType) {
            case DATE_LONG_FROM_TODAY:
                return formatDateFromToday(time);
            case DURATION:
                return formatDurationInTimeFormat(time);
            case DATE_CURRENT_FORMAT:
                return formatDateInCurrentFormat(time);
            case TIME_CURRENT_FORMAT:
                return formatTimeInCurrentFormat(time);        }
        throw new IllegalArgumentException("Unknown special format: " + formatType);
    }

    private String formatTimeInCurrentFormat(long time) {
        if (mCurrentTimeFormat == null) {
            // Use as default - can happen only if async init is not done yet
            mCurrentTimeFormat = initFormat(DateTimeFormat.TIME_SHORT.ordinal());
        }

        return mCurrentTimeFormat.format(time);
    }

    private String formatDateInCurrentFormat(long time) {
        if (mCurrentDateFormat == null) {
            // Use as default - can happen only if async init is not done yet
            mCurrentDateFormat = initFormat(DateTimeFormat.DATE_SHORT_FULL.ordinal());
        }

        return mCurrentDateFormat.format(time);
    }

    private String formatDateFromToday(long time) {
        if (sTodayString == null) {
            initFormatStrings();
        }
        synchronized (mCalendarSync) {
            initCalendars(time);

            if (isSameDay(mCalendarNow, mCalendarTmp)) {
                return sTodayString;
            } else {
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);

                if (isSameDay(yesterday, mCalendarTmp)) {
                    return sYesterdayString;
                } else {
                    return formatDateTime(time, DateTimeFormat.DATE_LONG);
                }
            }
        }
    }

    private static boolean isSameDay(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
    }

    private void initCalendars(long time) {
        if (mCalendarNow == null) {
            mCalendarNow = Calendar.getInstance();
        } else {
            mCalendarNow.setTimeInMillis(System.currentTimeMillis());
        }

        if (mCalendarTmp == null) {
            mCalendarTmp = Calendar.getInstance();
        }

        mCalendarTmp.setTimeInMillis(time);
    }

    private void checkDestroyed() {
        if (getContext() == null) {
            throw new IllegalStateException("Already destroyed");
        }
    }

    private Context getContext() {
        WeakReference<Context> ref = mContextRef;
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    private void registerTimeReceiver() {
        Context context = getContext();
        mLastKnownTime = System.currentTimeMillis();
        if (context != null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIME_TICK);
            // Time in msec is NOT changed in this case but need to notify
            // listeners to update time on UI
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            context.registerReceiver(mTimeReceiver, filter);
        }
    }

    private void unregisterTimeReceiver() {
        mLastKnownTime = 0;
        Context context = getContext();
        if (context != null) {
            context.unregisterReceiver(mTimeReceiver);
        }
    }

    private void processTimeIntent() {
        mLastKnownTime = System.currentTimeMillis();
        for (OnTimeChangedListener listener: mOnTimeChangedListeners) {
            listener.onTimeChanged(mLastKnownTime);
        }
    }

    private void registerFormatReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mFormatReceiver, filter);
    }

    private void unregisterFormatReceiver() {
        getContext().unregisterReceiver(mFormatReceiver);
    }

    private void processFormatIntent() {
        if (DEBUG) {
            Log.d(TAG, "processFormatIntent");
        }
        clearDateTimeFormats();
        initFormatStrings();
        synchronized (mCalendarSync) {
            mCalendarNow = null;
            mCalendarTmp = null;
        }
        new FormatChangedAsyncTask(this, true, true).execute();
    }

    private void notifyFormatChanged() {
        for (OnFormatChangedListener listener: mOnFormatChangedListeners) {
            listener.onFormatChanged();
        }
    }

    private void registerSettingsObservers() {
        Context context = getContext();
        if (context != null) {
            ContentResolver resolver = context.getContentResolver();
            Uri timeFormatUri = Settings.System.getUriFor(Settings.System.TIME_12_24);
            resolver.registerContentObserver(timeFormatUri, true, mTimeFormatObserver);

            Uri dateFormatUri = Settings.System.getUriFor(Settings.System.DATE_FORMAT);
            resolver.registerContentObserver(dateFormatUri, true, mDateFormatObserver);
        }
    }

    private void unregisterSettingsObservers() {
        Context context = getContext();
        if (context != null) {
            ContentResolver resolver = context.getContentResolver();
            resolver.unregisterContentObserver(mTimeFormatObserver);
            resolver.unregisterContentObserver(mDateFormatObserver);
        }
    }

    private static class FormatChangedAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<TimeManager> mTimeManagerRef;
        private final boolean mDateFormatChanged;
        private final boolean mTimeFormatChanged;
        private Format mDateFormat;
        private Format mTimeFormat;

        public FormatChangedAsyncTask(TimeManager tm, boolean dateFormatChanged, boolean timeFormatChanged) {
            mTimeManagerRef = new WeakReference<TimeManager>(tm);
            mDateFormatChanged = dateFormatChanged;
            mTimeFormatChanged = timeFormatChanged;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final TimeManager tm = mTimeManagerRef.get();
            if (tm == null) {
                Log.w(TAG, "FormatChangedAsyncTask::doInBackground no TimeManager reference");
                return null;
            }
            final Context context = tm.getContext();
            if (context == null) {
                Log.w(TAG, "FormatChangedAsyncTask::doInBackground no context");
                return null;
            }

            if (mDateFormatChanged) {
                final ContentResolver resolver = context.getContentResolver();
                String dateFormat = Settings.System.getString(
                        resolver, Settings.System.DATE_FORMAT);
                if (dateFormat != null) {
                    dateFormat = dateFormat.replace('-', DATE_DELIM);
                    mDateFormat = new SimpleDateFormat(dateFormat);
                } else {
                    mDateFormat = tm.initFormat(DateTimeFormat.DATE_SHORT_FULL.ordinal());
                }
                if (DEBUG) {
                    Log.d(TAG, "dateFormat: " + mDateFormat);
                }
            }

            if (mTimeFormatChanged) {
                tm.mIs24 = DateFormat.is24HourFormat(context);
                if (DEBUG) {
                    Log.d(TAG, "is24: " + tm.mIs24);
                }
                final DateTimeFormat formatType = tm.mIs24 ? DateTimeFormat.TIME_SHORT
                        : DateTimeFormat.TIME_AM_PM;
                mTimeFormat = tm.initFormat(formatType.ordinal());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final TimeManager tm = mTimeManagerRef.get();
            if (tm == null) {
                Log.w(TAG, "FormatChangedAsyncTask::onPostExecute no TimeManager reference");
                return;
            }

            if (mDateFormatChanged) {
                tm.mCurrentDateFormat = mDateFormat;
            }
            if (mTimeFormatChanged) {
                tm.mCurrentTimeFormat = mTimeFormat;
            }
            if (mDateFormatChanged || mTimeFormatChanged) {
                tm.notifyFormatChanged();
            }
        }
    }

    private static class UiHandler extends Handler {
        static final int MSG_PROCESS_DATE_FORMAT_CHANGE = 1;
        static final int MSG_PROCESS_TIME_FORMAT_CHANGE = 2;
        static final int MSG_PROCESS_TIME_INTENT = 3;

        private final WeakReference<TimeManager> mTimeManagerRef;

        UiHandler(TimeManager tm) {
            super(Looper.getMainLooper());
            mTimeManagerRef = new WeakReference<TimeManager>(tm);
        }

        @Override
        public void handleMessage(Message msg) {
            final TimeManager tm = mTimeManagerRef.get();
            if (tm == null) {
                super.handleMessage(msg);
                return;
            }
            switch (msg.what) {
                case MSG_PROCESS_DATE_FORMAT_CHANGE:
                    new FormatChangedAsyncTask(tm, true, false).execute();
                    break;
                case MSG_PROCESS_TIME_FORMAT_CHANGE:
                    new FormatChangedAsyncTask(tm, true, false).execute();
                    break;
                case MSG_PROCESS_TIME_INTENT:
                    tm.processTimeIntent();
                    break;
            }
        }
    }
}

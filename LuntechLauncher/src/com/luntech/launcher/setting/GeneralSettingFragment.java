package com.luntech.launcher.setting;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.IPTVLauncher;
import com.luntech.launcher.Launcher;
import com.luntech.launcher.Module;
import com.luntech.launcher.Q1SLauncher;
import com.luntech.launcher.R;
import com.luntech.launcher.ToolUtils;
import com.luntech.launcher.db.DBDao;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralSettingFragment extends Fragment {

    private ListView mTvGeneralListView;
    private List<String> mItems;
    private Context mContext;
    private List<String> mTimes;
    private List<String> mThemes;


    public static GeneralSettingFragment newInstance() {
        GeneralSettingFragment generalFragment = new GeneralSettingFragment();
        return generalFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_general_layout, null);
        initView(rootView);
        return rootView;
    }


    private void initView(View view) {
        /** first, we must get the Service. **/

        initItems();
        mContext = getActivity();
        AboutTVGenaralAdapter adapter = new AboutTVGenaralAdapter(mContext, mItems);

        mTvGeneralListView = (ListView) view.findViewById(R.id.about_general_list);
        mTvGeneralListView.setAdapter(adapter);
        mTvGeneralListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String time = ToolUtils.getCommonValueFromSP(mContext, "saver_time");
                int index = mTimes.indexOf(time);

                if (position == 0) {
                    new AlertDialog.Builder(mContext).setSingleChoiceItems(R.array.screensaver_array, index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Launcher.sCloseScreenSaver = false;
                                    if (which == 0) {
                                        Launcher.sCloseScreenSaver = true;
                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(0));
                                    } else if (which == 1) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(1));
                                    } else if (which == 2) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(2));
                                    } else if (which == 3) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(3));
                                    }
                                    Launcher.showScreenSaverTime = (which) * 5 * 60 * 1000;
                                    dialog.dismiss();
                                }
                            }
                    ).create().show();
                } else if (position == 1) {
                    Toast.makeText(getActivity(), R.string.no_permission, Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    String selectAlert = getString(R.string.select_alert);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_CHOOSER);
                    intent2.putExtra(Intent.EXTRA_TITLE, selectAlert);
                    intent2.putExtra(Intent.EXTRA_INTENT, intent);
                    startActivity(intent2);
                } else if (position == 3) {
                    String theme = ToolUtils.getCommonValueFromSP(mContext, "theme");
                    if (TextUtils.isEmpty(theme)) {
                        ToolUtils.storeCommonValueIntoSP(mContext, "theme", "IPTV");
                    }
                    final int i = mThemes.indexOf(theme);
                    final Intent themeIntent = new Intent();
                    new AlertDialog.Builder(mContext).setSingleChoiceItems(R.array.theme_array, i, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (i == which) {
                                        Toast.makeText(mContext, R.string.current_theme, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        return;
                                    }

                                    if (which == 0) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "theme", mThemes.get(0));
                                        themeIntent.setClass(mContext, IPTVLauncher.class);
                                    } else if (which == 1) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "theme", mThemes.get(1));
                                        themeIntent.setClass(mContext, Q1SLauncher.class);
                                    }
                                    getActivity().finish();
                                    themeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(themeIntent);
                                    dialog.dismiss();
                                }
                            }
                    ).create().show();

                } else if (position == 4) {
                    Intent intent = new Intent(mContext, SetCity.class);
                    startActivity(intent);
                } else if (position == 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(R.string.reboot_alert);
                    builder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    clearCustomCache();
                                    getActivity().finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    arg0.dismiss();
                                }
                            });
                    builder.setNegativeButton(R.string.cancel, null);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();

                }
            }

        });
    }

    private void clearCustomCache() {
        DBDao dao = new DBDao(mContext);
        ArrayList<Module> modules = dao.fetchAllModulesByGroup(Launcher.getCustomType());
        ToolUtils tools = ToolUtils.getInstance(mContext);
        for (Module m : modules) {
            if (tools.isExsitsKey(mContext, m.getModuleCode())) {
                String key = tools.getConfigured(mContext, m.getModuleCode());
                tools.clearConfiguredPkg(mContext, m.getModuleCode());
                tools.clearConfiguredPkg(mContext, key);
            }
        }

    }

    private void initItems() {
        String[] array = getResources().getStringArray(R.array.general_item);
        mItems = new ArrayList<String>();
        mItems = Arrays.asList(array);

        String[] arrayTime = getResources().getStringArray(R.array.screensaver_array);
        mTimes = new ArrayList<String>();
        mTimes = Arrays.asList(arrayTime);
        String[] arrayTheme = getResources().getStringArray(R.array.theme_array);
        mThemes = new ArrayList<String>();
        mThemes = Arrays.asList(arrayTheme);
    }

    class AboutTVGenaralAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private List<String> mItems;

        public AboutTVGenaralAdapter(Context context, List<String> items) {
            mInflater = LayoutInflater.from(context);
            mItems = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.general_item_layout, parent,
                        false);
            }
            TextView title = (TextView) convertView
                    .findViewById(R.id.chooser_title);
            String currentItem = mItems.get(position);
            title.setText(currentItem);
            return convertView;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int id) {
            return mItems.get(id);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

    }
}

package com.luntech.launcher.setting;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.Launcher;
import com.luntech.launcher.R;
import com.luntech.launcher.ToolUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralSettingFragment extends Fragment {

    private ListView mTvGeneralListView;
    private List<String> mItems;
    private Context mContext;
    private List<String> mTimes;

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
                String time = ToolUtils.getValueFromSP(mContext, "saver_time");
                int index = mTimes.indexOf(time);

                if (position == 0) {
                    new AlertDialog.Builder(mContext).setSingleChoiceItems(R.array.screensaver_array, index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {

                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(0));
                                    } else if (which == 1) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(1));
                                    } else if (which == 2) {
                                        ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", mTimes.get(2));
                                    }
                                    Launcher.showScreenSaverTime = (which + 1) * 5 * 60 * 1000;
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
                    Intent intent = new Intent();
                    intent.setClass(mContext, SetCity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

        });
    }

    private void initItems() {
        String[] array = getResources().getStringArray(R.array.general_item);
        mItems = new ArrayList<String>();
        mItems = Arrays.asList(array);

        String[] arrayTime = getResources().getStringArray(R.array.screensaver_array);
        mTimes = new ArrayList<String>();
        mTimes = Arrays.asList(arrayTime);
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

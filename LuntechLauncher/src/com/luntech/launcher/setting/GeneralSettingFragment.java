package com.luntech.launcher.setting;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
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

import com.luntech.launcher.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class GeneralSettingFragment extends Fragment {

    private ListView mTvGeneralListView;
    private List<String> mItems;
    

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

        AboutTVGenaralAdapter adapter = new AboutTVGenaralAdapter(getActivity()
                .getApplicationContext(), mItems);

        mTvGeneralListView = (ListView) view.findViewById(R.id.about_general_list);
        mTvGeneralListView.setAdapter(adapter);
        mTvGeneralListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if(position ==0){
                  Toast.makeText(getActivity(), R.string.no_feature, Toast.LENGTH_SHORT).show();
              }else if(position ==1){
                  Toast.makeText(getActivity(), R.string.no_permission, Toast.LENGTH_SHORT).show();
              }else if(position ==2){
                  String selectAlert = getString(R.string.select_alert);
                  Intent intent = new Intent();  
                  intent.setAction(Intent.ACTION_MAIN); 
                  intent.addCategory(Intent.CATEGORY_HOME);
                  Intent intent2 = new Intent();  
                  intent2.setAction(Intent.ACTION_CHOOSER);  
                  intent2.putExtra(Intent.EXTRA_TITLE, selectAlert);  
                  intent2.putExtra(Intent.EXTRA_INTENT, intent);  
                  startActivity(intent2);  
              }
            }
            
        });
    }

    private void initItems(){
        String[] array = getResources().getStringArray(R.array.general_item);
        mItems = new ArrayList<String>();
        mItems = Arrays.asList(array);
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

package com.luntech.launcher.setting;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.luntech.launcher.R;

import java.util.ArrayList;
import java.util.List;

public class SetCity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provincelist);
        lv = (ListView) findViewById(R.id.listview_province);
        initView();
    }

    ListView lv;
    Context mcontext;

    static final String sCity = "city";
    String citys[][];
    int cityCount = 0;
    String provinces[][];
    int provinceCount = 0;
    MyDatabase myDB;
    public static String sCityName;

    private void initView() {
        mcontext = SetCity.this;
        myDB = new MyDatabase(mcontext);
        Cursor cProvinces = myDB.getProvinces();
        provinceCount = cProvinces.getCount();
        provinces = new String[provinceCount][2];

        for (int j = 0; j < provinceCount; j++) {
            provinces[j][0] = cProvinces.getString(0);
            provinces[j][1] = cProvinces.getString(1);
            cProvinces.moveToNext();
        }

        lv.setAdapter(new ArrayAdapter<String>(mcontext,
                android.R.layout.simple_expandable_list_item_1, getProvincesData()));
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // Toast.makeText(mcontext,p[position], 1).show();
                sCityName = provinces[position][1];
                showCitys(provinces[position][0]);
            }
        });
    }

    private void showCitys(String provinceId) {
        Cursor cCity = myDB.getCities(provinceId);
        cityCount = cCity.getCount();
        if (cityCount == 0) {
            Settings.System.putString(mcontext.getContentResolver(),
                    sCity, sCityName);
            onBackPressed();
            return;

        }
        citys = new String[cityCount][2];

        for (int j = 0; j < cityCount; j++) {
            citys[j][0] = cCity.getString(0);
            citys[j][1] = cCity.getString(1);
            cCity.moveToNext();
        }

        lv.setAdapter(new ArrayAdapter<String>(mcontext, android.R.layout.simple_expandable_list_item_1, getCityData()));
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                sCityName = citys[position][1];
                Settings.System.putString(mcontext.getContentResolver(),
                        sCity, sCityName);
                onBackPressed();
            }
        });

    }

    public List<String> getProvincesData() {

        List<String> ls = new ArrayList<String>();
        ls = asProvincesList(provinces);
        return ls;
    }

    public List<String> asProvincesList(String s[][]) {
        List<String> l = new ArrayList<String>();
        for (int i = 0; i < provinceCount; i++) {
            if (s[i][1] != null)
                l.add(s[i][1]);
        }
        return l;
    }

    public List<String> getCityData() {

        List<String> ls = new ArrayList<String>();
        ls = asCityList(citys);
        return ls;
    }

    public List<String> asCityList(String s[][]) {
        List<String> l = new ArrayList<String>();
        for (int i = 0; i < cityCount; i++) {
            if (s[i][1] != null)
                l.add(s[i][1]);
        }
        return l;
    }
}

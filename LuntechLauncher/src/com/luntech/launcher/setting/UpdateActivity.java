package com.luntech.launcher.setting;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.luntech.launcher.R;

public class UpdateActivity extends Activity {
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.update_layout);
	        Fragment currFragment = UpdateFragment.newInstance();
	        FragmentTransaction ft = getFragmentManager().beginTransaction();
	        ft.replace(R.id.fragment_container, currFragment);
	        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
	        ft.commit();
	    }
}

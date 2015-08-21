package com.luntech.launcher.setting;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;

public class UpdateFragment extends Fragment implements OnClickListener {
	TextView mProductModelView, mMacView, mVsrsionView;
	Button mLocalUpdateBtn, mNetworkUpdateBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.update_fragment_layout, null);
		initView(mView);
		return mView;
	}

	public static UpdateFragment newInstance() {
		UpdateFragment updateFragment = new UpdateFragment();
		return updateFragment;
	}

	private void initView(View view) {
		mProductModelView = (TextView) view
				.findViewById(R.id.product_version_value);
		mProductModelView.setText(Build.MODEL);
		mVsrsionView = (TextView) view.findViewById(R.id.soft_version_value);
		mMacView = (TextView) view.findViewById(R.id.mac_value);
		mVsrsionView.setText(SystemProperties.get("ro.product.version"));
		String ethernetMacAddress = Utilities.getMacAddress();
		if (ethernetMacAddress != null) {
			mMacView.setText(ethernetMacAddress.toUpperCase().trim());
		} else {
			mMacView.setText(R.string.settings_system_about_unavailiable);
		}

		mLocalUpdateBtn = (Button) view.findViewById(R.id.local_update);
		mLocalUpdateBtn.setOnClickListener(this);
		mNetworkUpdateBtn = (Button) view.findViewById(R.id.net_update);
		mNetworkUpdateBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.local_update) {
			Intent localUsbUpdateIntent = new Intent(
					"com.android.intent.local_update");
			this.getActivity().sendBroadcast(localUsbUpdateIntent);
		} else if (view.getId() == R.id.net_update) {
			Intent manualNetUpdateIntent = new Intent(
					"android.intent.action.TM_DOWNLOAD");
			manualNetUpdateIntent.putExtra("URL",
					"http://192.168.1.100/update.zip");
			this.getActivity().sendBroadcast(manualNetUpdateIntent);

		}

	}

}

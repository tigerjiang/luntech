package com.luntech.launcher.setting;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.os.SystemProperties;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.HttpUtils;
import com.luntech.launcher.Launcher;
import com.luntech.launcher.Logger;
import com.luntech.launcher.OtaInfo;
import com.luntech.launcher.R;
import com.luntech.launcher.ToolUtils;

import java.io.ByteArrayInputStream;


public class UpdateFragment extends Fragment {
    TextView mProductModelView, mMacView, mVsrsionView;
    Button mLocalUpdateBtn, mNetworkUpdateBtn;
    private Context mContext;


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
        mContext = this.getActivity();
        mProductModelView = (TextView) view
                .findViewById(R.id.product_version_value);
        String version = getString(R.string.app_version);
        mProductModelView.setText(version);
        mVsrsionView = (TextView) view.findViewById(R.id.soft_version_value);
        mMacView = (TextView) view.findViewById(R.id.mac_value);
        mVsrsionView.setText(version + "_" + Launcher.sVersionCode);
        String ethernetMacAddress = Utilities.getMacAddress();
        if (ethernetMacAddress != null) {
            mMacView.setText(ethernetMacAddress.toUpperCase().trim());
        } else {
            mMacView.setText(R.string.settings_system_about_unavailiable);
        }

        mLocalUpdateBtn = (Button) view.findViewById(R.id.net_update);
        mLocalUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String httpArg = "&package_name="+Launcher.sPackageName  + "&version="+Launcher.sVersionCode;
                final String update_url = HttpUtils.HTTP_UPDATE_APP_URL + httpArg;
                Logger.e("request url " + update_url);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new FetchTask(update_url, Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mUpdateConfigureFile,
                                1).execute();

                    }
                });
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String result = (String) msg.obj;
                final OtaInfo ota = ToolUtils.parseUpdateInfo(mContext,
                        new ByteArrayInputStream(result.getBytes()));
                if (ota != null
                        && !TextUtils.isEmpty(ota.currentVersion)
                        && ota.currentVersion.equals(String.valueOf(Launcher.sVersionCode))) {
                    if (Integer.parseInt(ota.currentVersion) < Integer.parseInt(ota.newVersion)) {
                        Log.d("update", "find new version for update " + ota.newVersion);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.update);
                        builder.setMessage(ota.remark);
                        builder.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        ((UpdateActivity)getActivity()).downloadOta(mContext,ota);
                                        arg0.dismiss();
                                    }
                                });
                        builder.setNegativeButton(R.string.cancel, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(UpdateFragment.this.getActivity(), getString(R.string.no_update), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    class FetchTask extends AsyncTask<Void, Integer, String> {

        private String mUrl;
        private String mFileName;
        private int mReturnCode;

        public FetchTask(String mUrl, String mFileName, int returnCode) {
            super();
            this.mUrl = mUrl;
            this.mFileName = mFileName;
            this.mReturnCode = returnCode;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result)) {
                Logger.e("Doesn't found any info from server");
                return;
            } else {
                Logger.d("result = " + result);
                Message msg = mHandler.obtainMessage(mReturnCode);
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = HttpUtils.requestAndWriteResourcesFromServer(mUrl, mFileName);
            return result;
        }
    }


}


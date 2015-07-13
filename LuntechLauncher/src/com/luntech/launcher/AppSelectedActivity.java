
package com.luntech.launcher;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;

import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.AppDialogFragment;

public class AppSelectedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.blank_layout);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        final DialogFragment newFragment = AppDialogFragment.newInstance(AppSelectedActivity.this);
        newFragment.show(getFragmentManager(), "dialog");
        super.onStart();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    public void setResult(ApplicationInfo app, boolean isSelected) {
        if (isSelected && app != null) {
            Intent intent = new Intent();
            intent.putExtra("app", app);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, null);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(null, false);
    }
}

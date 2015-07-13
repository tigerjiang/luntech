
package com.luntech.launcher.secondary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.luntech.launcher.R;

public class SmartDetialActivity extends Activity {
    private ImageButton mOrderLeftBtn, mOrderRightBtn;
    private ImageView mView;
    private static int[] mDrawablesResIds;

    static {
        mDrawablesResIds = new int[] {
                R.drawable.house_first, R.drawable.smart_curtain_detail,
                R.drawable.smart_energy_detail, R.drawable.smart_light_detail,
                R.drawable.smart_media_detail,
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_detail);
        mView = (ImageView) findViewById(R.id.resview);
        mOrderRightBtn = (ImageButton) findViewById(R.id.order_right_btn);
        mOrderLeftBtn = (ImageButton) findViewById(R.id.order_left_btn);
        mOrderLeftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), OrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mOrderRightBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), OrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        if (getIntent() != null) {
            String title = getIntent().getExtras().getString("title");
            String[] labelArray = getResources().getStringArray(R.array.scenario_array);
            for (int i = 0; i < labelArray.length; i++) {
                if (title.equals(labelArray[i])) {
                    if (i == 1 || i == 4) {
                        mOrderLeftBtn.setVisibility(View.VISIBLE);
                        mOrderRightBtn.setVisibility(View.GONE);
                    } else {
                        mOrderLeftBtn.setVisibility(View.GONE);
                        mOrderRightBtn.setVisibility(View.VISIBLE);
                    }
                    mView.setImageResource(mDrawablesResIds[i]);
                }
            }
        }
    }
}

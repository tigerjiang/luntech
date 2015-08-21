
package com.luntech.launcher.setting;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luntech.launcher.R;


public abstract class GenericFragment extends Fragment {

    protected int mActionAreaId;
    protected int mBackgroundId;
    protected int mIconTipId = R.drawable.fte_icn_tip;
    protected int mTitleStrId;
    protected int mTipStrId;
    protected ViewGroup mViewParentContainer;
    protected View mView;
    protected ViewGroup mContainer;
    /**
     * Reference to <em>FTE Activity</em>.
     */
    protected NetworkConfigActivity mFteMainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mContainer = container;

        initValues();

        mView =
                inflater.inflate(R.layout.generic_fte_fragment, container, false);
        mViewParentContainer = (ViewGroup)
                mView.findViewById(R.id.layout_action_area);
        mViewParentContainer.addView(inflater.inflate(mActionAreaId,
                container, false));

        setBackground();
        setText(R.id.textview_title, mTitleStrId);
        setText(R.id.textview_tip, mTipStrId);
        seTipImage();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFteMainActivity = (NetworkConfigActivity) activity;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Fragment#onDetach()
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mFteMainActivity = null;
    }

    private void seTipImage() {
        final ImageView imageView = (ImageView)
                mView.findViewById(R.id.imageview_tip);
        imageView.setImageResource(mIconTipId);
    }

    /**
     * Load user area
     */
    protected void loadUserArea(int userAreaId) {
        mViewParentContainer.removeAllViews();

        mViewParentContainer.addView(mFteMainActivity.getLayoutInflater().inflate(userAreaId,
                mContainer, false));

    }

    /**
     * Initializes Fragment values. This is called at the beginning of
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     */
    abstract protected void initValues();

    protected void setBackground() {
        setBackground(mBackgroundId);
    }

    protected void setBackground(int backgroundId) {
        final RelativeLayout layout = (RelativeLayout)
                mView.findViewById(R.id.main_fragment);
        layout.setBackgroundDrawable(getResources().getDrawable(backgroundId));
    }

    protected void setText(int textViewId, int stringId) {
        final TextView textView = (TextView)
                mView.findViewById(textViewId);
        if (textView != null) {
            textView.setText(stringId);
        }
    }
}

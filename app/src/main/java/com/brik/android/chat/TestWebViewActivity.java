package com.brik.android.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.brik.android.chat.common.BaseActivity;
import com.brik.android.chat.common.BaseFragment;
import com.brik.android.chat.common.BaseSwipeBackActivity;
import com.brik.android.chat.view.MyWebView;

import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/7/24.
 */
public class TestWebViewActivity extends BaseSwipeBackActivity {

    @InjectView(R.id.textView)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_swipe_back);
        textView.setText("fdsfsdfdsfdsfdsfsfsfdsfdsfsfsfdsfsfsfsfsfsdfdsfsfsfsdsfdsfsdfdsf");
    }

    @Override
    protected BaseFragment getFragmentByTag(String tag) {
        return null;
    }

    @Override
    public void onClick(View view) {

    }
}

package com.brik.chat.common;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import roboguice.activity.RoboFragmentActivity;

/**
 * Created by wangfengchen on 15/7/24.
 */
public abstract class BaseActivity extends RoboFragmentActivity implements View.OnClickListener {

    @Inject
    protected FragmentManager fragmentManager;
    @Inject
    protected LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        // 如果 Activity 有 actionbar，那么还需要在 Activity 的布局文件的根节点上设置两个属性
        //        android:clipToPadding="true"
        //        android:fitsSystemWindows="true"
    }

    /**
     * 设置ActionBar的布局
     *
     * @param layoutId 布局Id
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public View setActionBarLayout(int layoutId) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View v = layoutInflater.inflate(layoutId, null);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(v, layout);
            return v;
        }
        return null;
    }

    public BaseFragment showFragments(int content, String tag, int enter, int exit, boolean needback){
        FragmentTransaction trans = fragmentManager.beginTransaction();
        if(enter!=0&&exit!=0) {
            trans.setCustomAnimations(enter, exit);
        }
        if(needback){
            trans.add(content, getFragmentByTag(tag), tag);
            trans.addToBackStack(tag);
        }else{
            trans.replace(content, getFragmentByTag(tag), tag);
        }
        trans.commit();
        return getFragmentByTag(tag);
    }

    protected abstract BaseFragment getFragmentByTag(String tag);

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        int entryCount = fragmentManager.getBackStackEntryCount();
        Log.d("onBackPressed", "entryCount " + entryCount);
        if (entryCount > 1) {
            fragmentManager.popBackStack();
        } else {
            finish();
        }
    }

    public void popFragment() {
        backFragment(1, false);
    }

    public void backFragment(int back) {
        backFragment(back, false);
    }

    public void backFragment(int back, boolean refresh) {
        int entryCount = fragmentManager.getBackStackEntryCount();
        int i = entryCount - back;
        if(refresh) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            if (fragmentList != null && !fragmentList.isEmpty() && i > 0) {
                Fragment f = fragmentList.get(i - 1);
                if (f instanceof OnRefreshListener) {
                    ((OnRefreshListener) f).onRefresh();//刷新该page
                }
            }
        }
        if (i >= 0) {
            fragmentManager.popBackStack(i, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}

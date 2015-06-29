package com.brik.android.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ChatFragment chatFragment = new ChatFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content, chatFragment, "chat").commit();
    }

}

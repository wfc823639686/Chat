package com.brik.chat.android;

import android.app.Application;

import com.brik.chat.utils.ChatModule;

import roboguice.RoboGuice;

/**
 * Created by wangfengchen on 15/6/29.
 */
public class ChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice. setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new ChatModule());


    }
}

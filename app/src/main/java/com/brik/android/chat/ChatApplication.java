package com.brik.android.chat;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.brik.android.chat.utils.ChatModule;
import com.google.inject.Inject;

import org.jivesoftware.smack.XMPPException;

import java.util.concurrent.ExecutorService;

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

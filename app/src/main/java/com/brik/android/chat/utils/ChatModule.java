package com.brik.android.chat.utils;

import com.brik.android.chat.XMPPClient;
import com.google.inject.Binder;
import com.google.inject.Module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangfengchen on 15/10/30.
 */
public class ChatModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
        binder.bind(XMPPClient.class).toInstance(XMPPClient.getInstance());
    }
}

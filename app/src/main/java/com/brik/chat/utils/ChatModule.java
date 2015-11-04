package com.brik.chat.utils;

import android.content.Context;

import com.brik.chat.android.SystemSettings;
import com.brik.chat.android.XMPPClient;
import com.brik.chat.common.HttpClient;
import com.brik.chat.db.MessageDAO;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangfengchen on 15/10/30.
 */
public class ChatModule extends AbstractModule {

    private Context context;//系统会自己传入上下文

    public ChatModule(Context context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
        bind(XMPPClient.class).toInstance(XMPPClient.getInstance());
        bind(HttpClient.class).toInstance(HttpClient.getInstance());
        bind(SystemSettings.class).toInstance(SystemSettings.getInstance(context));
        bind(MessageDAO.class).toInstance(new MessageDAO(context));
    }
}

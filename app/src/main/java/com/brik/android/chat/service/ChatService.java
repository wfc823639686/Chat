package com.brik.android.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.brik.android.chat.XMPPClient;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatService extends Service {

    XMPPClient client = XMPPClient.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * 连接
     * @param listener
     */
    public void connect(final ConnectListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();
                    listener.onSuccess();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    listener.onError(e);
                }
            }
        });
    }

    public void getRoster(final RosterListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Collection<RosterEntry> entries = client.getRoster().getEntries();
                listener.onSuccess(entries);
            }
        });
    }
}

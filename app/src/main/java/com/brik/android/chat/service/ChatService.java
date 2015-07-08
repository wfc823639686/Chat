package com.brik.android.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;

import com.brik.android.chat.ChatEventObservable;
import com.brik.android.chat.Constants;
import com.brik.android.chat.IChatService;
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

    private IChatService.Stub mService = new IChatService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void connect() throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }

    ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * 连接
     */
    public void connect() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();
                    ChatEventObservable.getInstance().connectChanged(ConnectListener.class, true);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    ChatEventObservable.getInstance().connectChanged(ConnectListener.class, false);
                }
            }
        });
    }

    public void getRoster() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Collection<RosterEntry> entries = client.getRoster().getEntries();
            }
        });
    }
}

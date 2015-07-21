package com.brik.android.chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.brik.android.chat.service.event.ConnectEvent;
import com.brik.android.chat.service.event.LoginEvent;
import com.brik.android.chat.service.listener.ConnectListener;
import com.brik.android.chat.service.listener.LoginListener;

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity {

    private IChatService mService;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IChatService.Stub.asInterface(service);
            try {
                mService.connect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };

    ConnectListener connectListener = new ConnectListener() {
        @Override
        public void onSuccess(ConnectEvent data) {
            System.out.println("连接成功");
            System.out.println(data.name);
            try {
                mService.login("123456", "123456");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("连接失败，" + throwable.getMessage());
        }
    };

    LoginListener loginListener = new LoginListener() {
        @Override
        public void onSuccess(LoginEvent data) {
            System.out.println("login成功");
            contact();
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("login失败，" + throwable.getMessage());
        }
    };

    void contact() {
        ContactFragment contactFragment = new ContactFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content, contactFragment, "contact").commit();
    }

    public IChatService getIChatService() {
        return mService;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定进程B的服务
        Intent intent = new Intent(Constants.CHAT_SERVICE_ACTION);
        intent.setPackage(getPackageName());
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        ChatEventObservable.getInstance().register(connectListener);
        ChatEventObservable.getInstance().register(loginListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);

        ChatEventObservable.getInstance().unregister(connectListener);
        ChatEventObservable.getInstance().unregister(loginListener);
    }
}

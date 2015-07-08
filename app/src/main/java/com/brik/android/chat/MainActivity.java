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

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity {

    private IChatService mService;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IChatService.Stub.asInterface(service);
            chat();
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };

    void chat() {
        ChatFragment chatFragment = new ChatFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content, chatFragment, "chat").commit();
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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}

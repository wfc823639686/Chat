package com.brik.android.chat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import roboguice.activity.RoboFragmentActivity;

/**
 * Created by wangfengchen on 15/7/21.
 */
public class ChatActivity extends RoboFragmentActivity {

    String user, jid;

    int type;

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
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("jid", jid);
        args.putInt("type", type);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content, chatFragment, "chat").commit();
    }

    public IChatService getIChatService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取user
        getData();
        //绑定进程B的服务
        Intent intent = new Intent(Constants.CHAT_SERVICE_ACTION);
        intent.setPackage(getPackageName());
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    void getData() {
        Intent data = getIntent();
        if(data!=null) {
            user = data.getStringExtra("user");
            jid = data.getStringExtra("jid");
            type = data.getIntExtra("type", 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}

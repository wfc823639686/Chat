package com.brik.chat.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.brik.chat.common.BaseActivity;
import com.brik.chat.common.BaseFragment;
import com.brik.chat.service.ChatService;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends BaseActivity {

    @Inject
    private XMPPClient client;
    @Inject
    private SystemSettings settings;

    private Messenger sMessenger;

    TextView titleView;

    ImageButton titleOptionView;

    BaseFragment currentFragment;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ChatService.CHAT_MESSAGE_CONNECT_CALLBACK:
                    if(msg.arg1==1) {
                        //连接成功
                        Log.d("", "connect success");
                        login();
                    } else {
                        Log.d("", "connect fail");
                    }
                    break;
            }
        }
    };

    private Messenger cMessenger = new Messenger(mHandler);

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sMessenger = new Messenger(service);
            try {
                ChatService.registerMessenger(sMessenger, cMessenger);
                connect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected BaseFragment getFragmentByTag(String tag) {
        switch (tag) {
            case "contact":
                return new ContactFragment();
            case "createmulti":
                return new CreateMultiUserChatFragment();
            case "login":
                return new LoginFragment();
            case "register":
                return new RegisterFragment();
            case "search":
                return new SearchFragment();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitleView();
        onListener();
        startChatService();
        bindChatService();
    }

    void initTitleView() {
        View view = setActionBarLayout(R.layout.layout_main_title);
        titleView = (TextView) view.findViewById(R.id.main_title_text);
        titleOptionView = (ImageButton) view.findViewById(R.id.main_title_right);
    }

    void onListener() {
        titleOptionView.setOnClickListener(this);
    }

    void connect() throws RemoteException {
        android.os.Message msg = android.os.Message.obtain(null, ChatService.CHAT_MESSAGE_CONNECT);
        msg.replyTo = cMessenger;
        sMessenger.send(msg);
    }

    void login() {
        String username = settings.getUsername();
        String password = settings.getPassword();
        if(username==null || password==null) {//去登陆
            currentFragment = showFragments(R.id.content, "login", R.anim.fragment_enter_anim, R.anim.fragment_exit_anim, true);
            return;
        }
        client.login(username, password, new XMPPClient.LoginListener() {
            @Override
            public void onSuccess() {
                Log.d("login", "成功");
                loginSuccess();
            }

            @Override
            public void onFail(Throwable t) {
                Log.e("login", "失败", t);
                currentFragment = showFragments(R.id.content, "login", R.anim.fragment_enter_anim, R.anim.fragment_exit_anim, true);
            }
        });
    }

    public void loginSuccess() {
        //获取离线数据
        client.getOfflineMessage(new XMPPClient.GetOfflineMessageListener() {
            @Override
            public void onSuccess() {
                Log.d("getOfflineMessage", "onSuccess");
            }

            @Override
            public void onFail(Throwable t) {
                Log.d("getOfflineMessage", "onFail");
            }

            @Override
            public void onComplete() {
                Log.d("getOfflineMessage", "onComplete");
                //上线
                client.available();
                currentFragment = showFragments(R.id.content, "contact", R.anim.fragment_enter_anim, R.anim.fragment_exit_anim, true);
            }
        });

    }

    void startChatService() {
        Intent intent = new Intent(this, ChatService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }

    void bindChatService() {
        //绑定进程B的服务
        Intent intent = new Intent(Constants.CHAT_SERVICE_ACTION);
        intent.setPackage(getPackageName());
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ChatService.unregisterMessenger(sMessenger, cMessenger);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(mConnection);
    }

    long exitTime;

    @Override
    public void finish() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.finish();
            System.exit(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_title_right:
//                if(currentFragment!=null && currentFragment instanceof OnOptionClickListener) {
//                    OnOptionClickListener listener = (OnOptionClickListener) currentFragment;
//                    listener.onOptionClick(view);
//                }
            currentFragment = showFragments(R.id.content, "search", R.anim.fragment_enter_anim, R.anim.fragment_exit_anim, true);
            break;
        }
    }
}

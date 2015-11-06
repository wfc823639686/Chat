package com.brik.chat.android;

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

    private IChatService mService;

    TextView titleView;

    ImageButton titleOptionView;

    BaseFragment currentFragment;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IChatService.Stub.asInterface(service);
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected BaseFragment getFragmentByTag(String tag) {
        if(tag.equals("contact")) {
            return new ContactFragment();
        } else if(tag.equals("createmulti")){
            return new CreateMultiUserChatFragment();
        } else if (tag.equals("login")) {
            return new LoginFragment();
        } else if(tag.equals("register")) {
            return new RegisterFragment();
        } else if(tag.equals("search")) {
            return new SearchFragment();
        }
        return null;
    }

    public IChatService getIChatService() {
        return mService;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitleView();
        onListener();
        initXMPP();
    }

    void initTitleView() {
        View view = setActionBarLayout(R.layout.layout_main_title);
        titleView = (TextView) view.findViewById(R.id.main_title_text);
        titleOptionView = (ImageButton) view.findViewById(R.id.main_title_right);
    }

    void onListener() {
        titleOptionView.setOnClickListener(this);
    }

    void initXMPP() {

        client.connect(new XMPPClient.ConnectListener() {
            @Override
            public void onSuccess() {
                Log.d("connect", "成功");
                login();
            }

            @Override
            public void onFail(Throwable t) {
                Log.e("connect", "失败", t);
            }
        });
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
                startChatService();
                bindChatService();
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

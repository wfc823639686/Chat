package com.brik.chat.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.brik.chat.db.MessageDAO;
import com.brik.chat.entry.IMessage;
import com.brik.chat.service.ChatService;
import com.brik.chat.utils.MapUtils;
import com.brik.chat.view.PlayAudioButton;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.sql.SQLException;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;

/**
 * Created by wangfengchen on 15/7/13.
 */
public class TestActivity extends RoboFragmentActivity {

    IChatService mService;

    @Inject
    XMPPClient client;

    @Inject
    MessageDAO messageDAO;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IChatService.Stub.asInterface(service);
            testFileTransfer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    void startChatService() {
        Intent intent = new Intent(this, ChatService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }

    void stopChatService() {
        Intent intent = new Intent(this, ChatService.class);
        stopService(intent);
    }

    void bindChatService() {
        //绑定进程B的服务
        Intent intent = new Intent(Constants.CHAT_SERVICE_ACTION);
        intent.setPackage(getPackageName());
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        bindChatService();
//        testPlayAudio();
        testMessageDB();
    }

    public void testMessageDB() {
        IMessage im = new IMessage();
        im.setFrom("from");
        im.setTo("to");
        im.setBody("body");
        messageDAO.add(im);
        Log.d("testMessageDB", "testMessageDB");
        try {
            List<IMessage> iMessageList = messageDAO.getAll(0, 10);
            for (IMessage i : iMessageList) {
                Log.d("message", i.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    void testFileTransfer() {
        final String filePath = SystemSettings.TEMP_ROOT_DIR + "/c.png";
        try {
            mService.addFileTransfer(
                    MapUtils.create(
                            "fileurl", "https://www.baidu.com/img/bdlogo.png",
                            "filepath", filePath));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void testPlayAudio() {
        PlayAudioButton button = (PlayAudioButton) findViewById(R.id.view);
        final String filePath = SystemSettings.TEMP_ROOT_DIR + "/b.mp3";
        button.setAudioFilePath(filePath);
    }

    void testSendFile() {
        final String filePath = SystemSettings.TEMP_ROOT_DIR + "/a.png";
        ImageLoader.getInstance().displayImage(filePath, (ImageView) findViewById(R.id.action_settings));
        findViewById(R.id.action_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XMPPClient.getInstance().connect(new XMPPClient.ConnectListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("connect", "onSuccess");
                        XMPPClient.getInstance().login("111111", "123456", new XMPPClient.LoginListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("login", "onSuccess");
                                Log.d("sendTalkFile", "filePath: " + filePath);
                                XMPPClient.getInstance().sendTalkFile("123456", filePath, "yuyin", new XMPPClient.SendTalkFileListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("sendTalkFile", "onSuccess");
                                    }

                                    @Override
                                    public void onFail(Throwable t) {
                                        Log.d("sendTalkFile", "onFail");
                                    }

                                    @Override
                                    public void onProgress(double s) {

                                    }
                                });
                            }

                            @Override
                            public void onFail(Throwable t) {
                                Log.d("login", "onFail");
                            }
                        });
                    }

                    @Override
                    public void onFail(Throwable t) {
                        Log.d("connect", "onFail");
                    }
                });

            }
        });
    }


}

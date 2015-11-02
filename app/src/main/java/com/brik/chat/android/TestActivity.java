package com.brik.chat.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wangfengchen on 15/7/13.
 */
public class TestActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String filePath =  SystemSettings.TEMP_ROOT_DIR+"/a.png";
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

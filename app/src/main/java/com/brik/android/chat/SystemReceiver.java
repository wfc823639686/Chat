package com.brik.android.chat;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.brik.android.chat.service.ChatService;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

/**
 * Created by wangfengchen on 15/7/24.
 */
public class SystemReceiver extends BroadcastReceiver {

    Logger logger = LoggerFactory.getLogger(SystemReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.warn("onReceive ------------");
        logger.warn("action ------------"+intent.getAction());
        if(intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            checkServiceAlive(context);
        }
    }

    void checkServiceAlive(Context context) {
        boolean isChatServiceRunning = false, isDaemonServiceRunning = false;
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
            if("com.brik.android.chat.service.ChatService".equals(service.service.getClassName())) {
                isChatServiceRunning = true;
            }else if("com.brik.android.chat.service.DaemonService".equals(service.service.getClassName())) {
                isDaemonServiceRunning = true;
            }
        }
        logger.info("chat service running"+isChatServiceRunning);
        logger.info("daemon service running"+isDaemonServiceRunning);
        if (!isChatServiceRunning) {
            startChatService(context);
        }
        if (!isDaemonServiceRunning) {
            startDaemonService(context);
        }
    }

    void startChatService(Context context) {
        Intent i = new Intent(context, ChatService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }

    void startDaemonService(Context context) {

    }
}

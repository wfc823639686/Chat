package com.brik.chat.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.brik.chat.android.IChatService;
import com.brik.chat.android.SystemSettings;
import com.brik.chat.android.XMPPClient;
import com.brik.chat.db.MessageDAO;
import com.brik.chat.entry.IMessage;
import com.google.inject.Inject;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import roboguice.service.RoboService;

import static org.jivesoftware.smack.packet.Message.*;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatService extends RoboService {

    Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Inject
    MessageDAO messageDAO;
    @Inject
    XMPPClient client;

    private IChatService.Stub mService = new IChatService.Stub() {

    };

    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }

    @Inject
    ExecutorService executor;

    ChatFileTransferListener chatFileTransferListener;

    @Override
    public void onCreate() {
        super.onCreate();
        logger.warn("onCreate -----------------");
        chatFileTransferListener = new ChatFileTransferListener(this);
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
        startForeground(getClass().hashCode(), notification);
        rec();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("ChatService", "startCommand");
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
//      return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.warn("onDestroy -----------------");
        stopForeground(false);
    }

    public void rec() {
        System.out.println("开始接收");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                client.getChatManager().addChatListener(new ChatManagerListener() {

                    @Override
                    public void chatCreated(Chat chat, boolean b) {
                        chat.addMessageListener(new MessageListener() {
                            public void processMessage(Chat newchat, Message message) {
                                if (message.getType() == Type.chat) {
                                    messageDAO.add(new IMessage(message));
                                } else if (message.getType() == Type.error) {
                                    System.out.println("error");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 文件接受监听器
     *
     */
    static class ChatFileTransferListener implements FileTransferListener {
        Context context;

        public ChatFileTransferListener(Context context) {
            this.context = context;
        }

        @Override
        public void fileTransferRequest(FileTransferRequest request) {
            try {
                String des = request.getDescription();
                String pathStr = SystemSettings.CHAT_ROOT_DIR;
                if(des.equals("yuyin")) {
                    pathStr += "/yuyin/";
                }
                File path = new File(pathStr);
                if(!path.exists())
                    path.mkdirs();
                File insFile = new File(
                        pathStr + request.getFileName());
                IncomingFileTransfer infiletransfer = request.accept();
                infiletransfer.recieveFile(insFile);
                Log.d("fileTransferRequest", "接收成功");
//                sendBroadcastFile(context, insFile.getAbsolutePath());
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }
}

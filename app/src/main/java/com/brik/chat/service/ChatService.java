package com.brik.chat.service;

import android.app.Notification;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.brik.chat.android.Constants;
import com.brik.chat.android.IChatService;
import com.brik.chat.android.R;
import com.brik.chat.android.XMPPClient;
import com.brik.chat.common.ChatFileTransferListener;
import com.brik.chat.common.HttpClient;
import com.brik.chat.entry.IMessage;
import com.google.inject.Inject;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import roboguice.service.RoboService;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatService extends RoboService {

    @Inject
    XMPPClient client;

    private IChatService.Stub mService = new IChatService.Stub() {

        @Override
        public void addFileTransfer(Map m) throws RemoteException {
            try {
                chatFileTransferListener.put(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private List<Messenger> mClients = new ArrayList<>();

    public static void registerMessenger(Messenger service, Messenger client) throws RemoteException {
        if(service==null) throw new RemoteException("service is null");
        android.os.Message msg = android.os.Message.obtain(null, CHAT_MESSAGE_REGISTER_CLIENT);
        msg.replyTo = client;
        service.send(msg);
    }

    public static void unregisterMessenger(Messenger service, Messenger client) throws RemoteException {
        if(service==null) throw new RemoteException("service is null");
        android.os.Message msg = android.os.Message.obtain(null, CHAT_MESSAGE_UNREGISTER_CLIENT);
        msg.replyTo = client;
        service.send(msg);
    }

    public static final int CHAT_MESSAGE_REGISTER_CLIENT = 0x01;
    public static final int CHAT_MESSAGE_UNREGISTER_CLIENT = 0x02;
    public static final int CHAT_MESSAGE_CONNECT = 0x03;
    public static final int CHAT_MESSAGE_CONNECT_CALLBACK = 0x04;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHAT_MESSAGE_REGISTER_CLIENT:
                    if(msg.replyTo!=null) {
                        mClients.add(msg.replyTo);
                    }
                    break;
                case CHAT_MESSAGE_UNREGISTER_CLIENT:
                    if(msg.replyTo!=null) {
                        mClients.remove(msg.replyTo);
                    }
                    break;
                case CHAT_MESSAGE_CONNECT:
                    Log.d("chat Handler", "request connect");
                    connect();
                    break;
                default:
                    break;
            }
        }
    };
    //It's the messenger of server
    private Messenger sMessenger = new Messenger(mHandler);

    private void connect() {
        client.connect(new XMPPClient.ConnectListener() {
            @Override
            public void onSuccess() {
                Log.d("connect", "成功");
                sendConnectMessage(1);
                rec();
            }

            @Override
            public void onFail(Throwable t) {
                Log.e("connect", "失败", t);
                sendConnectMessage(0);
            }
        });
    }

    /**
     * 发送连接msg
     * @param success 1: 成功0: 失败
     */
    void sendConnectMessage(int success) {
        android.os.Message msg = android.os.Message.obtain(null, CHAT_MESSAGE_CONNECT_CALLBACK);
        msg.replyTo = sMessenger;
        msg.arg1 = success;
        sendMessage(msg);
    }

    void sendMessage(android.os.Message msg) {
        for (int i = mClients.size() - 1; i >= 0; i --) {
            try {
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMessenger.getBinder();
    }

    @Inject
    ExecutorService executor;
    @Inject
    HttpClient httpClient;

    ChatFileTransferListener chatFileTransferListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ChatService", "onCreate");
        chatFileTransferListener = new ChatFileTransferListener(this, httpClient);
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
        startForeground(getClass().hashCode(), notification);
//        startFileTransTask();
    }

    void startFileTransTask() {
        Log.d("chatFileTransfer", "startFileTransTask");
        executor.execute(chatFileTransferListener);
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
        Log.d("ChatService", "onDestroy");
        stopForeground(false);
    }

    public void rec() {
        System.out.println("开始接收");
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                client.getChatManager().addChatListener(new ChatManagerListener() {
//
//                    @Override
//                    public void chatCreated(Chat chat, boolean b) {
//                        chat.addMessageListener(new MessageListener() {
//                            public void processMessage(Chat newchat, Message message) {
//                                if (message.getType() == Message.Type.chat) {
//                                    messageDAO.add(new IMessage(message));
//                                } else if (message.getType() == Message.Type.error) {
//                                    System.out.println("error");
//                                }
//                            }
//                        });
//                    }
//                });

                //接收message
                client.getXMPPConnection().addPacketListener(new PacketListener() {

                    public void processPacket(Packet packet) {
                        Message message =
                                (Message) packet;
                        IMessage im = new IMessage(message);
                        im.setRoomId(message.getFrom());
                        client.saveMessage(im);
                        sendBroadcastMessage(im);
                    }
                }, new PacketTypeFilter(Message.class));

            }
        });
    }

    void sendBroadcastMessage(IMessage im) {
        Intent intent = new Intent(Constants.SEND_MESSAGE_ACTION);
        intent.putExtra("message", im);
        this.sendBroadcast(intent);
    }

    /**
     * 文件接受监听器
     *
     */
//    static class ChatFileTransferListener implements FileTransferListener {
//        Context context;
//
//        public ChatFileTransferListener(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public void fileTransferRequest(FileTransferRequest request) {
//            try {
//                String des = request.getDescription();
//                String pathStr = SystemSettings.CHAT_ROOT_DIR;
//                if(des.equals("yuyin")) {
//                    pathStr += "/yuyin/";
//                }
//                File path = new File(pathStr);
//                if(!path.exists())
//                    path.mkdirs();
//                File insFile = new File(
//                        pathStr + request.getFileName());
//                IncomingFileTransfer infiletransfer = request.accept();
//                infiletransfer.recieveFile(insFile);
//                Log.d("fileTransferRequest", "接收成功");
////                sendBroadcastFile(context, insFile.getAbsolutePath());
//            } catch (XMPPException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}

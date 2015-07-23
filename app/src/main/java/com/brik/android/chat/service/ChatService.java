package com.brik.android.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.brik.android.chat.ChatEventObservable;
import com.brik.android.chat.IChatService;
import com.brik.android.chat.XMPPClient;
import com.brik.android.chat.db.MessageDAO;
import com.brik.android.chat.entry.MessageWrapper;
import com.brik.android.chat.service.event.ConnectEvent;
import com.brik.android.chat.service.listener.ConnectListener;
import com.brik.android.chat.service.listener.IMessageListener;
import com.brik.android.chat.service.listener.LoginListener;
import com.brik.android.chat.service.event.MessageEvent;
import com.brik.android.chat.service.listener.RegisterListener;
import com.brik.android.chat.service.event.RosterEvent;
import com.brik.android.chat.service.listener.RosterListener;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jivesoftware.smack.packet.Message.*;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatService extends Service {

    MessageDAO messageDAO;

    XMPPClient client = XMPPClient.getInstance();

    private IChatService.Stub mService = new IChatService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void connect() throws RemoteException {
            ChatService.this.connect();
        }

        @Override
        public void login(String username, String password) throws RemoteException {
            ChatService.this.login(username, password);
        }

        @Override
        public void register() throws RemoteException {
            ChatService.this.register();
        }

        @Override
        public void getRoster() throws RemoteException {
            ChatService.this.getRoster();
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }

    ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        messageDAO = new MessageDAO(this);
    }

    /**
     * 连接
     */
    public void connect() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();
                    ConnectEvent event = new ConnectEvent();
                    event.name = "haha";
                    ChatEventObservable.getInstance().successChanged(ConnectListener.class, event);
                    rec();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    ChatEventObservable.getInstance().failChanged(ConnectListener.class, e);
                }
            }
        });
    }

    public void login(final String username, final String password) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    client.login(username, password);
                    ChatEventObservable.getInstance().successChanged(LoginListener.class);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    ChatEventObservable.getInstance().failChanged(LoginListener.class, e);
                }
            }
        });
    }

    public void register() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    client.register();
                    ChatEventObservable.getInstance().successChanged(RegisterListener.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatEventObservable.getInstance().failChanged(RegisterListener.class, e);
                }
            }
        });
    }

    public void getRoster() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Roster roster = client.getRoster();
                RosterEvent rosterEvent = new RosterEvent();
                rosterEvent.roster = roster;
                ChatEventObservable.getInstance().successChanged(RosterListener.class, rosterEvent);
            }
        });
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
                                if(message.getType()==Type.chat) {
                                    messageDAO.add(new MessageWrapper(message));
                                    MessageEvent messageEvent = new MessageEvent();
                                    messageEvent.message = message;
                                    ChatEventObservable.getInstance().successChanged(IMessageListener.class, messageEvent);
                                } else if(message.getType()==Type.error) {
                                    System.out.println("error");
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}

package com.brik.android.chat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brik.android.chat.db.MessageDAO;
import com.brik.android.chat.db.entry.MessageConver;
import com.brik.android.chat.db.entry.OrmMessage;
import com.brik.android.chat.service.ConnectEvent;
import com.brik.android.chat.service.ConnectListener;
import com.brik.android.chat.service.LoginEvent;
import com.brik.android.chat.service.LoginListener;
import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatFragment extends RoboFragment implements SwipeRefreshLayout.OnRefreshListener{

    int p;

    String userId = "admin@snowyoung.org";

    Chat chat;

    XMPPClient client = XMPPClient.getInstance();

    private SuperRecyclerView mRecycler;

    @InjectView(R.id.chat_send)
    private Button sendView;

    @Inject
    private LayoutInflater layoutInflater;

    private MyAdapter mAdapter = new MyAdapter();

    private Context mContext;

    private IChatService mService;

    private ConnectListener connectListener = new ConnectListener() {
        @Override
        public void onSuccess(ConnectEvent data) {
            System.out.println("连接成功");
            System.out.println(data.name);
            try {
                mService.login("123456", "123456");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("连接失败，" + throwable.getMessage());
        }
    };

    private LoginListener loginListener = new LoginListener() {
        @Override
        public void onSuccess(LoginEvent data) {
            System.out.println("login成功");
            createChat();
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("login失败，" + throwable.getMessage());
        }
    };

    private MessageDAO messageDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatEventObservable.getInstance().register(connectListener);
        ChatEventObservable.getInstance().register(loginListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatEventObservable.getInstance().unregister(connectListener);
        ChatEventObservable.getInstance().unregister(loginListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        if(activity instanceof MainActivity) {
            mService = ((MainActivity)activity).getIChatService();
        }
        messageDAO = new MessageDAO(getActivity());

    }

    void getMessageFromDB(int p) {
        try {
            List<OrmMessage> messages = messageDAO.getMessage(userId, p, 10);
            for(OrmMessage ormMessage : messages) {
                mAdapter.add(MessageConver.toMessage(ormMessage));
            }
            mRecycler.getSwipeToRefresh().setRefreshing(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fargment_chat, container, false);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initSuperRecyclerView(View view) {
        mRecycler = (SuperRecyclerView) view.findViewById(R.id.list);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSuperRecyclerView(view);
        getMessageFromDB(0);
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("hahahahahaha");
            }
        });
    }

    @Override
    public void onRefresh() {
        getMessageFromDB(p++);
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Message> items = new ArrayList<>();

        public void add(Message item) {
            items.add(item);
            notifyItemInserted(items.size()-1);
        }

        public Message getItem(int position) {
            return items.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==0) {
                View view = layoutInflater.inflate(R.layout.item_my_message, parent, false);
                return new MyViewHolder(view);
            } else {
                View view = layoutInflater.inflate(R.layout.item_other_message, parent, false);
                return new OtherViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if(viewType==0) {
                MyViewHolder vh = (MyViewHolder) holder;
                vh.bindView(getItem(position));
            } else {
                OtherViewHolder vh = (OtherViewHolder) holder;
                vh.bindView(getItem(position));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {

            Message m = items.get(position);
            if(userId.equals(m.getFrom())) return 1;
            return 0;
        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView headView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_my_text);
            headView = (ImageView) itemView.findViewById(R.id.item_my_head);
        }

        void bindView(Message m) {
            textView.setText(m.getBody());
        }
    }

    class OtherViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView headView;

        public OtherViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_other_text);
            headView = (ImageView) itemView.findViewById(R.id.item_other_head);
        }

        void bindView(Message m) {
            textView.setText(m.getBody());
        }
    }

    void createChat() {
        chat = client.getChatManager().createChat(userId, null);
        // 监听聊天消息
        client.getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new MessageListener() {

                    @Override
                    public void processMessage(Chat arg0, Message message) {
                        String result = message.getFrom() + ":" + message.getBody();
                        System.out.println(result);
                        mAdapter.add(message);
                    }
                });
            }
        });


    }

    public void sendMessage(String message) {
        try {
            Message m = new Message();
            m.setBody(message);
            chat.sendMessage(message);
            mAdapter.add(m);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}

package com.brik.android.chat;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatFragment extends RoboFragment {

    private static final int NEW_MESSAGE = 0x01;

    private static final String MESSAGE_TAG = "msg";

    String userId = "123";

    Chat chat;

    XMPPClient client = XMPPClient.getInstance();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

        }
    };

    @InjectView(R.id.recyclerview)
    private RecyclerView recyclerView;

    @InjectView(R.id.chat_send)
    private Button sendView;

    @Inject
    private LayoutInflater layoutInflater;

    private MyAdapter mAdapter = new MyAdapter();

    private Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fargment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

//        createChat();

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("hahahahahaha");
            }
        });
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
//        try {
            Message m = new Message();
            m.setBody(message);
            m.setFrom("123");
//            chat.sendMessage(message);
            mAdapter.add(m);
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
    }
}

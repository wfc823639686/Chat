package com.brik.android.chat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brik.android.chat.db.MessageDAO;
import com.brik.android.chat.entry.IMessage;
import com.brik.android.chat.service.event.ConnectEvent;
import com.brik.android.chat.service.event.MessageEvent;
import com.brik.android.chat.service.listener.ConnectListener;
import com.brik.android.chat.service.event.LoginEvent;
import com.brik.android.chat.service.listener.IMessageListener;
import com.brik.android.chat.service.listener.LoginListener;
import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.muc.MultiUserChat;

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

    String user = "";

    Chat chat;

    MultiUserChat muc;

    String multiUserRoom = "";

    int userType = 1;

    XMPPClient client = XMPPClient.getInstance();

    private SuperRecyclerView mRecycler;

    @InjectView(R.id.editText)
    private EditText editText;

    @Inject
    private LayoutInflater layoutInflater;

    private MyAdapter mAdapter = new MyAdapter();

    private Context mContext;

    private IChatService mService;

    private MessageDAO messageDAO;

    private ConnectListener connectListener = new ConnectListener() {
        @Override
        public void onSuccess(ConnectEvent data) {
            System.out.println("连接成功");
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
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("login失败，" + throwable.getMessage());
        }
    };

    private IMessageListener messageListener = new IMessageListener() {

        @Override
        public void onSuccess(MessageEvent data) {
            final Message message = data.message;
            String[] fs = message.getFrom().split("/");
            if(user.equals(fs[0])) {
                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add(new IMessage(message));
                        int count = mAdapter.getItemCount();
                        mRecycler.getRecyclerView().scrollToPosition(count==0?0 : count-1);
                    }
                });
            }
        }

        @Override
        public void onFail(Throwable throwable) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取user
        getData();
        ChatEventObservable.getInstance().register(connectListener);
        ChatEventObservable.getInstance().register(loginListener);
        ChatEventObservable.getInstance().register(messageListener);

        switch (userType) {
            case 1:
                createChat();
                break;
            case 2:
                createMultiChat();
                break;
        }
    }

    void getData() {
        Bundle args = getArguments();
        if(args!=null) {
            user = args.getString("user", "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatEventObservable.getInstance().unregister(connectListener);
        ChatEventObservable.getInstance().unregister(loginListener);
        ChatEventObservable.getInstance().unregister(messageListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        if(activity instanceof ChatActivity) {
            mService = ((ChatActivity)activity).getIChatService();
        }
        messageDAO = new MessageDAO(getActivity());

    }

    void getMessageFromDB(int p) {
        try {
            List<IMessage> list = messageDAO.listAll();
            List<IMessage> ormMessageList = messageDAO.getMessage(user, p, 10);
            mAdapter.addAllAtStart(ormMessageList);
            mRecycler.getSwipeToRefresh().setRefreshing(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
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
        initEditText();
        getMessageFromDB(0);
    }

    void initEditText() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String text = editText.getText().toString();
                    if(!text.equals("")) {
                        sendMessage(text);
                        editText.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onRefresh() {
        getMessageFromDB(p++);
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<IMessage> items = new ArrayList<>();

        public void add(IMessage item) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }

        public void addAllAtStart(List<IMessage> list) {
            items.addAll(0, list);
        }

        public IMessage getItem(int position) {
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

            IMessage mw = items.get(position);
            Message m = mw.getMessage();
            String[] fs = m.getFrom().split("/");
            if(chat.getParticipant().equals(fs[0])) {
                return 1;
            }else {
                return 0;
            }
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

        void bindView(IMessage mw) {
            Message m = mw.getMessage();
            textView.setText(m.getBody());
            XMPPError error = m.getError();
            if(error!=null) {
                if(error.getCode()==-1) {
                    //重试
                }
            }
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

        void bindView(IMessage mw) {
            Message m = mw.getMessage();
            textView.setText(m.getBody());
        }
    }

    void createChat() {
        System.out.println("监听聊天消息...");
        chat = client.getChatManager().createChat(user, null);
    }

    void createMultiChat() {
        try {
            muc = new MultiUserChat(client.getXMPPConnection(), multiUserRoom);
            // 创建聊天室,进入房间后的nickname
            muc.join("123456");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        muc.addMessageListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                final Message message = (Message) packet;
                String result = message.getFrom() + ":" + message.getBody();
                System.out.println(result);
                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add(new IMessage(message));
                    }
                });
            }
        });
    }

    public void sendMessage(String message) {
        try {
            Message m = new Message();
            m.setBody(message);
            IMessage mw = null;
            switch (userType) {
                case 1://user
                    if(chat!=null) {
                        chat.sendMessage(m);
                        mw = new IMessage(m);
                        messageDAO.add(mw);
                    }else {//如果为空，则重试
                        m.setError(new XMPPError(-1));//发送失败，请重试
                    }
                    break;
                case 2://room
                    if(muc!=null) {
                        muc.sendMessage(m);
                    }else {//如果为空，则重试
                        m.setError(new XMPPError(-1));//发送失败，请重试
                    }
                    break;
            }
            if(mw!=null) {
                mAdapter.add(mw);
                int count = mAdapter.getItemCount();
                mRecycler.getRecyclerView().scrollToPosition(count==0?0:count-1);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}

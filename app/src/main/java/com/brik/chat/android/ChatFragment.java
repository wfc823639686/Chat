package com.brik.chat.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brik.chat.adapter.IMessageAdapter;
import com.brik.chat.common.BaseFragment;
import com.brik.chat.common.HttpClient;
import com.brik.chat.entry.IMessage;
import com.brik.chat.view.RecordButton;
import com.google.inject.Inject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class ChatFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    int p;

    String user = "";

    Chat chat;

    MultiUserChat muc;

    String multiUserRoom = "";

    int userType;

    @Inject
    XMPPClient client;
    @Inject
    HttpClient httpClient;

    private SuperRecyclerView mRecycler;

    @InjectView(R.id.chat_input)
    private EditText editText;
    @InjectView(R.id.chat_toggle_btn)
    private Button toggleBtn;
    @InjectView(R.id.chat_speak)
    private RecordButton recordBtn;
    @InjectView(R.id.chat_send_other)
    private Button otherBtn;

    private boolean showEdit = true;//输入框是否显示，默认是显示的

    @Inject
    private LayoutInflater layoutInflater;

    private IMessageAdapter mAdapter;

    private Context mContext;

    private IChatService mService;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.SEND_MESSAGE_ACTION)) {
                IMessage im = intent.getParcelableExtra("message");
                notifyMessageView(im);
            }
        }
    };

    void registerMessageReceiver() {
        mContext.registerReceiver(messageReceiver, new IntentFilter(Constants.SEND_MESSAGE_ACTION));
    }

    void unregisterMessageReceiver() {
        mContext.unregisterReceiver(messageReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_toggle_btn:
                //切换功能按钮
                if(showEdit) {
                    recordBtn.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    showEdit = false;
                } else {
                    recordBtn.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                    showEdit = true;
                }
                break;
            case R.id.chat_send_other:
                //发送其他
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取user
        getData();

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
            multiUserRoom = args.getString("jid", "");
            userType = args.getInt("type");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mAdapter = new IMessageAdapter(activity, "chat");
        if(activity instanceof ChatActivity) {
            mService = ((ChatActivity)activity).getIChatService();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerMessageReceiver();
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterMessageReceiver();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initSuperRecyclerView(View view) {
        mRecycler = (SuperRecyclerView) view.findViewById(R.id.list);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mRecycler.setAdapter(mAdapter);
    }

    void initRecordButton() {
        String pathStr = SystemSettings.CHAT_ROOT_DIR+ "/yuyin/";
        File path = new File(pathStr);
        if(!path.exists()) {
            path.mkdirs();
        }
        recordBtn.setSavePath(pathStr);
        recordBtn.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(long time, String audioPath) {
                Log.d("onFinishedRecord", "audioPath: " + audioPath);
                sendAudio(time, audioPath);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleBtn.setOnClickListener(this);
        otherBtn.setOnClickListener(this);
        initSuperRecyclerView(view);
        initRecordButton();
        initEditText();
//        getMessageFromDB(0);
    }

    void initEditText() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String text = editText.getText().toString();
                    if(!text.equals("")) {
                        sendText(text);
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
        client.loadMessageFromDB(user, p++, new XMPPClient.LoadMessageFromDBListener() {
            @Override
            public void onSuccess(final List<IMessage> list) {
                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAllAtStart(list);
                    }
                });
            }

            @Override
            public void onFail(Throwable t) {

            }

            @Override
            public void onComplete() {
                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecycler.getSwipeToRefresh().setRefreshing(false);
                    }
                });
            }
        });
    }



    void createChat() {
        System.out.println("监听聊天消息...");
        chat = client.getChatManager().createChat(user, null);
        Log.d("chat getThreadID", chat.getThreadID());
        Log.d("chat getParticipant", chat.getParticipant());
    }

    void createMultiChat() {
//        try {
//            muc = new MultiUserChat(client.getXMPPConnection(), multiUserRoom);
//            // 创建聊天室,进入房间后的nickname
//            muc.join("123456");
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//        muc.addMessageListener(new PacketListener() {
//            @Override
//            public void processPacket(Packet packet) {
//                final Message message = (Message) packet;
//                String result = message.getFrom() + ":" + message.getBody();
//                System.out.println(result);
//                mRecycler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.add(new IMessage(message));
//                    }
//                });
//            }
//        });
    }

    public void sendAudio(final long time, final String filePath) {
        httpClient.uploadAudio(filePath, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                Log.d("uploadAudio", "onSuccess result " + response);
                String audioUrl = response.optString("url");
                IMessage im = new IMessage();
                im.setCustomType(IMessage.CUSTOM_TYPE_AUDIO);//自定义type
                im.setFileSize(new File(filePath).length());//文件大小
                im.setFileUrl(audioUrl);
                im.setFilePath(filePath);
                im.setTimeLength(time);
                try {
                    ChatFragment.this.sendMessage(im);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
                Log.d("uploadAudio", "onFailure result " + e + ",errorResponse " + errorResponse);
                //TODO 上传出错，重试
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.d("uploadAudio", "onFinish");
            }
        });

    }

    public void sendMessage(IMessage im) throws XMPPException {
        switch (userType) {
            case 1://user
                if(chat!=null) {
                    chat.sendMessage(im.getMessage());
                    im.setFrom(XMPPClient.getInstance().getUser());
                    im.setTo(chat.getParticipant());
                    im.setRoomId(user);
                }else {//如果为空，则重试

                }
                break;
            case 2://room
//                if(muc!=null) {
//                    m.setTo(muc.getRoom());
//                    m.setType(Message.Type.groupchat);
//                    m.setFrom(XMPPClient.getInstance().getUser());
//                    muc.sendMessage(m);
//                    mw = new IMessage(m);
//                }else {//如果为空，则重试
//
//                }
                break;
        }
        notifyMessageView(im);
    }

    public void sendText(String message) {
        try {
            IMessage im = new IMessage();
            im.setCustomType(IMessage.CUSTOM_TYPE_TEXT);
            im.setBody(message);
            sendMessage(im);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息列表增加消息
     */
    public void notifyMessageView(IMessage mw) {
        if(mw!=null) {
            mAdapter.add(mw);
            int count = mAdapter.getItemCount();
            mRecycler.getRecyclerView().scrollToPosition(count==0?0:count-1);
        }
    }
}

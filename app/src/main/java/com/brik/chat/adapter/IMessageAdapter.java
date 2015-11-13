package com.brik.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brik.chat.android.R;
import com.brik.chat.android.XMPPClient;
import com.brik.chat.entry.IMessage;
import com.brik.chat.view.PlayAudioButton;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.XMPPError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfengchen on 15/11/13.
 */
public class IMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ME_TEXT = 0x001;
    public static final int OTHER_TEXT = 0x002;
    public static final int ME_AUDIO = 0x011;
    public static final int OTHER_AUDIO = 0x012;
    public static final int ME_IMAGE = 0x021;
    public static final int OTHER_IMAGE = 0x022;
    public static final int ME_FILE = 0x031;
    public static final int OTHER_FILE = 0x032;


    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String messageType;

    public IMessageAdapter(Context context, String messageType) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.messageType = messageType;
    }

    private List<IMessage> items = new ArrayList<>();

    public void add(IMessage item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAllAtStart(List<IMessage> list) {
        items.addAll(0, list);
        notifyDataSetChanged();
    }

    public IMessage getItem(int position) {
        return items.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ME_TEXT: {
                View view = mLayoutInflater.inflate(R.layout.item_my_message_text, parent, false);
                return new MyTextViewHolder(view);
            }
            case OTHER_TEXT: {
                View view = mLayoutInflater.inflate(R.layout.item_other_message_text, parent, false);
                return new OtherTextViewHolder(view);
            }
        }
//        if(viewType==0) {
//            View view = mLayoutInflater.inflate(R.layout.item_my_message, parent, false);
//            return new MyViewHolder(view);
//        } else {
//            View view = mLayoutInflater.inflate(R.layout.item_other_message, parent, false);
//            return new OtherViewHolder(view);
//        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        int viewType = getItemViewType(position);
        BaseViewHolder vh = (BaseViewHolder) holder;
        vh.bindView(getItem(position));
//        if(viewType==0) {
//            MyViewHolder vh = (MyViewHolder) holder;
//            vh.bindView(getItem(position));
//        } else {
//            OtherViewHolder vh = (OtherViewHolder) holder;
//            vh.bindView(getItem(position));
//        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        //0 me 1 order
        IMessage mw = items.get(position);
        Log.d("Message Adapter", "from user "+mw.getFromUser());
        boolean isMe = XMPPClient.getInstance().getUser().equals(mw.getFromUser());
        Log.d("Message Adapter", "pos "+position+" isMe? "+ isMe);
        switch (mw.getCustomType()) {
            case IMessage.CUSTOM_TYPE_TEXT:
                return isMe?ME_TEXT:OTHER_TEXT;
            case IMessage.CUSTOM_TYPE_AUDIO:
                return isMe?ME_AUDIO:OTHER_AUDIO;
            case IMessage.CUSTOM_TYPE_IMAGE:
                return isMe?ME_IMAGE:OTHER_IMAGE;
            case IMessage.CUSTOM_TYPE_FILE:
                return isMe?ME_FILE:OTHER_FILE;
        }
        return 0;
    }

};

class BaseViewHolder extends RecyclerView.ViewHolder {

    ImageView headView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        headView = (ImageView) itemView.findViewById(R.id.item_head);
    }

    void bindView(IMessage mw) {

    }
}

class TextBaseViewHolder extends BaseViewHolder {
    TextView textView;
    public TextBaseViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.item_text);
    }
}

class MyTextViewHolder extends TextBaseViewHolder {

    public MyTextViewHolder(View itemView) {
        super(itemView);
    }

    void bindView(IMessage mw) {
        textView.setText(mw.getBody());
    }
}

class OtherTextViewHolder extends TextBaseViewHolder {

    public OtherTextViewHolder(View itemView) {
        super(itemView);
    }

    void bindView(IMessage mw) {
        textView.setText(mw.getBody());
    }

}

class AudioBaseViewHolder extends BaseViewHolder {
    PlayAudioButton playAudioButton;
    public AudioBaseViewHolder(View itemView) {
        super(itemView);
        playAudioButton = (PlayAudioButton) itemView.findViewById(R.id.item_play_audio_btn);
    }

    void initPlayAudioBtn(String path, long timeLength) {
        playAudioButton.setAudioFilePath(path);
        //设置最大／小宽度
        int w = 200;//没秒的长度
        int seconds = (int) (timeLength / 1000);
        w *= seconds;
        Log.d("path audio s", "" + seconds);
        Log.d("path audio w", ""+w);
        if(w<=200) {
            w = 200;
        }
        if(w>=1000) {
            w = 1000;
        }
        playAudioButton.setPlayButtonWidth(w);
    }
}

//    Message m = mw.getMessage();
//    String cType = (String) m.getProperty("c-type");
//    if(cType!=null) {
//        if(cType.equals("audio")) {
//            //是语音
//            showPlayAudioLayout();
//            String audioPath = (String) m.getProperty("file-path");
////                    String audioUrl = (String) m.getProperty("audio-url");
////                    Long audioSize = (Long) m.getProperty("file-size");
//            Long timeLength = (Long) m.getProperty("time-length");
//            initPlayAudioBtn(audioPath, timeLength==null?0:timeLength);
//        }
//    } else {
//        showText();
//        textView.setText(m.getBody());
//    }
//    XMPPError error = m.getError();
//    if(error!=null) {
//        if(error.getCode()==-1) {
//            //重试
//        }
//    }


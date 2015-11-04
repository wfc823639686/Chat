package com.brik.chat.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.brik.chat.android.R;
import com.brik.chat.common.MediaManager;

import java.io.File;
import java.io.IOException;

public class PlayAudioButton extends Button {

    private boolean playing;

    public PlayAudioButton(Context context) {
        super(context);
        init();
    }

    public PlayAudioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PlayAudioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setText("播放");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playing) {
                    stopPlay();
                } else {
                    startPlay();
                }
            }
        });
    }

    void startPlay() {
        setText("停止");
        MediaManager.playSound(mFileName, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                MediaManager.release();
            }
        });
        playing = true;
    }

    void stopPlay() {
        setText("播放");
        MediaManager.stop();
        MediaManager.release();
        playing = false;
    }

    public void setAudioFilePath(String path) {
        this.mFileName = path;
    }

    private String mFileName = null;

}
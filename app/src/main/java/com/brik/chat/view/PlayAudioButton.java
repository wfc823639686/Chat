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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.brik.chat.android.R;
import com.brik.chat.common.HttpClient;
import com.brik.chat.common.MediaManager;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;

import java.io.File;
import java.io.IOException;

public class PlayAudioButton extends ImageButton {

    public static final int PLAY = 0x01;
    public static final int STOP = 0x02;
    public static final int LOADING = 0x03;
    public static final int LOAD_FAIL = 0x04;

    private boolean playing;

    File audio;

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
        progress(PLAY);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playing) {
                    stopPlay();
                } else {
                    loadAudio();
                }
            }
        });
    }

    void loadAudio() {
        audio = new File(mFileName);
        if(!audio.exists()) {
            //开始下载
            progress(LOADING);
            HttpClient.getInstance().get(mFileUrl, new BinaryHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] binaryData) {
                    Log.d("loadFile", "onSuccess");
                    try {
                        FileUtils.writeByteArrayToFile(audio, binaryData);
                        startPlay();
                    } catch (IOException e) {
                        e.printStackTrace();
                        progress(LOAD_FAIL);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] binaryData, Throwable error) {
                    Log.d("loadFile", "onFailure");
                    progress(LOAD_FAIL);
                }
            });
        } else {
            startPlay();
        }
    }



    void startPlay() {
        progress(STOP);
        MediaManager.playSound(mFileName, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                MediaManager.release();
                progress(PLAY);
            }
        });
        playing = true;
    }

    void stopPlay() {
        progress(PLAY);
        MediaManager.stop();
        MediaManager.release();
        playing = false;
    }

    void progress(int p) {

    }

    public void setAudioFilePath(String path) {
        this.mFileName = path;
    }

    public void setAudioFileUrl(String url) {
        this.mFileUrl = url;
    }

    private String mFileName = null;
    private String mFileUrl = null;

    public void setPlayButtonWidth(int w) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = w;
        setLayoutParams(lp);
    }

}
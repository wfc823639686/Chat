package com.brik.chat.common;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by wangfengchen on 15/11/4.
 */
public class MediaManager {

    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    /**
     * 播放音乐
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(String filePath,MediaPlayer.OnCompletionListener onCompletionListener) {
        Log.d("MediaManager", "playSound "+filePath);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void stop() {
        Log.d("MediaManager", "stop");
        if (mMediaPlayer != null) { //正在播放的时候
            mMediaPlayer.stop();
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        Log.d("MediaManager", "release");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}

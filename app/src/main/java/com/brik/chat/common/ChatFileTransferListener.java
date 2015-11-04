package com.brik.chat.common;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ChatFileTransferListener implements Runnable {

    Context mContext;

    HttpClient httpClient;

    public ChatFileTransferListener(Context context, HttpClient c) {
        this.mContext = context;
        httpClient = c;
    }

    BlockingQueue<Map> blockingQueue = new LinkedBlockingDeque<Map>();

    public void put(Map msg) throws InterruptedException {
        blockingQueue.put(msg);
    }

    public Map take() throws InterruptedException {
        return blockingQueue.take();
    }

    boolean check() {
        return false;
    }

    @Override
    public void run() {
        task();
    }

    void task() {
        while (true) {
            try {
                Map msg = take();//阻塞
                String fileUrl = (String) msg.get("fileurl");
                final String filePath = (String) msg.get("filepath");
                Log.d("ChatFileTransfer", "fileUrl " + fileUrl);
                httpClient.get(fileUrl, new BinaryHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] binaryData) {
                        Log.d("loadFile", "onSuccess");
                        try {
                            FileUtils.writeByteArrayToFile(new File(filePath), binaryData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] binaryData, Throwable error) {
                        Log.d("loadFile", "onFailure");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("ChatFileTransfer", "stop");
                return;
            }
        }
    }
}
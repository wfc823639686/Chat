package com.brik.android.chat.service;

import com.brik.android.chat.Listener;

/**
 * Created by wangfengchen on 15/6/26.
 */
public interface ConnectListener extends Listener {

    void onSuccess();

    void onError(Throwable throwable);
}
